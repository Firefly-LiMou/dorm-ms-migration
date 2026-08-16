package com.dorm.entity.vo;

import lombok.Data;

/**
 * 楼栋下拉选项展示对象
 */
@Data
public class BuildingVO {

    /** 楼栋主键ID */
    private Long buildingId;

    /** 楼栋编号 */
    private String buildingNo;

    /** 楼栋名称 */
    private String buildingName;
}
