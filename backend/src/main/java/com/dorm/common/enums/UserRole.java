package com.dorm.common.enums;

import lombok.Getter;

/**
 * 用户角色：统一用户体系，学生与管理员共用用户表
 */
@Getter
public enum UserRole {

    /** 学生 */
    STUDENT("student"),
    /** 管理员 */
    ADMIN("admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
