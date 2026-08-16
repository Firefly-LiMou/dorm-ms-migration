package com.dorm.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修单展示对象：联查用户姓名、楼栋名称、房间号，并计算超时标识
 */
@Data
public class RepairVO {

    /** 报修单ID */
    private Long repairId;

    /** 报修学生ID */
    private Long userId;

    /** 学生学号 */
    private String username;

    /** 学生姓名 */
    private String realName;

    /** 楼栋名称 */
    private String buildingName;

    /** 房间编号 */
    private String roomNo;

    /** 报修类型：0-水电故障，1-家具损坏，2-网络问题，3-其他 */
    private Integer repairType;

    /** 报修内容详情 */
    private String content;

    /** 联系电话 */
    private String contactPhone;

    /** 报修状态：0-待处理，1-处理中，2-已完成 */
    private Integer status;

    /** 是否超时：status=0 且提交超过 24 小时为 true */
    private Boolean isOverdue;

    /** 处理结果 */
    private String handleResult;

    /** 处理完成时间 */
    private LocalDateTime handleTime;

    /** 处理人姓名 */
    private String handlerName;

    /** 提交时间 */
    private LocalDateTime createTime;
}
