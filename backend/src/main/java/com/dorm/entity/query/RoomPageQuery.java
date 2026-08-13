package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 房间分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoomPageQuery extends PageQuery {

    /** 所属楼栋ID */
    private Long buildingId;

    /** 房间编号模糊 */
    private String roomNo;
}
