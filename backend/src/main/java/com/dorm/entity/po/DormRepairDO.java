package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修单表：全生命周期记录
 */
@Data
@TableName("dorm_repair")
public class DormRepairDO {

    /** 报修单ID */
    @TableId(type = IdType.AUTO)
    private Long repairId;

    /** 报修学生ID */
    private Long userId;

    /** 报修房间ID（冗余字段，快速定位楼栋） */
    private Long roomId;

    /** 报修类型：0-水电故障，1-家具损坏，2-网络问题，3-其他 */
    private Integer repairType;

    /** 报修内容详情 */
    private String content;

    /** 联系电话 */
    private String contactPhone;

    /** 报修状态：0-待处理，1-处理中，2-已完成 */
    private Integer status;

    /** 处理结果 */
    private String handleResult;

    /** 处理完成时间 */
    private LocalDateTime handleTime;

    /** 处理人ID（管理员） */
    private Long handlerId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
