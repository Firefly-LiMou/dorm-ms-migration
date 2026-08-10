package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋信息表
 */
@Data
@TableName("dorm_building")
public class DormBuildingDO {

    /** 楼栋主键ID */
    @TableId(type = IdType.AUTO)
    private Long buildingId;

    /** 楼栋编号（唯一） */
    private String buildingNo;

    /** 楼栋名称 */
    private String buildingName;

    /** 总楼层数 */
    private Integer floorCount;

    /** 所属区域 */
    private String area;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
