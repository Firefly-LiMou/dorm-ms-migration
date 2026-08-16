package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 退宿请求参数
 */
@Data
public class CheckoutDTO {

    @Size(max = 255, message = "备注长度不超过255字")
    private String remark;
}
