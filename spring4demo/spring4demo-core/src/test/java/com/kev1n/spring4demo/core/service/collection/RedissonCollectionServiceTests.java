package com.kev1n.spring4demo.core.service.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedissonCollectionService 单元测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RedissonCollectionServiceTests {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RMap<String, String> rMap;

    @Mock
    private RSet<String> rSet;

    @Mock
    private RList<String> rList;

    @InjectMocks
    private com.kev1n.spring4demo.core.service.collection.impl.RedissonCollectionServiceImpl redissonCollectionService;

    private static final String COLLECTION_KEY = "test:collection";
    private static final String MAP_KEY = "map-key";
    private static final String MAP_VALUE = "map-value";
    private static final String SET_VALUE = "set-value";
    private static final String LIST_VALUE = "list-value";

    @BeforeEach
    void setUp() {
        reset(redissonClient, rMap, rSet, rList);
    }

    // ==================== Map 操作 ====================

    @Test
    void testGetMap() {
        // Given
        doReturn(rMap).when(redissonClient).getMap(anyString());

        // When
        RMap<String, String> map = redissonCollectionService.getMap(COLLECTION_KEY);

        // Then
        assertNotNull(map);
        verify(redissonClient).getMap(COLLECTION_KEY);
    }

    @Test
    void testMapPut() {
        // Given
        doReturn(rMap).when(redissonClient).getMap(anyString());
        when(rMap.put(anyString(), anyString())).thenReturn(null);

        // When
        redissonCollectionService.mapPut(COLLECTION_KEY, MAP_KEY, MAP_VALUE);

        // Then
        verify(rMap).put(MAP_KEY, MAP_VALUE);
    }

    @Test
    void testMapGet() {
        // Given
        doReturn(rMap).when(redissonClient).getMap(anyString());
        when(rMap.get(anyString())).thenReturn(MAP_VALUE);

        // When
        String result = redissonCollectionService.mapGet(COLLECTION_KEY, MAP_KEY);

        // Then
        assertEquals(MAP_VALUE, result);
        verify(rMap).get(MAP_KEY);
    }

    // ==================== Set 操作 ====================

    @Test
    void testGetSet() {
        // Given
        doReturn(rSet).when(redissonClient).getSet(anyString());

        // When
        RSet<String> set = redissonCollectionService.getSet(COLLECTION_KEY);

        // Then
        assertNotNull(set);
        verify(redissonClient).getSet(COLLECTION_KEY);
    }

    @Test
    void testSetAdd() {
        // Given
        doReturn(rSet).when(redissonClient).getSet(anyString());
        when(rSet.add(anyString())).thenReturn(true);

        // When
        boolean result = redissonCollectionService.setAdd(COLLECTION_KEY, SET_VALUE);

        // Then
        assertTrue(result);
        verify(rSet).add(SET_VALUE);
    }

    @Test
    void testSetRemove() {
        // Given
        doReturn(rSet).when(redissonClient).getSet(anyString());
        when(rSet.remove(anyString())).thenReturn(true);

        // When
        boolean result = redissonCollectionService.setRemove(COLLECTION_KEY, SET_VALUE);

        // Then
        assertTrue(result);
        verify(rSet).remove(SET_VALUE);
    }

    // ==================== List 操作 ====================

    @Test
    void testGetList() {
        // Given
        doReturn(rList).when(redissonClient).getList(anyString());

        // When
        RList<String> list = redissonCollectionService.getList(COLLECTION_KEY);

        // Then
        assertNotNull(list);
        verify(redissonClient).getList(COLLECTION_KEY);
    }

    @Test
    void testListAdd() {
        // Given
        doReturn(rList).when(redissonClient).getList(anyString());
        when(rList.add(anyString())).thenReturn(true);

        // When
        boolean result = redissonCollectionService.listAdd(COLLECTION_KEY, LIST_VALUE);

        // Then
        assertTrue(result);
        verify(rList).add(LIST_VALUE);
    }

    @Test
    void testListGet() {
        // Given
        doReturn(rList).when(redissonClient).getList(anyString());
        when(rList.get(anyInt())).thenReturn(LIST_VALUE);

        // When
        String result = redissonCollectionService.listGet(COLLECTION_KEY, 0);

        // Then
        assertEquals(LIST_VALUE, result);
        verify(rList).get(0);
    }
}