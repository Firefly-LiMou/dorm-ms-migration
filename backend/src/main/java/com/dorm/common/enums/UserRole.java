package com.dorm.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {

    STUDENT("student", "学生"),
    ADMIN("admin", "管理员");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
