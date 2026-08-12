package com.dorm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.entity.query.LogPageQuery;
import com.dorm.mapper.SysOperationLogMapper;
import com.dorm.service.LogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 操作日志服务实现
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private SysOperationLogMapper logMapper;

    @Override
    public PageVO<SysOperationLogDO> pageLogs(LogPageQuery query) {
        LambdaQueryWrapper<SysOperationLogDO> wrapper = Wrappers.<SysOperationLogDO>lambdaQuery()
                .like(StrUtil.isNotBlank(query.getOperatorName()), SysOperationLogDO::getOperatorName, query.getOperatorName())
                .eq(StrUtil.isNotBlank(query.getModule()), SysOperationLogDO::getModule, query.getModule())
                .eq(StrUtil.isNotBlank(query.getOperationType()), SysOperationLogDO::getOperationType, query.getOperationType())
                .ge(StrUtil.isNotBlank(query.getStartTime()), SysOperationLogDO::getCreateTime, query.getStartTime())
                .le(StrUtil.isNotBlank(query.getEndTime()), SysOperationLogDO::getCreateTime, query.getEndTime())
                .orderByDesc(SysOperationLogDO::getCreateTime);
        Page<SysOperationLogDO> page = logMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageVO.of(page);
    }
}
