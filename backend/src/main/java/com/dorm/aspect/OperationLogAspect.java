package com.dorm.aspect;

import com.dorm.common.annotation.OperationLog;
import com.dorm.common.utils.LoginUserUtil;
import com.dorm.common.utils.OperationLogContext;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.mapper.SysOperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志切面：环绕 @OperationLog 标注的方法，自动记录操作人、模块、类型、操作详情（操作+变更+结果）、IP
 * 操作详情格式：{操作描述} 变更: {旧值→新值} 结果: {成功/失败}
 * 变更内容由业务层通过 OperationLogContext 提供；日志写入失败不影响主流程，仅记录错误日志
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    /** 参数内容最大长度，超出截断 */
    private static final int MAX_CONTENT_LENGTH = 500;

    private final SysOperationLogMapper operationLogMapper;

    public OperationLogAspect(SysOperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            saveLog(operationLog, null, System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable e) {
            saveLog(operationLog, e.getMessage(), System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    private void saveLog(OperationLog operationLog, String errorMsg, long costMs) {
        try {
            String change = OperationLogContext.getAndClear();
            SysOperationLogDO logDO = new SysOperationLogDO();
            logDO.setOperatorId(LoginUserUtil.getUserId());
            logDO.setOperatorName(LoginUserUtil.getRealName());
            logDO.setModule(operationLog.module());
            logDO.setOperationType(operationLog.type().getValue());
            logDO.setContent(buildContent(operationLog.desc(), change, errorMsg));
            logDO.setIpAddress(getClientIp());
            operationLogMapper.insert(logDO);
            log.info("操作日志已记录 module={} type={} 耗时={}ms", operationLog.module(), operationLog.type(), costMs);
        } catch (Exception e) {
            log.error("操作日志记录失败: {}", e.getMessage(), e);
        } finally {
            OperationLogContext.clear();
        }
    }

    private String buildContent(String desc, String change, String errorMsg) {
        StringBuilder sb = new StringBuilder();
        if (desc != null && !desc.isEmpty()) {
            sb.append(desc);
        }
        if (change != null && !change.isEmpty()) {
            sb.append(" 变更: ").append(change);
        }
        if (errorMsg != null && !errorMsg.isEmpty()) {
            sb.append(" 结果: 失败(").append(errorMsg).append(")");
        } else {
            sb.append(" 结果: 成功");
        }
        String full = sb.toString();
        return full.length() > MAX_CONTENT_LENGTH ? full.substring(0, MAX_CONTENT_LENGTH) : full;
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getRemoteAddr();
    }
}
