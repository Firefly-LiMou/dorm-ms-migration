package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.ChangePasswordDTO;
import com.dorm.entity.dto.CreateUserDTO;
import com.dorm.entity.dto.UpdateProfileDTO;
import com.dorm.entity.dto.UpdateUserDTO;
import com.dorm.entity.query.UserPageQuery;
import com.dorm.entity.vo.UserInfoVO;
import com.dorm.service.UserService;
import org.springframework.validation.annotation.Validated;
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
 * 用户控制器：个人信息维护 + 管理员用户管理
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

    /** 修改个人信息 */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        userService.updateProfile(dto);
        return Result.success();
    }

    /** 修改登录密码 */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success();
    }

    /** 管理员分页查询学生账号 */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<?> pageUsers(@Valid UserPageQuery query) {
        return Result.success(userService.pageUsers(query));
    }

    /** 管理员创建学生账号 */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = "新增", desc = "创建学生账号")
    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody CreateUserDTO dto) {
        userService.createUser(dto);
        return Result.success();
    }

    /** 管理员编辑学生账号 */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = "修改", desc = "编辑学生账号")
    @PutMapping("/{userId}")
    public Result<Void> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDTO dto) {
        userService.updateUser(userId, dto);
        return Result.success();
    }

    /** 管理员重置学生密码 */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = "修改", desc = "重置学生密码")
    @PutMapping("/{userId}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        userService.resetPassword(userId);
        return Result.success();
    }
}
