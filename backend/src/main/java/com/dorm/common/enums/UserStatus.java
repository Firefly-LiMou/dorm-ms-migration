package com.dorm.common.enums;

/**
 * 用户账号状态枚举
 */
public enum UserStatus {

    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    private final int code;
    private final String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
