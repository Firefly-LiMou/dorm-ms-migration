package com.dorm.common.enums;

import lombok.Getter;

/**
 * 报修状态：0-待处理，1-处理中，2-已完成
 */
@Getter
public enum RepairStatusEnum {

    /** 待处理 */
    PENDING(0, "待处理"),
    /** 处理中 */
    PROCESSING(1, "处理中"),
    /** 已完成 */
    COMPLETED(2, "已完成");

    private final Integer value;

    /** 状态中文文案 */
    private final String desc;

    RepairStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据状态值获取中文文案
     *
     * @param value 状态值
     * @return 中文文案，未知值返回 null
     */
    public static String descOf(Integer value) {
        if (value == null) {
            return null;
        }
        for (RepairStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status.desc;
            }
        }
        return null;
    }
}
