package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.entity.query.LogPageQuery;

/**
 * 操作日志服务接口
 */
public interface LogService {

    /** 分页查询操作日志 */
    PageVO<SysOperationLogDO> pageLogs(LogPageQuery query);
}
