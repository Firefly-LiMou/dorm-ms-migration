package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 入住记录分页查询参数（管理员全局查询）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckinPageQuery extends PageQuery {

    /** 楼栋ID */
    private Long buildingId;

    /** 学号精确 */
    private String username;

    /** 状态：1-入住中，2-已退宿 */
    private Integer status;
}
