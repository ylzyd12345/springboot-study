package com.kev1n.spring4demo.core.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import com.kev1n.spring4demo.core.annotation.RateLimit;
import com.kev1n.spring4demo.core.annotation.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * <p>
 * 拦截带有@RateLimit注解的方法，实现限流逻辑。
 * 使用令牌桶算法进行限流，支持配置限流阈值和超时时间。
 * </p>
 *
 * <p>限流策略：</p>
 * <ul>
 *   <li>当timeout=0时，不等待，直接拒绝超出的请求</li>
 *   <li>当timeout>0时，会在超时时间内尝试获取令牌</li>
 *   <li>限流触发时抛出RateLimitException异常</li>
 * </ul>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    @Autowired
    private Cache<String, RateLimiter> rateLimiterCache;

    /**
     * 环绕通知：拦截@RateLimit注解的方法
     *
     * @param joinPoint 连接点
     * @param rateLimit 限流注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常或限流异常
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 获取限流key
        String key = rateLimit.key();
        if (!StringUtils.hasText(key)) {
            // 如果没有指定key，使用方法签名作为默认key
            key = joinPoint.getSignature().toShortString();
        }

        // 从缓存中获取或创建限流器
        RateLimiter rateLimiter = rateLimiterCache.get(key, k -> {
            log.debug("创建新的限流器: key={}, permits={}", k, rateLimit.permits());
            return RateLimiter.create(rateLimit.permits());
        });

        // 根据配置的timeout值决定限流策略
        if (rateLimit.timeout() > 0) {
            // 策略1：尝试获取令牌，等待超时时间
            if (!rateLimiter.tryAcquire(rateLimit.timeout(), TimeUnit.SECONDS)) {
                log.warn("限流触发: key={}, permits={}, timeout={}", key, rateLimit.permits(), rateLimit.timeout());
                throw new RateLimitException("请求过于频繁，请稍后重试");
            }
        } else {
            // 策略2：不等待，直接返回
            if (!rateLimiter.tryAcquire()) {
                log.warn("限流触发: key={}, permits={}", key, rateLimit.permits());
                throw new RateLimitException("请求过于频繁，请稍后重试");
            }
        }

        // 执行目标方法
        return joinPoint.proceed();
    }
}