package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 学生提交报修入参
 */
@Data
public class RepairSubmitDTO {

    /** 报修类型：0-水电故障，1-家具损坏，2-网络问题，3-其他 */
    @NotNull(message = "报修类型不能为空")
    @Min(value = 0, message = "报修类型不合法")
    @Max(value = 3, message = "报修类型不合法")
    private Integer repairType;

    /** 报修内容详情 */
    @NotBlank(message = "报修内容不能为空")
    @Size(min = 1, max = 500, message = "报修内容长度需在1-500字之间")
    private String content;

    /** 联系电话（11 位手机号） */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "联系电话必须为11位数字")
    private String contactPhone;
}
