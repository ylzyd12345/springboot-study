package com.junmo.platform.common.exception;

import com.junmo.platform.base.exception.BaseException;

/**
 * 系统异常
 * 用于处理系统级别的异常，如数据库异常、网络异常等
 *
 * @author junmo
 * @version 1.0.0
 */
public class SystemException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public SystemException(String code, String message) {
        super(code, message, 500);
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public SystemException(String code, String message, Throwable cause) {
        super(code, message, cause, 500);
    }

    /**
     * 构造函数（默认错误码）
     *
     * @param message 错误消息
     */
    public SystemException(String message) {
        super("SYSTEM_ERROR", message, 500);
    }

    /**
     * 构造函数（默认错误码）
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public SystemException(String message, Throwable cause) {
        super("SYSTEM_ERROR", message, cause, 500);
    }
}