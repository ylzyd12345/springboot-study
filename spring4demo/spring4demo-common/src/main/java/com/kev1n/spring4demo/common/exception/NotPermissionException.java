package com.kev1n.spring4demo.common.exception;

/**
 * 无权限异常
 * 用户已登录但无权限访问资源
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class NotPermissionException extends AuthException {
    
    private static final long serialVersionUID = 1L;
    
    /** 权限码 */
    private final String permission;
    
    /**
     * 构造函数
     * 
     * @param permission 权限码
     * @param message 错误消息
     */
    public NotPermissionException(String permission, String message) {
        super("NO_PERMISSION", message, 403);
        this.permission = permission;
    }
    
    /**
     * 构造函数
     * 
     * @param permission 权限码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public NotPermissionException(String permission, String message, Throwable cause) {
        super("NO_PERMISSION", message, cause, 403);
        this.permission = permission;
    }
    
    /**
     * 获取权限码
     * 
     * @return 权限码
     */
    public String getPermission() {
        return permission;
    }
}