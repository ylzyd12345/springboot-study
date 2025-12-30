package com.kev1n.spring4demo.core.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 安全配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenSecurityConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一条 match 规则
            SaRouter
                .match("/**")    // 拦截的 path 列表，可以写多个
                .notMatch("/auth/**")    // 排除掉的 path 列表，可以写多个
                .notMatch("/user/login")
                .notMatch("/user/register")
                .notMatch("/swagger-ui/**")
                .notMatch("/v3/api-docs/**")
                .notMatch("/doc.html")
                .notMatch("/webjars/**")
                .notMatch("/favicon.ico")
                .notMatch("/actuator/**")
                .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式
        })).addPathPatterns("/**");
    }
}