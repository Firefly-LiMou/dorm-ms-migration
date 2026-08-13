package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.BedBatchDTO;
import com.dorm.entity.dto.BedStatusDTO;
import com.dorm.entity.query.BedPageQuery;
import com.dorm.service.BedService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 床位管理控制器（admin）
 */
@SaCheckRole("admin")
@Validated
@RestController
@RequestMapping("/bed")
public class BedController {

    @Resource
    private BedService bedService;

    /** 分页查询床位 */
    @GetMapping("/page")
    public Result<?> pageBeds(@Valid BedPageQuery query) {
        return Result.success(bedService.pageBeds(query));
    }

    /** 批量初始化房间床位 */
    @OperationLog(module = "床位管理", type = "新增", desc = "批量初始化床位")
    @PostMapping("/batch")
    public Result<Void> batchCreate(@Valid @RequestBody BedBatchDTO dto) {
        bedService.batchCreate(dto);
        return Result.success();
    }

    /** 手动更新床位状态（仅数据纠错） */
    @OperationLog(module = "床位管理", type = "修改", desc = "更新床位状态")
    @PutMapping("/{bedId}/status")
    public Result<Void> updateStatus(@PathVariable Long bedId, @Valid @RequestBody BedStatusDTO dto) {
        bedService.updateStatus(bedId, dto);
        return Result.success();
    }

    /** 删除床位（仅空闲床位可删除） */
    @OperationLog(module = "床位管理", type = "删除", desc = "删除床位")
    @DeleteMapping("/{bedId}")
    public Result<Void> deleteBed(@PathVariable Long bedId) {
        bedService.deleteBed(bedId);
        return Result.success();
    }
}
