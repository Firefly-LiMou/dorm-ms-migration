package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 管理员处理报修入参
 */
@Data
public class RepairHandleDTO {

    /** 流转目标状态：1-处理中，2-已完成 */
    @NotNull(message = "目标状态不能为空")
    @Min(value = 1, message = "目标状态不合法")
    @Max(value = 2, message = "目标状态不合法")
    private Integer status;

    /** 处理结果：status=2（已完成）时必填 */
    @Size(max = 500, message = "处理结果长度不能超过500字")
    private String handleResult;
}
