package com.kev1n.spring4demo.common.config;

import com.kev1n.spring4demo.common.interceptor.ApiVersionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API版本配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class ApiVersionConfig implements WebMvcConfigurer {

    private final ApiVersionInterceptor apiVersionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiVersionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**", "/api/auth/**", "/api/health/**");
    }
}