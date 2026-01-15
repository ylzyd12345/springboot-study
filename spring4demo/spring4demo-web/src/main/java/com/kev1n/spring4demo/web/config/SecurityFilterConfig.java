package com.kev1n.spring4demo.web.config;

import com.kev1n.spring4demo.web.filter.SqlInjectionFilter;
import com.kev1n.spring4demo.web.filter.XssFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 安全过滤器配置
 * 注册所有安全相关的过滤器
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class SecurityFilterConfig {

    /**
     * 注册SQL注入防护过滤器
     *
     * @param filter SQL注入防护过滤器
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<SqlInjectionFilter> sqlInjectionFilterRegistration(SqlInjectionFilter filter) {
        FilterRegistrationBean<SqlInjectionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setName("sqlInjectionFilter");
        registration.setOrder(1); // 最高优先级
        log.info("SQL注入防护过滤器已注册");
        return registration;
    }

    /**
     * 注册XSS防护过滤器
     *
     * @param filter XSS防护过滤器
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssFilter filter) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(2); // 第二优先级
        log.info("XSS防护过滤器已注册");
        return registration;
    }
}