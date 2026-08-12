package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报修单分页查询参数（管理员）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RepairPageQuery extends PageQuery {

    /** 状态：0-待处理，1-处理中，2-已完成 */
    private Integer status;

    /** 楼栋ID */
    private Long buildingId;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;
}
