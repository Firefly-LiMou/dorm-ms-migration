package com.dorm.controller;

import com.dorm.common.result.Result;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.vo.LoginVO;
import com.dorm.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证模块：登录 / 登出
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登录：签发 Token，有效期 1 天
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 登出：注销当前 Token
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
