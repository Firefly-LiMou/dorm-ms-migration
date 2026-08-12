package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.result.Result;
import com.dorm.entity.query.LogPageQuery;
import com.dorm.service.LogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 操作日志控制器（admin）
 */
@SaCheckRole("admin")
@Validated
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogService logService;

    /** 分页查询操作日志 */
    @GetMapping("/page")
    public Result<?> pageLogs(@Valid LogPageQuery query) {
        return Result.success(logService.pageLogs(query));
    }
}
