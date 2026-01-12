package com.kev1n.spring4demo.web.annotation;

import java.lang.annotation.*;

/**
 * API版本注解
 * 用于标记API接口的版本号
 * 支持URL路径版本、请求头版本、参数版本等多种方式
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {

    /**
     * 版本号
     * 例如：1, 2, 3
     */
    int value();

    /**
     * 版本类型
     * URL_PATH: URL路径版本（/api/v1/users）
     * HEADER: 请求头版本（X-API-Version: 1）
     * PARAMETER: 参数版本（?version=1）
     */
    ApiVersionType type() default ApiVersionType.URL_PATH;

    /**
     * 版本类型枚举
     */
    enum ApiVersionType {
        /**
         * URL路径版本
         * 例如：/api/v1/users
         */
        URL_PATH,
        
        /**
         * 请求头版本
         * 例如：X-API-Version: 1
         */
        HEADER,
        
        /**
         * 参数版本
         * 例如：?version=1
         */
        PARAMETER
    }
}