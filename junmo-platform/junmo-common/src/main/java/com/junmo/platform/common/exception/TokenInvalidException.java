package com.junmo.platform.common.exception;

/**
 * Token无效异常
 * Token格式错误或无法解析
 *
 * @author junmo
 * @version 1.0.0
 */
public class TokenInvalidException extends AuthException {

    private static final long serialVersionUID = 1L;

    /** Token值 */
    private final String token;

    /**
     * 构造函数
     *
     * @param token Token值
     * @param message 错误消息
     */
    public TokenInvalidException(String token, String message) {
        super("TOKEN_INVALID", message, 401);
        this.token = token;
    }

    /**
     * 构造函数
     *
     * @param token Token值
     * @param message 错误消息
     * @param cause 原始异常
     */
    public TokenInvalidException(String token, String message, Throwable cause) {
        super("TOKEN_INVALID", message, cause, 401);
        this.token = token;
    }

    /**
     * 获取Token值
     *
     * @return Token值
     */
    public String getToken() {
        return token;
    }
}