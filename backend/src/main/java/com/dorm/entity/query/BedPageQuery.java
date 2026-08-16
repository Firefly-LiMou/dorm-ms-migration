package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 床位分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BedPageQuery extends PageQuery {

    /** 所属房间ID */
    private Long roomId;

    /** 状态：0-空闲，1-已入住 */
    private Integer status;
}
