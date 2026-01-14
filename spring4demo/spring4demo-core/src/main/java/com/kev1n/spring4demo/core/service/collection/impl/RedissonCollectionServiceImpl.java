package com.kev1n.spring4demo.core.service.collection.impl;

import com.kev1n.spring4demo.core.service.collection.RedissonCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * Redisson集合服务实现类
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonCollectionServiceImpl implements RedissonCollectionService {

    private final RedissonClient redissonClient;

    // ==================== Map 操作 ====================

    @Override
    public <K, V> RMap<K, V> getMap(String key) {
        return redissonClient.getMap(key);
    }

    @Override
    public <K, V> void mapPut(String key, K mapKey, V value) {
        RMap<K, V> map = redissonClient.getMap(key);
        map.put(mapKey, value);
    }

    @Override
    public <K, V> V mapGet(String key, K mapKey) {
        RMap<K, V> map = redissonClient.getMap(key);
        return map.get(mapKey);
    }

    // ==================== Set 操作 ====================

    @Override
    public <V> RSet<V> getSet(String key) {
        return redissonClient.getSet(key);
    }

    @Override
    public <V> boolean setAdd(String key, V value) {
        RSet<V> set = redissonClient.getSet(key);
        return set.add(value);
    }

    @Override
    public <V> boolean setRemove(String key, V value) {
        RSet<V> set = redissonClient.getSet(key);
        return set.remove(value);
    }

    // ==================== List 操作 ====================

    @Override
    public <V> RList<V> getList(String key) {
        return redissonClient.getList(key);
    }

    @Override
    public <V> boolean listAdd(String key, V value) {
        RList<V> list = redissonClient.getList(key);
        return list.add(value);
    }

    @Override
    public <V> V listGet(String key, int index) {
        RList<V> list = redissonClient.getList(key);
        return list.get(index);
    }
}