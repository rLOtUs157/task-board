package com.example.taskboard.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 200成功，其他失败
    private String msg;   // 提示信息
    private T data;       // 数据

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
