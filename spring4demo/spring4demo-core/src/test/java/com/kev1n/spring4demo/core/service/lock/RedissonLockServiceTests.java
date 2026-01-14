package com.kev1n.spring4demo.core.service.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * RedissonLockService 单元测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RedissonLockServiceTests {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @Mock
    private RReadWriteLock rReadWriteLock;

    @Mock
    private RSemaphore rSemaphore;

    @Mock
    private RCountDownLatch rCountDownLatch;

    @InjectMocks
    private com.kev1n.spring4demo.core.service.lock.impl.RedissonLockServiceImpl redissonLockService;

    private static final String LOCK_KEY = "test:lock";
    private static final String SEMAPHORE_KEY = "test:semaphore";
    private static final String COUNT_DOWN_LATCH_KEY = "test:countdownlatch";

    @BeforeEach
    void setUp() {
        // 重置mock对象
        reset(redissonClient, rLock, rReadWriteLock, rSemaphore, rCountDownLatch);
    }

    @Test
    void testGetLock() {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);

        // When
        RLock lock = redissonLockService.getLock(LOCK_KEY);

        // Then
        assertNotNull(lock);
        verify(redissonClient).getLock(LOCK_KEY);
    }

    @Test
    void testTryLock_Success() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // When
        boolean result = redissonLockService.tryLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS);

        // Then
        assertTrue(result);
        verify(rLock).tryLock(1, 10, TimeUnit.SECONDS);
    }

    @Test
    void testTryLock_Failed() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        // When
        boolean result = redissonLockService.tryLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS);

        // Then
        assertFalse(result);
        verify(rLock).tryLock(1, 10, TimeUnit.SECONDS);
    }

    @Test
    void testTryLock_InterruptedException() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenThrow(new InterruptedException("Thread interrupted"));

        // When
        boolean result = redissonLockService.tryLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS);

        // Then
        assertFalse(result);
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void testUnlock_Success() {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        doNothing().when(rLock).unlock();

        // When
        redissonLockService.unlock(LOCK_KEY);

        // Then
        verify(rLock).unlock();
    }

    @Test
    void testUnlock_NotHeldByCurrentThread() {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.isHeldByCurrentThread()).thenReturn(false);

        // When
        redissonLockService.unlock(LOCK_KEY);

        // Then
        verify(rLock, never()).unlock();
    }

    @Test
    void testExecuteWithLock_Success() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        Runnable action = mock(Runnable.class);

        // When
        boolean result = redissonLockService.executeWithLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS, action);

        // Then
        assertTrue(result);
        verify(action).run();
        verify(rLock).unlock();
    }

    @Test
    void testExecuteWithLock_WithReturnValue() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        java.util.function.Supplier<String> action = () -> "success";

        // When
        String result = redissonLockService.executeWithLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS, action);

        // Then
        assertEquals("success", result);
        verify(rLock).unlock();
    }

    @Test
    void testExecuteWithLock_FailedToAcquire() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);
        Runnable action = mock(Runnable.class);

        // When
        boolean result = redissonLockService.executeWithLock(LOCK_KEY, 1, 10, TimeUnit.SECONDS, action);

        // Then
        assertFalse(result);
        verify(action, never()).run();
    }

    @Test
    void testGetReadWriteLock() {
        // Given
        when(redissonClient.getReadWriteLock(anyString())).thenReturn(rReadWriteLock);

        // When
        RReadWriteLock readWriteLock = redissonLockService.getReadWriteLock(LOCK_KEY);

        // Then
        assertNotNull(readWriteLock);
        verify(redissonClient).getReadWriteLock(LOCK_KEY);
    }

    @Test
    void testGetFairLock() {
        // Given
        when(redissonClient.getFairLock(anyString())).thenReturn(rLock);

        // When
        RLock fairLock = redissonLockService.getFairLock(LOCK_KEY);

        // Then
        assertNotNull(fairLock);
        verify(redissonClient).getFairLock(LOCK_KEY);
    }

    @Test
    void testGetSemaphore() {
        // Given
        when(redissonClient.getSemaphore(anyString())).thenReturn(rSemaphore);

        // When
        RSemaphore semaphore = redissonLockService.getSemaphore(SEMAPHORE_KEY);

        // Then
        assertNotNull(semaphore);
        verify(redissonClient).getSemaphore(SEMAPHORE_KEY);
    }

    @Test
    void testGetCountDownLatch() {
        // Given
        when(redissonClient.getCountDownLatch(anyString())).thenReturn(rCountDownLatch);

        // When
        RCountDownLatch countDownLatch = redissonLockService.getCountDownLatch(COUNT_DOWN_LATCH_KEY);

        // Then
        assertNotNull(countDownLatch);
        verify(redissonClient).getCountDownLatch(COUNT_DOWN_LATCH_KEY);
    }
}