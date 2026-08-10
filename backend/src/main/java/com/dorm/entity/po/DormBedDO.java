package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 床位信息表：关联所属房间，记录入住状态
 */
@Data
@TableName("dorm_bed")
public class DormBedDO {

    /** 床位主键ID */
    @TableId(type = IdType.AUTO)
    private Long bedId;

    /** 所属房间ID */
    private Long roomId;

    /** 床位编号（同一房间内唯一） */
    private String bedNo;

    /** 入住状态：0-空闲，1-已入住 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
