package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.enums.OperationType;
import com.dorm.common.page.PageVO;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.UserCreateDTO;
import com.dorm.entity.dto.UserUpdateDTO;
import com.dorm.entity.query.UserQuery;
import com.dorm.entity.vo.UserVO;
import com.dorm.service.AuthService;
import com.dorm.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块：当前用户信息 + 管理员学生账号管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * 获取当前登录用户信息（学生 / 管理员）
     */
    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.success(authService.profile());
    }

    /**
     * 分页查询学生账号（管理员）：按学号、姓名、状态筛选
     */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<PageVO<UserVO>> page(UserQuery query) {
        return Result.success(userService.page(query));
    }

    /**
     * 创建学生账号（管理员）：默认密码 123456，校验学号唯一
     */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = OperationType.ADD, desc = "创建学生账号")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated UserCreateDTO dto) {
        return Result.success(userService.create(dto));
    }

    /**
     * 编辑学生账号（管理员）：修改信息或禁用/启用
     */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = OperationType.UPDATE, desc = "编辑学生账号")
    @PutMapping("/{userId}")
    public Result<Void> update(@PathVariable Long userId, @RequestBody @Validated UserUpdateDTO dto) {
        userService.update(userId, dto);
        return Result.success();
    }

    /**
     * 重置学生密码（管理员）：重置为初始密码 123456
     */
    @SaCheckRole("admin")
    @OperationLog(module = "用户管理", type = OperationType.UPDATE, desc = "重置学生密码")
    @PutMapping("/{userId}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        userService.resetPassword(userId);
        return Result.success();
    }
}
