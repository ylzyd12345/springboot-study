package com.junmo.platform.core.service.lock.impl;

import com.junmo.platform.core.service.lock.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson分布式锁服务实现类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonLockServiceImpl implements RedissonLockService {

    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁失败，线程被中断: lockKey={}", lockKey, e);
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
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

    @Override
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

    @Override
    public RReadWriteLock getReadWriteLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey);
    }

    @Override
    public RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    @Override
    public RSemaphore getSemaphore(String semaphoreKey) {
        return redissonClient.getSemaphore(semaphoreKey);
    }

    @Override
    public RCountDownLatch getCountDownLatch(String countDownLatchKey) {
        return redissonClient.getCountDownLatch(countDownLatchKey);
    }
}