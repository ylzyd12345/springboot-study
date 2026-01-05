package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.SeataExampleService;
import com.kev1n.spring4demo.core.service.SeataService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SeataController 测试
 *
 * <p>测试 SeataController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("Seata分布式事务控制器测试")
class SeataControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeataService seataService;

    @MockitoBean
    private SeataExampleService seataExampleService;

    @BeforeEach
    void setUp() {
        // SeataController 不需要登录，所以不需要 mockLogin
    }

    @Test
    @DisplayName("基本的分布式事务示例 - 成功")
    void basicTransaction_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/basic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("分布式事务执行成功"));

        verify(seataExampleService, times(1)).basicTransaction();
    }

    @Test
    @DisplayName("基本的分布式事务示例 - 失败")
    void basicTransaction_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("事务执行失败"))
                .when(seataExampleService).basicTransaction();

        // When & Then
        mockMvc.perform(post("/api/seata/basic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("分布式事务执行失败: 事务执行失败"));

        verify(seataExampleService, times(1)).basicTransaction();
    }

    @Test
    @DisplayName("跨数据源的分布式事务示例 - 成功")
    void transactionWithDataSource_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/datasource")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("跨数据源分布式事务执行成功"));

        verify(seataExampleService, times(1)).transactionWithDataSource();
    }

    @Test
    @DisplayName("跨数据源的分布式事务示例 - 失败")
    void transactionWithDataSource_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("跨数据源事务失败"))
                .when(seataExampleService).transactionWithDataSource();

        // When & Then
        mockMvc.perform(post("/api/seata/datasource")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("跨数据源分布式事务执行失败: 跨数据源事务失败"));

        verify(seataExampleService, times(1)).transactionWithDataSource();
    }

    @Test
    @DisplayName("事务回滚示例 - 成功")
    void transactionWithRollback_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/rollback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("分布式事务执行成功"));

        verify(seataExampleService, times(1)).transactionWithRollback();
    }

    @Test
    @DisplayName("事务回滚示例 - 回滚")
    void transactionWithRollback_Rollback() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("需要回滚的异常"))
                .when(seataExampleService).transactionWithRollback();

        // When & Then
        mockMvc.perform(post("/api/seata/rollback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("rollback"))
                .andExpect(jsonPath("$.message").value("分布式事务执行失败并回滚: 需要回滚的异常"))
                .andExpect(jsonPath("$.expected").value("这是预期的回滚行为"));

        verify(seataExampleService, times(1)).transactionWithRollback();
    }

    @Test
    @DisplayName("使用 SeataService 封装类示例 - 成功")
    void transactionWithSeataService_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/seata-service")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("使用 SeataService 的分布式事务执行成功"));

        verify(seataExampleService, times(1)).transactionWithSeataService();
    }

    @Test
    @DisplayName("使用 SeataService 封装类示例 - 失败")
    void transactionWithSeataService_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("SeataService执行失败"))
                .when(seataExampleService).transactionWithSeataService();

        // When & Then
        mockMvc.perform(post("/api/seata/seata-service")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("分布式事务执行失败: SeataService执行失败"));

        verify(seataExampleService, times(1)).transactionWithSeataService();
    }

    @Test
    @DisplayName("嵌套事务示例 - 成功")
    void nestedTransaction_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/nested")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("嵌套分布式事务执行成功"));

        verify(seataExampleService, times(1)).nestedTransaction();
    }

    @Test
    @DisplayName("嵌套事务示例 - 失败")
    void nestedTransaction_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("嵌套事务失败"))
                .when(seataExampleService).nestedTransaction();

        // When & Then
        mockMvc.perform(post("/api/seata/nested")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("嵌套分布式事务执行失败: 嵌套事务失败"));

        verify(seataExampleService, times(1)).nestedTransaction();
    }

    @Test
    @DisplayName("事务超时示例 - 成功")
    void transactionWithTimeout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/seata/timeout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("分布式事务执行成功"));

        verify(seataExampleService, times(1)).transactionWithTimeout();
    }

    @Test
    @DisplayName("事务超时示例 - 超时")
    void transactionWithTimeout_Timeout() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("事务超时"))
                .when(seataExampleService).transactionWithTimeout();

        // When & Then
        mockMvc.perform(post("/api/seata/timeout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("timeout"))
                .andExpect(jsonPath("$.message").value("分布式事务执行超时: 事务超时"))
                .andExpect(jsonPath("$.expected").value("这是预期的超时行为"));

        verify(seataExampleService, times(1)).transactionWithTimeout();
    }

    @Test
    @DisplayName("自定义分布式事务示例 - 成功")
    void customTransaction_Success() throws Exception {
        // Given
        when(seataService.executeInTransaction(anyString(), anyInt(), any())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/seata/custom")
                        .param("name", "custom-transaction")
                        .param("timeout", "60")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("自定义分布式事务执行成功"))
                .andExpect(jsonPath("$.transactionName").value("custom-transaction"))
                .andExpect(jsonPath("$.timeout").value(60));

        verify(seataService, times(1)).executeInTransaction("custom-transaction", 60, any());
    }

    @Test
    @DisplayName("自定义分布式事务示例 - 失败")
    void customTransaction_Failed() throws Exception {
        // Given
        when(seataService.executeInTransaction(anyString(), anyInt(), any())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/seata/custom")
                        .param("name", "custom-transaction")
                        .param("timeout", "60")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("自定义分布式事务执行失败"));

        verify(seataService, times(1)).executeInTransaction(anyString(), anyInt(), any());
    }

    @Test
    @DisplayName("自定义分布式事务示例 - 使用默认参数")
    void customTransaction_DefaultParams() throws Exception {
        // Given
        when(seataService.executeInTransaction(anyString(), anyInt(), any())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/seata/custom")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.transactionName").value("custom-seata-transaction"))
                .andExpect(jsonPath("$.timeout").value(60));

        verify(seataService, times(1)).executeInTransaction("custom-seata-transaction", 60, any());
    }

    @Test
    @DisplayName("查询事务状态示例 - 成功")
    void queryTransactionStatus_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/seata/status/test-xid-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("事务状态查询成功"))
                .andExpect(jsonPath("$.xid").value("test-xid-123"));

        verify(seataExampleService, times(1)).queryTransactionStatus("test-xid-123");
    }

    @Test
    @DisplayName("查询事务状态示例 - 失败")
    void queryTransactionStatus_Failed() throws Exception {
        // Given
        org.mockito.Mockito.doThrow(new RuntimeException("查询失败"))
                .when(seataExampleService).queryTransactionStatus(anyString());

        // When & Then
        mockMvc.perform(get("/api/seata/status/test-xid-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("事务状态查询失败: 查询失败"));

        verify(seataExampleService, times(1)).queryTransactionStatus("test-xid-123");
    }

    @Test
    @DisplayName("Seata 健康检查 - 正常")
    void health_Up() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/seata/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("up"))
                .andExpect(jsonPath("$.message").value("Seata 服务正常"));
    }

    @Test
    @DisplayName("Seata 健康检查 - 异常")
    void health_Down() throws Exception {
        // Given - 这个测试不需要Mock，因为health方法中没有实际调用任何服务
        // 如果需要测试异常情况，需要在health方法中添加实际的检查逻辑

        // When & Then
        mockMvc.perform(get("/api/seata/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("up"));
    }
}