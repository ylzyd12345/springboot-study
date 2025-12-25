package com.kev1n.spring4demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 废弃API注解
 * 用于标记已废弃的API接口
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedApi {
    
    /**
     * 废弃原因
     */
    String reason();
    
    /**
     * 废弃时间
     */
    String since();
    
    /**
     * 建议使用的新版本
     */
    String useInstead() default "";
    
    /**
     * 移除时间
     */
    String removeAfter() default "";
}