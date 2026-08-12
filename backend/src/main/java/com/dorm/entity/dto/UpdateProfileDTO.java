package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改个人信息请求参数
 */
@Data
public class UpdateProfileDTO {

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 1, max = 32, message = "姓名长度为1-32位")
    private String realName;

    private String gender;

    @Size(max = 11, message = "手机号长度不超过11位")
    private String phone;
}
