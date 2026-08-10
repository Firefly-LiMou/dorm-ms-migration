package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入住记录表：承载学生与床位的关联关系，支持历史追溯
 */
@Data
@TableName("dorm_checkin")
public class DormCheckinDO {

    /** 入住记录ID */
    @TableId(type = IdType.AUTO)
    private Long checkinId;

    /** 学生用户ID */
    private Long userId;

    /** 入住床位ID */
    private Long bedId;

    /** 入住时间 */
    private LocalDateTime checkinTime;

    /** 退宿时间（入住中为 NULL） */
    private LocalDateTime checkoutTime;

    /** 记录状态：1-入住中，2-已退宿 */
    private Integer status;

    /** 办理人ID（管理员） */
    private Long operatorId;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
