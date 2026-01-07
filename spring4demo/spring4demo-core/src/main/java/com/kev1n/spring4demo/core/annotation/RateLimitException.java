package com.kev1n.spring4demo.core.annotation;

/**
 * 限流异常
 * <p>
 * 当请求超过限流阈值时抛出此异常。
 * 通常由RateLimiterAspect切面在限流触发时抛出。
 * </p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
public class RateLimitException extends RuntimeException {

    /**
     * 错误码
     */
    private static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public RateLimitException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause   原因
     */
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return ERROR_CODE;
    }
}