package com.junmo.platform.core.service.collection;

import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;

/**
 * Redisson集合服务接口
 *
 * <p>提供集合相关的操作，包括Map、Set、List等。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface RedissonCollectionService {

    // ==================== Map 操作 ====================

    /**
     * 获取 Map
     *
     * @param key key
     * @param <K> 键类型
     * @param <V> 值类型
     * @return RMap 实例
     */
    <K, V> RMap<K, V> getMap(String key);

    /**
     * Map 设置值
     *
     * @param key key
     * @param mapKey map key
     * @param value 值
     * @param <K> 键类型
     * @param <V> 值类型
     */
    <K, V> void mapPut(String key, K mapKey, V value);

    /**
     * Map 获取值
     *
     * @param key key
     * @param mapKey map key
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 值
     */
    <K, V> V mapGet(String key, K mapKey);

    // ==================== Set 操作 ====================

    /**
     * 获取 Set
     *
     * @param key key
     * @param <V> 值类型
     * @return RSet 实例
     */
    <V> RSet<V> getSet(String key);

    /**
     * Set 添加值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否添加成功
     */
    <V> boolean setAdd(String key, V value);

    /**
     * Set 删除值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否删除成功
     */
    <V> boolean setRemove(String key, V value);

    // ==================== List 操作 ====================

    /**
     * 获取 List
     *
     * @param key key
     * @param <V> 值类型
     * @return RList 实例
     */
    <V> RList<V> getList(String key);

    /**
     * List 添加值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否添加成功
     */
    <V> boolean listAdd(String key, V value);

    /**
     * List 获取值
     *
     * @param key key
     * @param index 索引
     * @param <V> 值类型
     * @return 值
     */
    <V> V listGet(String key, int index);
}