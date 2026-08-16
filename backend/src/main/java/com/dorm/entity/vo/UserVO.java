package com.dorm.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户展示对象：不暴露密码等敏感字段
 */
@Data
public class UserVO {

    /** 用户主键ID */
    private Long userId;

    /** 登录账号（学生为学号） */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 角色：student-学生，admin-管理员 */
    private String role;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
