package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 床位批量初始化请求参数
 */
@Data
public class BedBatchDTO {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "床位数量不能为空")
    @Min(value = 1, message = "床位数量最小为1")
    @Max(value = 12, message = "床位数量最大为12")
    private Integer count;
}
