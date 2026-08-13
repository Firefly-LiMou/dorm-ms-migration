package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    @Size(min = 1, max = 32, message = "账号长度为1-32位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
}
