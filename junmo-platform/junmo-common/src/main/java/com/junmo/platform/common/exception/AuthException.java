package com.junmo.platform.common.exception;

import com.junmo.platform.base.exception.BaseException;

/**
 * 认证异常基类
 * 用于处理所有认证相关的异常
 *
 * @author junmo
 * @version 1.0.0
 */
public class AuthException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public AuthException(String code, String message, int httpStatus) {
        super(code, message, httpStatus);
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     * @param httpStatus HTTP状态码
     */
    public AuthException(String code, String message, Throwable cause, int httpStatus) {
        super(code, message, cause, httpStatus);
    }
}