package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 学生提交报修请求参数
 */
@Data
public class RepairSubmitDTO {

    @NotNull(message = "报修类型不能为空")
    private Integer repairType;

    @NotBlank(message = "报修内容不能为空")
    @Size(min = 1, max = 500, message = "报修内容长度为1-500字")
    private String content;

    @NotBlank(message = "联系电话不能为空")
    @Size(min = 11, max = 11, message = "联系电话为11位")
    private String contactPhone;
}
