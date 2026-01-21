package com.junmo.platform.common.exception;

import com.junmo.platform.base.exception.BaseException;

/**
 * 业务异常
 * 用于处理业务逻辑相关的异常
 *
 * @author junmo
 * @version 1.0.0
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public BusinessException(String code, String message, int httpStatus) {
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
    public BusinessException(String code, String message, Throwable cause, int httpStatus) {
        super(code, message, cause, httpStatus);
    }

    /**
     * 构造函数（默认错误码和状态码）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super("BUSINESS_ERROR", message, 400);
    }

    /**
     * 构造函数（默认状态码）
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(String code, String message) {
        super(code, message, 400);
    }

    /**
     * 构造函数（默认错误码和状态码）
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super("BUSINESS_ERROR", message, cause, 400);
    }

    /**
     * 构造函数（默认状态码）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause, 400);
    }
}