package com.dorm.common.result;

import lombok.Data;

/**
 * 统一返回结果封装
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /** 状态码：200 成功，其他为错误码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    public static <T> Result<T> success() {
        return build(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return build(200, "操作成功", data);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return build(code, msg, null);
    }

    private static <T> Result<T> build(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
