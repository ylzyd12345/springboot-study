package com.junmo.platform.core.service.cache.impl;

import com.junmo.platform.core.service.cache.RedissonCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redisson缓存服务实现类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonCacheServiceImpl implements RedissonCacheService {

    private final RedissonClient redissonClient;

    @Override
    public <T> RBucket<T> getBucket(String key) {
        return redissonClient.getBucket(key);
    }

    @Override
    public void set(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    @Override
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, time, timeUnit);
    }

    @Override
    public <T> T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }

    @Override
    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    @Override
    public void expire(String key, long time, TimeUnit timeUnit) {
        redissonClient.getBucket(key).expire(time, timeUnit);
    }

    @Override
    public long getExpire(String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }
}