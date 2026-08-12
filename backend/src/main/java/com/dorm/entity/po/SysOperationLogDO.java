package com.dorm.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表：AOP 自动记录的管理端写操作日志
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLogDO {

    /** 日志主键ID */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名（冗余字段，避免联表） */
    private String operatorName;

    /** 操作模块 */
    private String module;

    /** 操作类型：新增 / 修改 / 删除 */
    private String operationType;

    /** 操作详情描述 */
    private String content;

    /** 操作IP地址 */
    private String ipAddress;

    /** 操作时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
