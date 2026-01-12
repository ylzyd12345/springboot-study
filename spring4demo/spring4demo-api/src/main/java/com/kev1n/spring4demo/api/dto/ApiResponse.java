package com.kev1n.spring4demo.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一API响应格式.
 * 
 * <p>提供统一的API响应格式，包含响应码、消息、数据和时间戳。
 * 所有Controller接口都应该使用此类作为返回值类型。</p>
 * 
 * @param <T> 响应数据类型
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /** 响应码. */
    private Integer code;

    /** 响应消息. */
    private String message;

    /** 响应数据. */
    private T data;

    /** 响应时间戳. */
    private LocalDateTime timestamp;

    /** 请求ID（用于链路追踪）. */
    private String traceId;

    /**
     * 成功响应（无数据）.
     *
     * @param <T> 响应数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功响应（带数据）.
     *
     * @param <T> 响应数据类型
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 成功响应（自定义消息）.
     *
     * @param <T> 响应数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应.
     *
     * @param <T> 响应数据类型
     * @param message 错误消息
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(500)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应（自定义错误码）.
     *
     * @param <T> 响应数据类型
     * @param code 错误码
     * @param message 错误消息
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 失败响应（带数据）.
     *
     * @param <T> 响应数据类型
     * @param code 错误码
     * @param message 错误消息
     * @param data 响应数据
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 判断响应是否成功.
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}