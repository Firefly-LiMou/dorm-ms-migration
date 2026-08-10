package com.dorm.aspect;

import cn.hutool.json.JSONUtil;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.utils.LoginUserUtil;
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
 * 操作日志切面：环绕 @OperationLog 标注的方法，自动记录操作人、模块、类型、内容、IP
 * 日志写入失败不影响主流程，仅记录错误日志
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
            saveLog(operationLog, serializeParams(joinPoint.getArgs()), null, System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable e) {
            saveLog(operationLog, serializeParams(joinPoint.getArgs()), e.getMessage(), System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    private void saveLog(OperationLog operationLog, String content, String errorMsg, long costMs) {
        try {
            SysOperationLogDO logDO = new SysOperationLogDO();
            logDO.setOperatorId(LoginUserUtil.getUserId());
            logDO.setOperatorName(LoginUserUtil.getRealName());
            logDO.setModule(operationLog.module());
            logDO.setOperationType(operationLog.type());
            logDO.setContent(buildContent(operationLog.desc(), content, errorMsg));
            logDO.setIpAddress(getClientIp());
            operationLogMapper.insert(logDO);
            log.info("操作日志已记录 module={} type={} 耗时={}ms", operationLog.module(), operationLog.type(), costMs);
        } catch (Exception e) {
            log.error("操作日志记录失败: {}", e.getMessage(), e);
        }
    }

    private String buildContent(String desc, String content, String errorMsg) {
        StringBuilder sb = new StringBuilder();
        if (desc != null && !desc.isEmpty()) {
            sb.append(desc);
        }
        if (content != null && !content.isEmpty()) {
            sb.append(" 参数: ").append(content);
        }
        if (errorMsg != null && !errorMsg.isEmpty()) {
            sb.append(" 结果: 失败(").append(errorMsg).append(")");
        } else {
            sb.append(" 结果: 成功");
        }
        String full = sb.toString();
        return full.length() > MAX_CONTENT_LENGTH ? full.substring(0, MAX_CONTENT_LENGTH) : full;
    }

    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return JSONUtil.toJsonStr(args);
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
