package com.kev1n.spring4demo.common.constant;

/**
 * 安全常量
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public final class SecurityConstants {
    
    private SecurityConstants() {
        // Utility class
    }
    
    /** JWT相关 */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_KEY = "Authorization";
    public static final String JWT_TYPE_KEY = "type";
    public static final String JWT_TYPE_ACCESS = "access";
    public static final String JWT_TYPE_REFRESH = "refresh";
    
    /** JWT过期时间 */
    public static final long JWT_ACCESS_TOKEN_EXPIRATION = 3600L; // 1小时
    public static final long JWT_REFRESH_TOKEN_EXPIRATION = 86400L * 7; // 7天
    
    /** 权限相关 */
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_PREFIX = "PERM_";
    
    /** 预定义角色 */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_GUEST = "GUEST";
    
    /** 密码相关 */
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]";
    
    /** 安全相关 */
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOGIN_LOCK_TIME = 30 * 60 * 1000L; // 30分钟
    
    /** 缓存相关 */
    public static final String CACHE_USER_PREFIX = "user:";
    public static final String CACHE_ROLE_PREFIX = "role:";
    public static final String CACHE_PERMISSION_PREFIX = "permission:";
    public static final String CACHE_TOKEN_PREFIX = "token:";
    
    /** 路径匹配 */
    public static final String[] AUTH_WHITELIST = {
        "/api/v1/auth/**",
        "/api/v1/public/**",
        "/actuator/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/doc.html"
    };
}