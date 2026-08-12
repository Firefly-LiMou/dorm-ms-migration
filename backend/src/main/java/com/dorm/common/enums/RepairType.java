package com.dorm.common.enums;

/**
 * 报修类型枚举
 */
public enum RepairType {

    WATER_ELECTRICITY(0, "水电故障"),
    FURNITURE(1, "家具损坏"),
    NETWORK(2, "网络问题"),
    OTHER(3, "其他");

    private final int code;
    private final String desc;

    RepairType(int code, String desc) {
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
