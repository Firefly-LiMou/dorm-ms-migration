package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 管理员编辑学生账号请求参数
 */
@Data
public class UpdateUserDTO {

    @Size(min = 1, max = 32, message = "姓名长度为1-32位")
    private String realName;

    private String gender;

    @Size(max = 11, message = "手机号长度不超过11位")
    private String phone;

    private Integer status;
}
