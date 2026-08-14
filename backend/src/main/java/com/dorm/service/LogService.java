package com.dorm.service;

import com.dorm.common.page.PageVO;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.entity.query.LogQuery;

/**
 * 操作日志服务（管理员）
 */
public interface LogService {

    /**
     * 分页查询操作日志
     *
     * @param query 查询条件（操作人模糊 / 模块精确 / 操作类型精确 / 时间范围）
     * @return 分页结果
     */
    PageVO<SysOperationLogDO> page(LogQuery query);
}
