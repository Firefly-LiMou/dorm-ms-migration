package com.dorm.service;

import com.dorm.common.exception.BusinessException;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.vo.LoginVO;
import com.dorm.entity.vo.UserVO;

/**
 * 认证服务：登录、登出、当前用户信息
 */
public interface AuthService {

    /**
     * 登录：校验账号密码与状态，签发 Token 并写入会话
     *
     * @param dto 登录入参
     * @return Token + 用户信息
     * @throws BusinessException 1001 账号或密码错误 / 1002 账号已禁用
     */
    LoginVO login(LoginDTO dto);

    /**
     * 登出：注销当前 Token
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息（不含密码）
     */
    UserVO profile();
}
