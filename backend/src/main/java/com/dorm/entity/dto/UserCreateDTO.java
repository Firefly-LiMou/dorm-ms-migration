package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 创建学生账号入参：密码不传，默认初始密码 123456，status 默认 1
 */
@Data
public class UserCreateDTO {

    /** 学号（唯一） */
    @NotBlank(message = "学号不能为空")
    @Size(max = 32, message = "学号长度不能超过32位")
    private String username;

    /** 真实姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 32, message = "姓名长度不能超过32位")
    private String realName;

    /** 性别：男 / 女 */
    private String gender;

    /** 联系电话（11 位数字） */
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;
}
