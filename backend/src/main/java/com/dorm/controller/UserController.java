package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.result.Result;
import com.dorm.entity.query.UserPageQuery;
import com.dorm.entity.vo.UserInfoVO;
import com.dorm.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户控制器：个人信息 + 管理员学生列表（本次最小集）
 */
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /** 获取当前登录用户信息 */
    @GetMapping("/profile")
    public Result<UserInfoVO> profile() {
        return Result.success(userService.getProfile());
    }

    /** 管理员分页查询学生账号（入住分配选人用） */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<?> pageStudents(@Valid UserPageQuery query) {
        return Result.success(userService.pageStudents(query));
    }
}
