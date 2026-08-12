package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 入住分配请求参数
 */
@Data
public class CheckinDTO {

    @NotNull(message = "学生ID不能为空")
    private Long userId;

    @NotNull(message = "床位ID不能为空")
    private Long bedId;

    @Size(max = 255, message = "备注长度不超过255字")
    private String remark;
}
