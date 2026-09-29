package com.example.taskboard.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：所有异常统一在这里拦截，返回统一格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 参数校验异常统一处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        return Result.error(msg);
    }

    // 其他未知异常兜底
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("服务器内部错误：" + e.getMessage());
    }
}
