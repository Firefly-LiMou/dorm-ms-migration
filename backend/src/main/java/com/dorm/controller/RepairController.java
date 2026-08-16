package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.enums.OperationType;
import com.dorm.common.page.PageVO;
import com.dorm.common.result.Result;
import com.dorm.common.utils.LoginUserUtil;
import com.dorm.entity.dto.RepairHandleDTO;
import com.dorm.entity.dto.RepairSubmitDTO;
import com.dorm.entity.query.RepairPageQuery;
import com.dorm.entity.vo.RepairVO;
import com.dorm.service.RepairService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报修管理模块：学生提交/查询本人报修，管理员全局查询/处理报修
 */
@RestController
@RequestMapping("/repair")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    /**
     * 学生提交报修：无有效入住记录返回 4001
     */
    @SaCheckRole("student")
    @PostMapping
    public Result<Long> submit(@RequestBody @Validated RepairSubmitDTO dto) {
        return Result.success(repairService.submitRepair(dto, LoginUserUtil.getUserId()));
    }

    /**
     * 学生分页查询本人报修
     */
    @SaCheckRole("student")
    @GetMapping("/my")
    public Result<PageVO<RepairVO>> myRepairs(RepairPageQuery query) {
        return Result.success(repairService.pageQueryForStudent(LoginUserUtil.getUserId(), query));
    }

    /**
     * 管理员分页查询全部报修
     */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<PageVO<RepairVO>> page(RepairPageQuery query) {
        return Result.success(repairService.pageQueryForAdmin(query));
    }

    /**
     * 管理员处理报修：严格状态流转校验，非法流转返回 4002/4003
     */
    @SaCheckRole("admin")
    @OperationLog(module = "报修管理", type = OperationType.UPDATE, desc = "处理报修")
    @PutMapping("/{repairId}/handle")
    public Result<Void> handle(@PathVariable Long repairId, @RequestBody @Validated RepairHandleDTO dto) {
        repairService.handleRepair(repairId, dto.getStatus(), dto.getHandleResult(), LoginUserUtil.getUserId());
        return Result.success();
    }
}
