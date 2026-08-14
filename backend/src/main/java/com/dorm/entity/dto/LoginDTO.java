package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 登录入参
 */
@Data
public class LoginDTO {

    /** 登录账号 */
    @NotBlank(message = "账号不能为空")
    @Size(max = 32, message = "账号长度不能超过32位")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
}
