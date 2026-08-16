package com.dorm.common.enums;

import lombok.Getter;

/**
 * 操作日志类型：仅管理员写操作（增删改）由 AOP 切面记录
 */
@Getter
public enum OperationType {

    /** 新增操作 */
    ADD("新增"),
    /** 修改操作 */
    UPDATE("修改"),
    /** 删除操作 */
    DELETE("删除");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }
}
