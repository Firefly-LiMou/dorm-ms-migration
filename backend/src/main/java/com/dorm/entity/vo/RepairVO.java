package com.dorm.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修单视图对象（含冗余联查字段 + isOverdue 超时标识）
 */
@Data
public class RepairVO {

    private Long repairId;

    private Long userId;

    private String username;

    private String realName;

    private String buildingName;

    private String roomNo;

    private Integer repairType;

    private String content;

    private String contactPhone;

    private Integer status;

    /** 超时标识：待处理且提交超过 24 小时为 true，由服务端计算 */
    private Boolean isOverdue;

    private String handleResult;

    private LocalDateTime handleTime;

    private String handlerName;

    private LocalDateTime createTime;
}
