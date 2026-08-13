package com.dorm.controller;

import com.dorm.common.result.Result;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.vo.LoginVO;
import com.dorm.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 认证控制器：登录 / 登出
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }
}
