package com.junmo.platform.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * <p>
 * 使用Guava RateLimiter实现接口级别的限流功能。
 * 支持自定义限流key、限流阈值和超时时间。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * @GetMapping
 * @RateLimit(key = "user:list", permits = 100)
 * public ResponseEntity<Page<User>> getUsers() {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 *
 * @author junmo-platform
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key
     * <p>
     * 用于标识不同的限流器，相同的key共享同一个限流器。
     * 如果不指定，则使用方法签名作为key。
     * </p>
     *
     * @return 限流key
     */
    String key() default "";

    /**
     * 每秒允许的请求数（限流阈值）
     * <p>
     * 默认值为100，表示每秒最多允许100个请求通过。
     * 可以根据接口的访问频率和系统承载能力进行调整。
     * </p>
     *
     * @return 每秒允许的请求数
     */
    double permits() default 100;

    /**
     * 超时时间（秒）
     * <p>
     * 当请求超过限流阈值时，等待获取令牌的最长时间。
     * 默认值为0，表示不等待，直接拒绝请求。
     * 设置为大于0的值时，会在超时时间内尝试获取令牌。
     * </p>
     *
     * @return 超时时间（秒）
     */
    long timeout() default 0;
}