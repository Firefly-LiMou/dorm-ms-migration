package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 楼栋分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuildingPageQuery extends PageQuery {

    /** 楼栋编号模糊 */
    private String buildingNo;

    /** 所属区域精确 */
    private String area;
}
