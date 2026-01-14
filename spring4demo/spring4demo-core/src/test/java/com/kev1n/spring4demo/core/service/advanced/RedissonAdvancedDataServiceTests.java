package com.kev1n.spring4demo.core.service.advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedissonAdvancedDataService 单元测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RedissonAdvancedDataServiceTests {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RHyperLogLog<Object> rHyperLogLog;

    @Mock
    private RBloomFilter<String> rBloomFilter;

    @InjectMocks
    private com.kev1n.spring4demo.core.service.advanced.impl.RedissonAdvancedDataServiceImpl redissonAdvancedDataService;

    private static final String ADVANCED_KEY = "test:advanced";
    private static final String VALUE = "test-value";

    @BeforeEach
    void setUp() {
        reset(redissonClient, rHyperLogLog, rBloomFilter);
    }

    // ==================== HyperLogLog 操作 ====================

    @Test
    void testGetHyperLogLog() {
        // Given
        when(redissonClient.getHyperLogLog(anyString())).thenReturn(rHyperLogLog);

        // When
        RHyperLogLog<Object> hyperLogLog = redissonAdvancedDataService.getHyperLogLog(ADVANCED_KEY);

        // Then
        assertNotNull(hyperLogLog);
        verify(redissonClient).getHyperLogLog(ADVANCED_KEY);
    }

    @Test
    void testHyperLogLogAdd() {
        // Given
        when(redissonClient.getHyperLogLog(anyString())).thenReturn(rHyperLogLog);
        when(rHyperLogLog.add(any())).thenReturn(true);

        // When
        boolean result = redissonAdvancedDataService.hyperLogLogAdd(ADVANCED_KEY, VALUE, "value2", "value3");

        // Then
        assertTrue(result);
        verify(rHyperLogLog).add(any(Object[].class));
    }

    @Test
    void testHyperLogLogCount() {
        // Given
        when(redissonClient.getHyperLogLog(anyString())).thenReturn(rHyperLogLog);
        when(rHyperLogLog.count()).thenReturn(100L);

        // When
        long result = redissonAdvancedDataService.hyperLogLogCount(ADVANCED_KEY);

        // Then
        assertEquals(100L, result);
        verify(rHyperLogLog).count();
    }

    // ==================== Bloom Filter 操作 ====================

    @Test
    void testGetBloomFilter() {
        // Given
        doReturn(rBloomFilter).when(redissonClient).getBloomFilter(anyString());

        // When
        RBloomFilter<String> bloomFilter = redissonAdvancedDataService.getBloomFilter(ADVANCED_KEY);

        // Then
        assertNotNull(bloomFilter);
        verify(redissonClient).getBloomFilter(ADVANCED_KEY);
    }

    @Test
    void testCreateBloomFilter() {
        // Given
        doReturn(rBloomFilter).when(redissonClient).getBloomFilter(anyString());
        when(rBloomFilter.tryInit(anyLong(), anyDouble())).thenReturn(true);

        // When
        RBloomFilter<String> bloomFilter = redissonAdvancedDataService.createBloomFilter(
            ADVANCED_KEY, 10000, 0.01);

        // Then
        assertNotNull(bloomFilter);
        verify(rBloomFilter).tryInit(10000, 0.01);
    }

    @Test
    void testBloomFilterContains() {
        // Given
        doReturn(rBloomFilter).when(redissonClient).getBloomFilter(anyString());
        when(rBloomFilter.contains(anyString())).thenReturn(true);

        // When
        boolean result = redissonAdvancedDataService.bloomFilterContains(ADVANCED_KEY, VALUE);

        // Then
        assertTrue(result);
        verify(rBloomFilter).contains(VALUE);
    }

    @Test
    void testBloomFilterContains_NotFound() {
        // Given
        doReturn(rBloomFilter).when(redissonClient).getBloomFilter(anyString());
        when(rBloomFilter.contains(anyString())).thenReturn(false);

        // When
        boolean result = redissonAdvancedDataService.bloomFilterContains(ADVANCED_KEY, VALUE);

        // Then
        assertFalse(result);
        verify(rBloomFilter).contains(VALUE);
    }
}