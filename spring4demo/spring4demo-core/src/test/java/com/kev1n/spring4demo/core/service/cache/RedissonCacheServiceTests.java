package com.kev1n.spring4demo.core.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * RedissonCacheService 单元测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RedissonCacheServiceTests {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<Object> rBucket;

    @InjectMocks
    private com.kev1n.spring4demo.core.service.cache.impl.RedissonCacheServiceImpl redissonCacheService;

    private static final String CACHE_KEY = "test:cache";
    private static final String CACHE_VALUE = "test-value";

    @BeforeEach
    void setUp() {
        reset(redissonClient, rBucket);
    }

    @Test
    void testGetBucket() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);

        // When
        RBucket<String> bucket = redissonCacheService.getBucket(CACHE_KEY);

        // Then
        assertNotNull(bucket);
        verify(redissonClient).getBucket(CACHE_KEY);
    }

    @Test
    void testSet() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        doNothing().when(rBucket).set(any());

        // When
        redissonCacheService.set(CACHE_KEY, CACHE_VALUE);

        // Then
        verify(rBucket).set(CACHE_VALUE);
    }

    @Test
    void testSetWithExpire() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        doNothing().when(rBucket).set(any(), anyLong(), any(TimeUnit.class));

        // When
        redissonCacheService.set(CACHE_KEY, CACHE_VALUE, 10, TimeUnit.SECONDS);

        // Then
        verify(rBucket).set(CACHE_VALUE, 10, TimeUnit.SECONDS);
    }

    @Test
    void testGet() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        when(rBucket.get()).thenReturn(CACHE_VALUE);

        // When
        String result = redissonCacheService.get(CACHE_KEY);

        // Then
        assertEquals(CACHE_VALUE, result);
        verify(rBucket).get();
    }

    @Test
    void testGet_NotFound() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        when(rBucket.get()).thenReturn(null);

        // When
        String result = redissonCacheService.get(CACHE_KEY);

        // Then
        assertNull(result);
    }

    @Test
    void testDelete() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        when(rBucket.delete()).thenReturn(true);

        // When
        boolean result = redissonCacheService.delete(CACHE_KEY);

        // Then
        assertTrue(result);
        verify(rBucket).delete();
    }

    @Test
    void testExists() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        when(rBucket.isExists()).thenReturn(true);

        // When
        boolean result = redissonCacheService.exists(CACHE_KEY);

        // Then
        assertTrue(result);
        verify(rBucket).isExists();
    }

    @Test
    void testExpire() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        doNothing().when(rBucket).expire(anyLong(), any(TimeUnit.class));

        // When
        redissonCacheService.expire(CACHE_KEY, 10, TimeUnit.SECONDS);

        // Then
        verify(rBucket).expire(10, TimeUnit.SECONDS);
    }

    @Test
    void testGetExpire() {
        // Given
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);
        when(rBucket.remainTimeToLive()).thenReturn(10000L);

        // When
        long result = redissonCacheService.getExpire(CACHE_KEY);

        // Then
        assertEquals(10000L, result);
        verify(rBucket).remainTimeToLive();
    }
}