package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.CheckinDTO;
import com.dorm.entity.dto.CheckoutDTO;
import com.dorm.entity.query.CheckinPageQuery;
import com.dorm.service.CheckinService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 入住管理控制器
 */
@Validated
@RestController
@RequestMapping("/checkin")
public class CheckinController {

    @Resource
    private CheckinService checkinService;

    /** 管理员入住分配 */
    @SaCheckRole("admin")
    @OperationLog(module = "入住管理", type = "新增", desc = "入住分配")
    @PostMapping
    public Result<Void> checkin(@Valid @RequestBody CheckinDTO dto) {
        checkinService.checkin(dto);
        return Result.success();
    }

    /** 管理员办理退宿 */
    @SaCheckRole("admin")
    @OperationLog(module = "入住管理", type = "修改", desc = "办理退宿")
    @PostMapping("/{checkinId}/checkout")
    public Result<Void> checkout(@PathVariable Long checkinId, @RequestBody CheckoutDTO dto) {
        checkinService.checkout(checkinId, dto);
        return Result.success();
    }

    /** 管理员分页查询入住记录 */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<?> pageCheckins(@Valid CheckinPageQuery query) {
        return Result.success(checkinService.pageCheckins(query));
    }

    /** 学生查询本人入住记录 */
    @GetMapping("/my")
    public Result<?> myCheckins(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(checkinService.pageMyCheckins(pageNum, pageSize));
    }
}
