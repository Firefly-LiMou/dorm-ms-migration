package com.dorm.common.enums;

/**
 * 入住记录状态枚举
 */
public enum CheckinStatus {

    CHECKED_IN(1, "入住中"),
    CHECKED_OUT(2, "已退宿");

    private final int code;
    private final String desc;

    CheckinStatus(int code, String desc) {
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
