package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogPageQuery extends PageQuery {

    /** 操作人姓名模糊 */
    private String operatorName;

    /** 操作模块精确 */
    private String module;

    /** 操作类型精确 */
    private String operationType;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;
}
