package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.RepairHandleDTO;
import com.dorm.entity.dto.RepairSubmitDTO;
import com.dorm.entity.query.RepairPageQuery;
import com.dorm.service.RepairService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 报修管理控制器
 */
@Validated
@RestController
@RequestMapping("/repair")
public class RepairController {

    @Resource
    private RepairService repairService;

    /** 学生提交报修 */
    @SaCheckRole("student")
    @PostMapping
    public Result<Void> submitRepair(@Valid @RequestBody RepairSubmitDTO dto) {
        repairService.submitRepair(dto);
        return Result.success();
    }

    /** 管理员分页查询报修单 */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<?> pageRepairs(@Valid RepairPageQuery query) {
        return Result.success(repairService.pageRepairs(query));
    }

    /** 学生分页查询本人报修 */
    @GetMapping("/my")
    public Result<?> myRepairs(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Integer status) {
        return Result.success(repairService.pageMyRepairs(pageNum, pageSize, status));
    }

    /** 管理员处理报修 */
    @SaCheckRole("admin")
    @OperationLog(module = "报修管理", type = "修改", desc = "处理报修")
    @PutMapping("/{repairId}/handle")
    public Result<Void> handleRepair(@PathVariable Long repairId, @Valid @RequestBody RepairHandleDTO dto) {
        repairService.handleRepair(repairId, dto);
        return Result.success();
    }
}
