package com.junmo.platform.core.service.advanced.impl;

import com.junmo.platform.core.service.advanced.RedissonAdvancedDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * Redisson高级数据结构服务实现类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonAdvancedDataServiceImpl implements RedissonAdvancedDataService {

    private final RedissonClient redissonClient;

    // ==================== HyperLogLog 操作 ====================

    @Override
    public RHyperLogLog<Object> getHyperLogLog(String key) {
        return redissonClient.getHyperLogLog(key);
    }

    @Override
    public boolean hyperLogLogAdd(String key, Object... values) {
        RHyperLogLog<Object> hyperLogLog = redissonClient.getHyperLogLog(key);
        return hyperLogLog.add(values);
    }

    @Override
    public long hyperLogLogCount(String key) {
        RHyperLogLog<Object> hyperLogLog = redissonClient.getHyperLogLog(key);
        return hyperLogLog.count();
    }

    // ==================== Bloom Filter 操作 ====================

    @Override
    public <T> RBloomFilter<T> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

    @Override
    public <T> RBloomFilter<T> createBloomFilter(String key, long expectedInsertions, double falseProbability) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(key);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
    }

    @Override
    public <T> boolean bloomFilterContains(String key, T value) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(key);
        return bloomFilter.contains(value);
    }
}