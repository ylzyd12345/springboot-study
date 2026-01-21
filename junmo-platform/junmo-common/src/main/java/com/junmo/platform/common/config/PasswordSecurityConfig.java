package com.junmo.platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码安全配置
 *
 * <p>提供密码编码器 Bean，使用 BCrypt 算法进行密码加密。</p>
 *
 * @author junmo
 * @version 1.0.0
 */
@Configuration
public class PasswordSecurityConfig {

    /**
     * 密码编码器
     * 使用 BCrypt 算法进行密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}