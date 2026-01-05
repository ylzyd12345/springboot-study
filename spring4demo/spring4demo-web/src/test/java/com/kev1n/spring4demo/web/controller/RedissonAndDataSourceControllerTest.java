package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.DynamicDataSourceService;
import com.kev1n.spring4demo.core.service.RedissonService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RedissonAndDataSourceController 测试
 *
 * <p>测试 RedissonAndDataSourceController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("Redisson和动态数据源控制器测试")
class RedissonAndDataSourceControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedissonService redissonService;

    @MockitoBean
    private DynamicDataSourceService dynamicDataSourceService;

    @BeforeEach
    void setUp() {
        // RedissonAndDataSourceController 不需要登录，所以不需要 mockLogin
    }

    // ==================== Redisson 分布式锁测试 ====================

    @Test
    @DisplayName("Redisson 分布式锁示例 - 获取锁成功")
    void redissonLock_Success() throws Exception {
        // Given
        when(redissonService.tryLock(anyString(), anyLong(), anyLong(), any())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/demo/redisson/lock")
                        .param("lockKey", "test_lock")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("获取锁成功"))
                .andExpect(jsonPath("$.lockKey").value("test_lock"))
                .andExpect(jsonPath("$.unlock").value("锁已释放"));

        verify(redissonService, times(1)).tryLock("test_lock", 10, 30, null);
        verify(redissonService, times(1)).unlock("test_lock");
    }

    @Test
    @DisplayName("Redisson 分布式锁示例 - 获取锁失败")
    void redissonLock_Failed() throws Exception {
        // Given
        when(redissonService.tryLock(anyString(), anyLong(), anyLong(), any())).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/demo/redisson/lock")
                        .param("lockKey", "test_lock")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("获取锁失败"))
                .andExpect(jsonPath("$.lockKey").value("test_lock"));

        verify(redissonService, times(1)).tryLock("test_lock", 10, 30, null);
        verify(redissonService, times(0)).unlock(anyString());
    }

    // ==================== Redisson 缓存测试 ====================

    @Test
    @DisplayName("Redisson 缓存示例 - 设置缓存")
    void redissonCache_Set() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/demo/redisson/cache")
                        .param("key", "test_key")
                        .param("value", "test_value")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("缓存设置成功"))
                .andExpect(jsonPath("$.key").value("test_key"))
                .andExpect(jsonPath("$.value").value("test_value"))
                .andExpect(jsonPath("$.expireTime").value("60s"));

        verify(redissonService, times(1)).set("test_key", "test_value", 60, null);
    }

    @Test
    @DisplayName("Redisson 获取缓存示例 - 缓存存在")
    void getRedissonCache_Exists() throws Exception {
        // Given
        when(redissonService.get("test_key")).thenReturn("test_value");

        // When & Then
        mockMvc.perform(get("/api/demo/redisson/cache")
                        .param("key", "test_key")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("缓存获取成功"))
                .andExpect(jsonPath("$.key").value("test_key"))
                .andExpect(jsonPath("$.value").value("test_value"))
                .andExpect(jsonPath("$.exists").value(true));

        verify(redissonService, times(1)).get("test_key");
    }

    @Test
    @DisplayName("Redisson 获取缓存示例 - 缓存不存在")
    void getRedissonCache_NotExists() throws Exception {
        // Given
        when(redissonService.get("test_key")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/demo/redisson/cache")
                        .param("key", "test_key")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("not_found"))
                .andExpect(jsonPath("$.message").value("缓存不存在"))
                .andExpect(jsonPath("$.key").value("test_key"))
                .andExpect(jsonPath("$.exists").value(false));

        verify(redissonService, times(1)).get("test_key");
    }

    // ==================== 动态数据源测试 ====================

    @Test
    @DisplayName("动态数据源 - 写入主库示例 - 成功")
    void writeToMaster_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/demo/datasource/write")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("数据已写入主库 (master)"))
                .andExpect(jsonPath("$.datasource").value("master"));

        verify(dynamicDataSourceService, times(1)).writeToMaster();
    }

    @Test
    @DisplayName("动态数据源 - 写入主库示例 - 失败")
    void writeToMaster_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("写入失败"))
                .when(dynamicDataSourceService).writeToMaster();

        // When & Then
        mockMvc.perform(post("/api/demo/datasource/write")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("写入失败: 写入失败"));

        verify(dynamicDataSourceService, times(1)).writeToMaster();
    }

    @Test
    @DisplayName("动态数据源 - 从从库读取示例 - 成功")
    void readFromSlave_Success() throws Exception {
        // Given
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "test1");
        mockData.add(row1);

        when(dynamicDataSourceService.readFromSlave()).thenReturn(mockData);

        // When & Then
        mockMvc.perform(get("/api/demo/datasource/read")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("数据已从从库 (slave) 读取"))
                .andExpect(jsonPath("$.datasource").value("slave"))
                .andExpect(jsonPath("$.dataCount").value(1));

        verify(dynamicDataSourceService, times(1)).readFromSlave();
    }

    @Test
    @DisplayName("动态数据源 - 从从库读取示例 - 失败")
    void readFromSlave_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("读取失败"))
                .when(dynamicDataSourceService).readFromSlave();

        // When & Then
        mockMvc.perform(get("/api/demo/datasource/read")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("读取失败: 读取失败"));

        verify(dynamicDataSourceService, times(1)).readFromSlave();
    }

    @Test
    @DisplayName("动态数据源 - 读写分离示例 - 成功")
    void readWriteSeparation_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/demo/datasource/readwrite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("读写分离操作完成"))
                .andExpect(jsonPath("$.writeDatasource").value("master"))
                .andExpect(jsonPath("$.readDatasource").value("slave"));

        verify(dynamicDataSourceService, times(1)).readWriteSeparation();
    }

    @Test
    @DisplayName("动态数据源 - 读写分离示例 - 失败")
    void readWriteSeparation_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("操作失败"))
                .when(dynamicDataSourceService).readWriteSeparation();

        // When & Then
        mockMvc.perform(post("/api/demo/datasource/readwrite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("操作失败: 操作失败"));

        verify(dynamicDataSourceService, times(1)).readWriteSeparation();
    }

    // ==================== Redisson 数据结构测试 ====================

    @Test
    @DisplayName("Redisson Map 操作示例 - 成功")
    void redissonMap_Success() throws Exception {
        // Given
        when(redissonService.mapGet(eq("test_map"), eq("map_key"))).thenReturn("map_value");

        // When & Then
        mockMvc.perform(post("/api/demo/redisson/map")
                        .param("key", "test_map")
                        .param("mapKey", "map_key")
                        .param("value", "map_value")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Map 操作成功"))
                .andExpect(jsonPath("$.key").value("test_map"))
                .andExpect(jsonPath("$.mapKey").value("map_key"))
                .andExpect(jsonPath("$.value").value("map_value"))
                .andExpect(jsonPath("$.retrievedValue").value("map_value"));

        verify(redissonService, times(1)).mapPut("test_map", "map_key", "map_value");
        verify(redissonService, times(1)).mapGet("test_map", "map_key");
    }

    @Test
    @DisplayName("Redisson Set 操作示例 - 成功")
    void redissonSet_Success() throws Exception {
        // Given
        when(redissonService.setAdd(eq("test_set"), eq("set_value"))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/demo/redisson/set")
                        .param("key", "test_set")
                        .param("value", "set_value")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Set 操作成功"))
                .andExpect(jsonPath("$.key").value("test_set"))
                .andExpect(jsonPath("$.value").value("set_value"))
                .andExpect(jsonPath("$.added").value(true));

        verify(redissonService, times(1)).setAdd("test_set", "set_value");
    }

    @Test
    @DisplayName("Redisson List 操作示例 - 成功")
    void redissonList_Success() throws Exception {
        // Given
        when(redissonService.listAdd(eq("test_list"), eq("list_value"))).thenReturn(true);
        when(redissonService.listGet(eq("test_list"), eq(0))).thenReturn("list_value");

        // When & Then
        mockMvc.perform(post("/api/demo/redisson/list")
                        .param("key", "test_list")
                        .param("value", "list_value")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("List 操作成功"))
                .andExpect(jsonPath("$.key").value("test_list"))
                .andExpect(jsonPath("$.value").value("list_value"))
                .andExpect(jsonPath("$.added").value(true))
                .andExpect(jsonPath("$.firstValue").value("list_value"));

        verify(redissonService, times(1)).listAdd("test_list", "list_value");
        verify(redissonService, times(1)).listGet("test_list", 0);
    }

    @Test
    @DisplayName("Redisson 分布式锁示例 - 使用默认锁key")
    void redissonLock_DefaultKey() throws Exception {
        // Given
        when(redissonService.tryLock(anyString(), anyLong(), anyLong(), any())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/demo/redisson/lock")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.lockKey").value("test_lock"));

        verify(redissonService, times(1)).tryLock("test_lock", 10, 30, null);
    }
}