package com.kev1n.spring4demo.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API版本控制配置
 * 使用Spring Boot 4的API版本控制特性
 * 支持URL路径版本、请求头版本、参数版本等多种版本控制方式
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    /**
     * 配置请求映射处理器
     * 设置API版本控制策略
     *
     * @param handlerMapping 请求映射处理器
     */
    @Override
    public void configurePathMatch(RequestMappingHandlerMapping handlerMapping) {
        // 启用尾部斜杠匹配
        handlerMapping.setUseTrailingSlashMatch(true);
        
        // 启用后缀模式匹配
        handlerMapping.setUseSuffixPatternMatch(true);
        
        // 设置内容协商
        handlerMapping.setContentNegotiationManager(
            new org.springframework.web.accept.ContentNegotiationManagerFactoryBean()
                .addMediaTypes("v1", org.springframework.http.MediaType.APPLICATION_JSON)
                .addMediaTypes("v2", org.springframework.http.MediaType.APPLICATION_JSON)
                .build()
        );
    }
}