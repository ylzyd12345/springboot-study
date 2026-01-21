package com.junmo.platform.core.service.lock;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson分布式锁服务接口
 *
 * <p>提供分布式锁相关的操作，包括普通锁、读写锁、信号量、倒计时门栓等。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface RedissonLockService {

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁的 key
     * @return RLock 实例
     */
    RLock getLock(String lockKey);

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param lockKey 锁的 key
     */
    void unlock(String lockKey);

    /**
     * 执行带锁的操作（无返回值）
     *
     * @param lockKey 锁的 key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @param action 要执行的操作
     * @return 是否执行成功
     */
    boolean executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable action);

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
    <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> action);

    /**
     * 获取读写锁
     *
     * @param lockKey 锁的 key
     * @return RReadWriteLock 实例
     */
    RReadWriteLock getReadWriteLock(String lockKey);

    /**
     * 获取公平锁
     *
     * @param lockKey 锁的 key
     * @return RLock 实例
     */
    RLock getFairLock(String lockKey);

    /**
     * 获取信号量
     *
     * @param semaphoreKey 信号量的 key
     * @return RSemaphore 实例
     */
    RSemaphore getSemaphore(String semaphoreKey);

    /**
     * 获取倒计时门栓
     *
     * @param countDownLatchKey 倒计时门栓的 key
     * @return RCountDownLatch 实例
     */
    RCountDownLatch getCountDownLatch(String countDownLatchKey);
}