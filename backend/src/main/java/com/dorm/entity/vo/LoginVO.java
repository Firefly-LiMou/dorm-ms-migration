package com.dorm.entity.vo;

import lombok.Data;

/**
 * 登录响应：Token + 用户信息
 */
@Data
public class LoginVO {

    private String token;

    private UserInfoVO userInfo;

    public static LoginVO of(String token, UserInfoVO userInfo) {
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(userInfo);
        return vo;
    }
}
