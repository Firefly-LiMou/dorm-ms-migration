package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间信息表：关联所属楼栋
 */
@Data
@TableName("dorm_room")
public class DormRoomDO {

    /** 房间主键ID */
    @TableId(type = IdType.AUTO)
    private Long roomId;

    /** 所属楼栋ID */
    private Long buildingId;

    /** 房间编号（同一楼栋内唯一） */
    private String roomNo;

    /** 所在楼层 */
    private Integer floor;

    /** 床位数 */
    private Integer bedCount;

    /** 房间类型 */
    private String roomType;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
