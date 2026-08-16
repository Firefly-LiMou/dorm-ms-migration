package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.page.PageVO;
import com.dorm.common.result.Result;
import com.dorm.entity.po.SysOperationLogDO;
import com.dorm.entity.query.LogQuery;
import com.dorm.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志模块（管理员）：查询 AOP 自动记录的增删改操作日志
 */
@RestController
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * 分页查询操作日志（管理员）
     */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<PageVO<SysOperationLogDO>> page(LogQuery query) {
        return Result.success(logService.page(query));
    }
}
