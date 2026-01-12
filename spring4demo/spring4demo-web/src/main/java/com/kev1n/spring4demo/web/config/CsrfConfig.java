package com.kev1n.spring4demo.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * CSRF防护配置
 * 配置跨站请求伪造（CSRF）防护
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class CsrfConfig {

    /**
     * 配置CSRF防护
     * 使用Cookie存储CSRF令牌
     *
     * @param http HTTP安全配置
     * @return 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 启用CSRF防护
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository())
                // 忽略GraphQL端点的CSRF检查（GraphQL使用POST方法）
                .ignoringRequestMatchers("/graphql")
                // 忽略API端点的CSRF检查（API通常使用Token认证）
                .ignoringRequestMatchers("/api/**")
            )
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许所有请求（使用Sa-Token进行认证授权）
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * CSRF令牌存储库
     * 使用Cookie存储CSRF令牌，方便前端获取
     *
     * @return CSRF令牌存储库
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        // 使用Cookie存储CSRF令牌
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        
        // 设置Cookie的属性
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        
        log.info("CSRF防护已启用，使用Cookie存储令牌");
        
        return repository;
    }

    /**
     * Web安全自定义器
     * 忽略静态资源的CSRF检查
     *
     * @return Web安全自定义器
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/fonts/**",
                "/favicon.ico",
                "/error",
                "/actuator/**"
            );
    }
}