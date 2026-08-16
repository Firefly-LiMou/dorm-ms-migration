package com.dorm.common.enums;

import lombok.Getter;

/**
 * 账号状态：1-正常，0-禁用
 */
@Getter
public enum UserStatus {

    /** 正常 */
    NORMAL(1),
    /** 禁用 */
    DISABLED(0);

    private final Integer value;

    UserStatus(Integer value) {
        this.value = value;
    }
}
