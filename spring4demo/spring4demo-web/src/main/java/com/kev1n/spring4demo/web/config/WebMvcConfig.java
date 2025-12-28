package com.kev1n.spring4demo.web.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.kev1n.spring4demo.web.interceptor.ApiVersionInterceptor;
import com.kev1n.spring4demo.web.interceptor.MetricsInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 统一管理所有拦截器配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MetricsInterceptor metricsInterceptor;
    private final ApiVersionInterceptor apiVersionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Sa-Token认证拦截器 - 最高优先级
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter
                .match("/**")
                .notMatch("/auth/login")
                .notMatch("/auth/register")
                .notMatch("/doc.html")
                .notMatch("/v3/api-docs/**")
                .notMatch("/webjars/**")
                .notMatch("/img.icons/**")
                .notMatch("/actuator/**")
                .notMatch("/error")
                .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**").order(1);

        // 2. API版本拦截器 - 第二优先级
        registry.addInterceptor(apiVersionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**", "/api/auth/**", "/api/health/**")
                .order(2);

        // 3. Metrics拦截器 - 最低优先级
        registry.addInterceptor(metricsInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**", "/api/auth/**", "/api/version/**")
                .order(3);
    }
}