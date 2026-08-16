package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogQuery extends PageQuery {

    /** 操作人姓名（模糊查询） */
    private String operatorName;

    /** 操作模块（精确匹配） */
    private String module;

    /** 操作类型（精确匹配）：新增 / 修改 / 删除 */
    private String operationType;

    /** 操作时间起 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** 操作时间止 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
