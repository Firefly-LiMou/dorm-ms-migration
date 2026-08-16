package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 报修分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RepairPageQuery extends PageQuery {

    /** 报修状态：0-待处理，1-处理中，2-已完成 */
    private Integer status;

    /** 楼栋ID（管理员筛选） */
    private Long buildingId;

    /** 提交时间起 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** 提交时间止 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
