package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.entity.query.LogQuery;
import com.dorm.mapper.SysOperationLogMapper;
import com.dorm.service.LogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 操作日志服务实现
 */
@Service
public class LogServiceImpl implements LogService {

    private final SysOperationLogMapper logMapper;

    public LogServiceImpl(SysOperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Override
    public PageVO<SysOperationLogDO> page(LogQuery query) {
        Page<SysOperationLogDO> page = logMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()),
                Wrappers.<SysOperationLogDO>lambdaQuery()
                        .like(StringUtils.hasText(query.getOperatorName()), SysOperationLogDO::getOperatorName, query.getOperatorName())
                        .eq(StringUtils.hasText(query.getModule()), SysOperationLogDO::getModule, query.getModule())
                        .eq(StringUtils.hasText(query.getOperationType()), SysOperationLogDO::getOperationType, query.getOperationType())
                        .ge(query.getStartTime() != null, SysOperationLogDO::getCreateTime, query.getStartTime())
                        .le(query.getEndTime() != null, SysOperationLogDO::getCreateTime, query.getEndTime())
                        .orderByDesc(SysOperationLogDO::getCreateTime));
        return PageVO.of(page);
    }
}
