package com.dorm.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在管理员写操作（增删改）接口方法上，由 OperationLogAspect 自动记录
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 操作模块（如 楼栋管理、报修管理、入住管理） */
    String module();

    /** 操作类型：新增 / 修改 / 删除 */
    String type();

    /** 操作描述（如 删除楼栋） */
    String desc() default "";
}
