package com.dorm.entity.vo;

import lombok.Data;

/**
 * 登录响应：Token + 用户信息
 */
@Data
public class LoginVO {

    /** 登录凭证，有效期 1 天 */
    private String token;

    /** 当前登录用户信息 */
    private UserVO userInfo;
}
