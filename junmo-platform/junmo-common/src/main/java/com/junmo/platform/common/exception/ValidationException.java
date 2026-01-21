package com.junmo.platform.common.exception;

import com.junmo.platform.base.exception.BaseException;

import java.util.Collections;
import java.util.List;

/**
 * 参数校验异常
 * 用于处理请求参数校验失败的异常
 *
 * @author junmo
 * @version 1.0.0
 */
public class ValidationException extends BaseException {

    private static final long serialVersionUID = 1L;

    /** 校验字段名 */
    private final String field;

    /** 校验错误消息列表 */
    private final List<String> errors;

    /**
     * 构造函数
     *
     * @param field 校验字段名
     * @param message 错误消息
     */
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", message, 400);
        this.field = field;
        this.errors = null;
    }

    /**
     * 构造函数
     *
     * @param field 校验字段名
     * @param message 错误消息
     * @param cause 原始异常
     */
    public ValidationException(String field, String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause, 400);
        this.field = field;
        this.errors = null;
    }

    /**
     * 构造函数（带多个错误消息）
     *
     * @param errors 校验错误消息列表
     */
    public ValidationException(List<String> errors) {
        super("VALIDATION_ERROR", "参数校验失败", 400);
        this.field = null;
        // 创建防御性拷贝，避免外部修改内部状态
        this.errors = errors != null ? List.copyOf(errors) : null;
    }

    /**
     * 获取校验字段名
     *
     * @return 校验字段名
     */
    public String getField() {
        return field;
    }

    /**
     * 获取校验错误消息列表
     *
     * @return 校验错误消息列表的不可修改视图
     */
    public List<String> getErrors() {
        // 返回不可修改的视图，避免外部修改内部状态
        return errors != null ? Collections.unmodifiableList(errors) : null;
    }
}