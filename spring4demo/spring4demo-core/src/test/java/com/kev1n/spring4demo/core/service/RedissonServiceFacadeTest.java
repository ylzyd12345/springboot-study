package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.core.service.advanced.RedissonAdvancedDataService;
import com.kev1n.spring4demo.core.service.cache.RedissonCacheService;
import com.kev1n.spring4demo.core.service.collection.RedissonCollectionService;
import com.kev1n.spring4demo.core.service.lock.RedissonLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Redisson服务门面测试类
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Redisson服务门面测试")
class RedissonServiceFacadeTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RedissonLockService lockService;

    @Mock
    private RedissonCacheService cacheService;

    @Mock
    private RedissonCollectionService collectionService;

    @Mock
    private RedissonAdvancedDataService advancedDataService;

    @Mock
    private RKeys rKeys;

    private RedissonServiceFacade facade;

    @BeforeEach
    void setUp() {
        facade = new RedissonServiceFacade(redissonClient, lockService, cacheService,
                collectionService, advancedDataService);
    }

    @Test
    @DisplayName("获取Redisson客户端")
    void testGetRedissonClient() {
        // When
        RedissonClient result = facade.getRedissonClient();

        // Then
        assertThat(result).isEqualTo(redissonClient);
    }

    @Test
    @DisplayName("获取分布式锁服务")
    void testGetLockService() {
        // When
        RedissonLockService result = facade.lock();

        // Then
        assertThat(result).isEqualTo(lockService);
    }

    @Test
    @DisplayName("获取缓存服务")
    void testGetCacheService() {
        // When
        RedissonCacheService result = facade.cache();

        // Then
        assertThat(result).isEqualTo(cacheService);
    }

    @Test
    @DisplayName("获取集合服务")
    void testGetCollectionService() {
        // When
        RedissonCollectionService result = facade.collection();

        // Then
        assertThat(result).isEqualTo(collectionService);
    }

    @Test
    @DisplayName("获取高级数据结构服务")
    void testGetAdvancedDataService() {
        // When
        RedissonAdvancedDataService result = facade.advanced();

        // Then
        assertThat(result).isEqualTo(advancedDataService);
    }

    @Test
    @DisplayName("获取所有keys")
    void testKeys() {
        // Given
        String pattern = "test:*";
        Iterable<String> expectedKeys = Arrays.asList("test:key1", "test:key2");
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.getKeysByPattern(pattern)).thenReturn(expectedKeys);

        // When
        Iterable<String> result = facade.keys(pattern);

        // Then
        assertThat(result).isEqualTo(expectedKeys);
        verify(redissonClient).getKeys();
        verify(rKeys).getKeysByPattern(pattern);
    }

    @Test
    @DisplayName("批量删除keys")
    void testDelete() {
        // Given
        Collection<String> keys = Arrays.asList("key1", "key2", "key3");
        long expectedDeleteCount = 3L;
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.delete(any(String[].class))).thenReturn(expectedDeleteCount);

        // When
        long result = facade.delete(keys);

        // Then
        assertThat(result).isEqualTo(expectedDeleteCount);
        verify(redissonClient).getKeys();
        verify(rKeys).delete(any(String[].class));
    }

    @Test
    @DisplayName("获取数据库大小")
    void testDbSize() {
        // Given
        long expectedSize = 100L;
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.count()).thenReturn(expectedSize);

        // When
        long result = facade.dbSize();

        // Then
        assertThat(result).isEqualTo(expectedSize);
        verify(redissonClient).getKeys();
        verify(rKeys).count();
    }

    @Test
    @DisplayName("刷新key的过期时间")
    void testExpireAll() {
        // Given
        String key = "test:key";
        long time = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.expire(eq(key), eq(time), eq(timeUnit))).thenReturn(true);

        // When
        facade.expireAll(key, time, timeUnit);

        // Then
        verify(redissonClient).getKeys();
        verify(rKeys).expire(eq(key), eq(time), eq(timeUnit));
    }

    @Test
    @DisplayName("批量删除空keys集合")
    void testDeleteEmptyKeys() {
        // Given
        Collection<String> keys = Arrays.asList();
        long expectedDeleteCount = 0L;
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.delete(any(String[].class))).thenReturn(expectedDeleteCount);

        // When
        long result = facade.delete(keys);

        // Then
        assertThat(result).isEqualTo(expectedDeleteCount);
        verify(redissonClient).getKeys();
        verify(rKeys).delete(any(String[].class));
    }

    @Test
    @DisplayName("获取keys时pattern为空")
    void testKeysWithEmptyPattern() {
        // Given
        String pattern = "";
        Iterable<String> expectedKeys = Arrays.asList();
        when(redissonClient.getKeys()).thenReturn(rKeys);
        when(rKeys.getKeysByPattern(pattern)).thenReturn(expectedKeys);

        // When
        Iterable<String> result = facade.keys(pattern);

        // Then
        assertThat(result).isEqualTo(expectedKeys);
        verify(redissonClient).getKeys();
        verify(rKeys).getKeysByPattern(pattern);
    }
}