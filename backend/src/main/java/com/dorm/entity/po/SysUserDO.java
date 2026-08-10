package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户表：学生与管理员统一身份体系
 */
@Data
@TableName("sys_user")
public class SysUserDO {

    /** 用户主键ID */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /** 登录账号（学生为学号，管理员为管理员账号） */
    private String username;

    /** 登录密码（BCrypt 加密存储） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色：student-学生，admin-管理员 */
    private String role;

    /** 性别 */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
