package com.kev1n.spring4demo.core.service;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 服务封装类
 *
 * <p>提供常用的 Redisson 操作方法，包括分布式锁、缓存、集合等。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
public class RedissonService {

    private final RedissonClient redissonClient;

    public RedissonService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取 Redisson 客户端
     *
     * @return RedissonClient 实例
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    // ==================== 分布式锁 ====================

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁的 key
     * @return RLock 实例
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的 key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 执行带锁的操作
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @param action 要执行的操作
     * @return 是否执行成功
     */
    public boolean executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable action) {
        if (tryLock(lockKey, waitTime, leaseTime, unit)) {
            try {
                action.run();
                return true;
            } finally {
                unlock(lockKey);
            }
        }
        return false;
    }

    /**
     * 执行带锁的操作（有返回值）
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> action) {
        if (tryLock(lockKey, waitTime, leaseTime, unit)) {
            try {
                return action.get();
            } finally {
                unlock(lockKey);
            }
        }
        return null;
    }

    /**
     * 获取读写锁
     *
     * @param lockKey 锁的 key
     * @return RReadWriteLock 实例
     */
    public RReadWriteLock getReadWriteLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey);
    }

    /**
     * 获取公平锁
     *
     * @param lockKey 锁的 key
     * @return RLock 实例
     */
    public RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    /**
     * 获取信号量
     *
     * @param semaphoreKey 信号量的 key
     * @return RSemaphore 实例
     */
    public RSemaphore getSemaphore(String semaphoreKey) {
        return redissonClient.getSemaphore(semaphoreKey);
    }

    /**
     * 获取倒计时门栓
     *
     * @param countDownLatchKey 倒计时门栓的 key
     * @return RCountDownLatch 实例
     */
    public RCountDownLatch getCountDownLatch(String countDownLatchKey) {
        return redissonClient.getCountDownLatch(countDownLatchKey);
    }

    // ==================== 缓存操作 ====================

    /**
     * 获取 Bucket
     *
     * @param key key
     * @param <T> 值类型
     * @return RBucket 实例
     */
    public <T> RBucket<T> getBucket(String key) {
        return redissonClient.getBucket(key);
    }

    /**
     * 设置值
     *
     * @param key key
     * @param value 值
     */
    public void set(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 设置值并指定过期时间
     *
     * @param key key
     * @param value 值
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, time, timeUnit);
    }

    /**
     * 获取值
     *
     * @param key key
     * @param <T> 值类型
     * @return 值
     */
    public <T> T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 删除 key
     *
     * @param key key
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }

    /**
     * 判断 key 是否存在
     *
     * @param key key
     * @return 是否存在
     */
    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 设置过期时间
     *
     * @param key key
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    public void expire(String key, long time, TimeUnit timeUnit) {
        redissonClient.getBucket(key).expire(time, timeUnit);
    }

    /**
     * 获取过期时间
     *
     * @param key key
     * @return 过期时间（秒）
     */
    public long getExpire(String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }

    // ==================== Map 操作 ====================

    /**
     * 获取 Map
     *
     * @param key key
     * @param <K> 键类型
     * @param <V> 值类型
     * @return RMap 实例
     */
    public <K, V> RMap<K, V> getMap(String key) {
        return redissonClient.getMap(key);
    }

    /**
     * Map 设置值
     *
     * @param key key
     * @param mapKey map key
     * @param value 值
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public <K, V> void mapPut(String key, K mapKey, V value) {
        RMap<K, V> map = redissonClient.getMap(key);
        map.put(mapKey, value);
    }

    /**
     * Map 获取值
     *
     * @param key key
     * @param mapKey map key
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 值
     */
    public <K, V> V mapGet(String key, K mapKey) {
        RMap<K, V> map = redissonClient.getMap(key);
        return map.get(mapKey);
    }

    // ==================== Set 操作 ====================

    /**
     * 获取 Set
     *
     * @param key key
     * @param <V> 值类型
     * @return RSet 实例
     */
    public <V> RSet<V> getSet(String key) {
        return redissonClient.getSet(key);
    }

    /**
     * Set 添加值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否添加成功
     */
    public <V> boolean setAdd(String key, V value) {
        RSet<V> set = redissonClient.getSet(key);
        return set.add(value);
    }

    /**
     * Set 删除值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否删除成功
     */
    public <V> boolean setRemove(String key, V value) {
        RSet<V> set = redissonClient.getSet(key);
        return set.remove(value);
    }

    // ==================== List 操作 ====================

    /**
     * 获取 List
     *
     * @param key key
     * @param <V> 值类型
     * @return RList 实例
     */
    public <V> RList<V> getList(String key) {
        return redissonClient.getList(key);
    }

    /**
     * List 添加值
     *
     * @param key key
     * @param value 值
     * @param <V> 值类型
     * @return 是否添加成功
     */
    public <V> boolean listAdd(String key, V value) {
        RList<V> list = redissonClient.getList(key);
        return list.add(value);
    }

    /**
     * List 获取值
     *
     * @param key key
     * @param index 索引
     * @param <V> 值类型
     * @return 值
     */
    public <V> V listGet(String key, int index) {
        RList<V> list = redissonClient.getList(key);
        return list.get(index);
    }

    // ==================== HyperLogLog 操作 ====================

    /**
     * 获取 HyperLogLog
     *
     * @param key key
     * @return RHyperLogLog 实例
     */
    public RHyperLogLog<Object> getHyperLogLog(String key) {
        return redissonClient.getHyperLogLog(key);
    }

    /**
     * HyperLogLog 添加值
     *
     * @param key    key
     * @param values 值集合
     * @return 添加的元素个数
     */
    public boolean hyperLogLogAdd(String key, Object... values) {
        RHyperLogLog<Object> hyperLogLog = redissonClient.getHyperLogLog(key);
        return hyperLogLog.add(values);
    }

    /**
     * HyperLogLog 获取基数
     *
     * @param key key
     * @return 基数
     */
    public long hyperLogLogCount(String key) {
        RHyperLogLog<Object> hyperLogLog = redissonClient.getHyperLogLog(key);
        return hyperLogLog.count();
    }

    // ==================== Bloom Filter 操作 ====================

    /**
     * 获取布隆过滤器
     *
     * @param key key
     * @param <T> 值类型
     * @return RBloomFilter 实例
     */
    public <T> RBloomFilter<T> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

    /**
     * 创建布隆过滤器
     *
     * @param key key
     * @param expectedInsertions 预期插入数量
     * @param falseProbability 误判率
     * @param <T> 值类型
     * @return RBloomFilter 实例
     */
    public <T> RBloomFilter<T> createBloomFilter(String key, long expectedInsertions, double falseProbability) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(key);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
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

    /**
     * Supplier 接口（用于带返回值的操作）
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
}