package com.junmo.platform.common.exception;

/**
 * 未登录异常
 * 用户未登录或登录状态失效
 *
 * @author junmo
 * @version 1.0.0
 */
public class NotLoginException extends AuthException {

    private static final long serialVersionUID = 1L;

    /** 未提供Token */
    public static final String NOT_TOKEN = "NOT_TOKEN";

    /** Token无效 */
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    /** Token已过期 */
    public static final String TOKEN_TIMEOUT = "TOKEN_TIMEOUT";

    /** 被顶下线 */
    public static final String BE_REPLACED = "BE_REPLACED";

    /** 被踢下线 */
    public static final String KICK_OUT = "KICK_OUT";

    /** Token被冻结 */
    public static final String TOKEN_FREEZE = "TOKEN_FREEZE";

    /** Token前缀错误 */
    public static final String NO_PREFIX = "NO_PREFIX";

    /**
     * 构造函数
     *
     * @param type 异常类型
     * @param message 错误消息
     */
    public NotLoginException(String type, String message) {
        super(type, message, 401);
    }

    /**
     * 构造函数
     *
     * @param type 异常类型
     * @param message 错误消息
     * @param cause 原始异常
     */
    public NotLoginException(String type, String message, Throwable cause) {
        super(type, message, cause, 401);
    }

    /**
     * 获取异常类型
     *
     * @return 异常类型
     */
    public String getType() {
        return getCode();
    }
}