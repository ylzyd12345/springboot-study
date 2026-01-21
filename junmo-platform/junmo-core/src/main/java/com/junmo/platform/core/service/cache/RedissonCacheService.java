package com.junmo.platform.core.service.cache;

import org.redisson.api.RBucket;

import java.util.concurrent.TimeUnit;

/**
 * Redisson缓存服务接口
 *
 * <p>提供缓存相关的操作，包括设置值、获取值、删除key、设置过期时间等。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface RedissonCacheService {

    /**
     * 获取 Bucket
     *
     * @param key key
     * @param <T> 值类型
     * @return RBucket 实例
     */
    <T> RBucket<T> getBucket(String key);

    /**
     * 设置值
     *
     * @param key key
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置值并指定过期时间
     *
     * @param key key
     * @param value 值
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long time, TimeUnit timeUnit);

    /**
     * 获取值
     *
     * @param key key
     * @param <T> 值类型
     * @return 值
     */
    <T> T get(String key);

    /**
     * 删除 key
     *
     * @param key key
     * @return 是否删除成功
     */
    boolean delete(String key);

    /**
     * 判断 key 是否存在
     *
     * @param key key
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 设置过期时间
     *
     * @param key key
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    void expire(String key, long time, TimeUnit timeUnit);

    /**
     * 获取过期时间
     *
     * @param key key
     * @return 过期时间（毫秒）
     */
    long getExpire(String key);
}