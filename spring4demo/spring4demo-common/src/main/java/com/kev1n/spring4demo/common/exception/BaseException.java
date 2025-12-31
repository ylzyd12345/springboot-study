package com.kev1n.spring4demo.common.exception;

/**
 * 基础异常类
 * 所有自定义异常的父类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public abstract class BaseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /** 错误码 */
    private final String code;
    
    /** HTTP状态码 */
    private final int httpStatus;
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public BaseException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     * @param httpStatus HTTP状态码
     */
    public BaseException(String code, String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取HTTP状态码
     * 
     * @return HTTP状态码
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}