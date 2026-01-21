package com.junmo.platform.core.service.advanced;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RHyperLogLog;

/**
 * Redisson高级数据结构服务接口
 *
 * <p>提供高级数据结构相关的操作，包括HyperLogLog、Bloom Filter等。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface RedissonAdvancedDataService {

    // ==================== HyperLogLog 操作 ====================

    /**
     * 获取 HyperLogLog
     *
     * @param key key
     * @return RHyperLogLog 实例
     */
    RHyperLogLog<Object> getHyperLogLog(String key);

    /**
     * HyperLogLog 添加值
     *
     * @param key    key
     * @param values 值集合
     * @return 是否添加成功
     */
    boolean hyperLogLogAdd(String key, Object... values);

    /**
     * HyperLogLog 获取基数
     *
     * @param key key
     * @return 基数
     */
    long hyperLogLogCount(String key);

    // ==================== Bloom Filter 操作 ====================

    /**
     * 获取布隆过滤器
     *
     * @param key key
     * @param <T> 值类型
     * @return RBloomFilter 实例
     */
    <T> RBloomFilter<T> getBloomFilter(String key);

    /**
     * 创建布隆过滤器
     *
     * @param key key
     * @param expectedInsertions 预期插入数量
     * @param falseProbability 误判率
     * @param <T> 值类型
     * @return RBloomFilter 实例
     */
    <T> RBloomFilter<T> createBloomFilter(String key, long expectedInsertions, double falseProbability);

    /**
     * 布隆过滤器判断是否存在
     *
     * @param key key
     * @param value 值
     * @param <T> 值类型
     * @return 是否存在
     */
    <T> boolean bloomFilterContains(String key, T value);
}