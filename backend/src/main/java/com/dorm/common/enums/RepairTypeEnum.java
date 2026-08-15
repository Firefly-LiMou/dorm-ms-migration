package com.dorm.common.enums;

import lombok.Getter;

/**
 * 报修类型：0-水电故障，1-家具损坏，2-网络问题，3-其他
 */
@Getter
public enum RepairTypeEnum {

    /** 水电故障 */
    ELECTRICITY(0, "水电故障"),
    /** 家具损坏 */
    FURNITURE(1, "家具损坏"),
    /** 网络问题 */
    NETWORK(2, "网络问题"),
    /** 其他 */
    OTHER(3, "其他");

    private final Integer value;

    /** 类型中文文案 */
    private final String desc;

    RepairTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
