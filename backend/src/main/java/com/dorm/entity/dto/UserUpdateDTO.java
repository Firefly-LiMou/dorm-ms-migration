package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 编辑学生账号入参：字段可空，服务端仅更新非空字段（禁用/启用仅传 status）
 */
@Data
public class UserUpdateDTO {

    /** 真实姓名 */
    @Size(max = 32, message = "姓名长度不能超过32位")
    private String realName;

    /** 性别：男 / 女 */
    private String gender;

    /** 联系电话（11 位数字） */
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;
}
