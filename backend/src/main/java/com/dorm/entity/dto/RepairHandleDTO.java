package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 管理员处理报修请求参数
 */
@Data
public class RepairHandleDTO {

    @NotNull(message = "处理状态不能为空")
    private Integer status;

    @Size(max = 500, message = "处理结果长度不超过500字")
    private String handleResult;
}
