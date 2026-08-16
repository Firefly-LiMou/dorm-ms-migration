package com.dorm.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入住记录视图对象（含冗余联查字段）
 */
@Data
public class CheckinVO {

    private Long checkinId;

    private Long userId;

    private String username;

    private String realName;

    private String buildingName;

    private String roomNo;

    private String bedNo;

    private LocalDateTime checkinTime;

    private LocalDateTime checkoutTime;

    private Integer status;

    private String operatorName;

    private String remark;
}
