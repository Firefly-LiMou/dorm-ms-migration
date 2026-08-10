package com.dorm.common.utils;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 当前登录用户工具类：登录时向会话写入 role / username / realName，此处统一读取
 */
public class LoginUserUtil {

    private LoginUserUtil() {
    }

    /** 当前登录用户 ID */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /** 当前登录用户角色：student / admin */
    public static String getRole() {
        return StpUtil.getSession().getString("role");
    }

    /** 当前登录用户名 */
    public static String getUsername() {
        return StpUtil.getSession().getString("username");
    }

    /** 当前登录用户真实姓名 */
    public static String getRealName() {
        return StpUtil.getSession().getString("realName");
    }
}
