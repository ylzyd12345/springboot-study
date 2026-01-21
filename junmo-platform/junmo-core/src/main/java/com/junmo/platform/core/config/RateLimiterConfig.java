package com.junmo.platform.core.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 限流配置类
 * <p>
 * 配置Guava RateLimiter限流器，使用Caffeine缓存来存储和管理限流器实例。
 * 每个限流器根据key进行区分，支持动态创建和管理。
 * </p>
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>缓存最大容量：1000个限流器</li>
 *   <li>缓存过期时间：1小时（写入后过期）</li>
 *   <li>默认限流阈值：每秒100个请求</li>
 * </ul>
 *
 * @author junmo-platform
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 创建限流器缓存
     * <p>
     * 使用Caffeine缓存来存储RateLimiter实例，每个key对应一个限流器。
     * 当缓存中没有对应的限流器时，会自动创建一个新的限流器。
     * </p>
     *
     * @return 限流器缓存
     */
    @Bean
    public Cache<String, RateLimiter> rateLimiterCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(key -> RateLimiter.create(100)); // 默认每秒100个请求
    }
}