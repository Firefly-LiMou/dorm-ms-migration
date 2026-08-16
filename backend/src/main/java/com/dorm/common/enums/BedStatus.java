package com.dorm.common.enums;

/**
 * 床位入住状态枚举
 */
public enum BedStatus {

    FREE(0, "空闲"),
    OCCUPIED(1, "已入住");

    private final int code;
    private final String desc;

    BedStatus(int code, String desc) {
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
