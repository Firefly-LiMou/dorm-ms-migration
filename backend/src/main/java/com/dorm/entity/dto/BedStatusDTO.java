package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 床位状态更新请求参数
 */
@Data
public class BedStatusDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
