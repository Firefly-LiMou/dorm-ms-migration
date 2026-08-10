package com.dorm.common.exception;

import lombok.Getter;

/**
 * 业务异常：业务规则校验失败时抛出，携带错误码与用户可读提示
 * 继承 RuntimeException，触发事务回滚；由全局异常处理器统一封装返回
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务错误码（见接口清单文档错误码表） */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
