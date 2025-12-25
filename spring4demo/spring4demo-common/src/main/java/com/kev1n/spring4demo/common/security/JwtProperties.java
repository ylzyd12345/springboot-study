package com.kev1n.spring4demo.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "mySecretKey123456789012345678901234567890";

    /**
     * JWT过期时间（毫秒）
     */
    private Long expiration = 86400000L; // 24小时

    /**
     * JWT刷新Token过期时间（毫秒）
     */
    private Long refreshExpiration = 604800000L; // 7天

    /**
     * JWT请求头名称
     */
    private String header = "Authorization";

    /**
     * JWT Token前缀
     */
    private String prefix = "Bearer";
}