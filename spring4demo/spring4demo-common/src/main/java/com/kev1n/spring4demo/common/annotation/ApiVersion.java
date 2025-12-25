package com.kev1n.spring4demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本注解
 * 用于标记API接口的版本号
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * API版本号
     */
    int value();
    
    /**
     * 是否为正式版本
     */
    boolean stable() default false;
    
    /**
     * 版本描述
     */
    String description() default "";
    
    /**
     * 废弃时间
     */
    String deprecatedSince() default "";
    
    /**
     * 移除时间
     */
    String removeAfter() default "";
}