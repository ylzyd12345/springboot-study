package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.core.service.advanced.RedissonAdvancedDataService;
import com.kev1n.spring4demo.core.service.cache.RedissonCacheService;
import com.kev1n.spring4demo.core.service.collection.RedissonCollectionService;
import com.kev1n.spring4demo.core.service.lock.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redisson服务门面（统一入口）
 *
 * <p>采用门面模式，提供统一的Redisson操作入口，委托给各个子服务。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonServiceFacade {

    private final RedissonClient redissonClient;
    private final RedissonLockService lockService;
    private final RedissonCacheService cacheService;
    private final RedissonCollectionService collectionService;
    private final RedissonAdvancedDataService advancedDataService;

    /**
     * 获取 Redisson 客户端
     *
     * @return RedissonClient 实例
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * 获取分布式锁服务
     *
     * @return RedissonLockService 实例
     */
    public RedissonLockService lock() {
        return lockService;
    }

    /**
     * 获取缓存服务
     *
     * @return RedissonCacheService 实例
     */
    public RedissonCacheService cache() {
        return cacheService;
    }

    /**
     * 获取集合服务
     *
     * @return RedissonCollectionService 实例
     */
    public RedissonCollectionService collection() {
        return collectionService;
    }

    /**
     * 获取高级数据结构服务
     *
     * @return RedissonAdvancedDataService 实例
     */
    public RedissonAdvancedDataService advanced() {
        return advancedDataService;
    }

    // ==================== 其他操作 ====================

    /**
     * 获取所有 keys
     *
     * @param pattern 匹配模式
     * @return keys 集合
     */
    public Iterable<String> keys(String pattern) {
        return redissonClient.getKeys().getKeysByPattern(pattern);
    }

    /**
     * 批量删除 keys
     *
     * @param keys keys 集合
     * @return 删除数量
     */
    public long delete(Collection<String> keys) {
        return redissonClient.getKeys().delete(keys.toArray(new String[0]));
    }

    /**
     * 获取数据库大小
     *
     * @return key 的数量
     */
    public long dbSize() {
        return redissonClient.getKeys().count();
    }

    /**
     * 刷新所有 key 的过期时间
     *
     * @param key key
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    public void expireAll(String key, long time, TimeUnit timeUnit) {
        redissonClient.getKeys().expire(key, time, timeUnit);
    }
}