package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.JobScheduleService;
import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JobScheduleController 测试
 *
 * <p>测试 JobScheduleController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("任务调度控制器测试")
class JobScheduleControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobScheduleService jobScheduleService;

    @BeforeEach
    void setUp() {
        // JobScheduleController 不需要登录，所以不需要 mockLogin
    }

    @Test
    @DisplayName("手动触发任务 - 成功")
    void triggerJob_Success() throws Exception {
        // Given
        when(jobScheduleService.triggerJob(1L, "test-param")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/trigger")
                        .param("executorParam", "test-param")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务触发成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(jobScheduleService, times(1)).triggerJob(1L, "test-param");
    }

    @Test
    @DisplayName("手动触发任务 - 失败")
    void triggerJob_Failed() throws Exception {
        // Given
        when(jobScheduleService.triggerJob(1L, null)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/trigger")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("任务触发失败"));

        verify(jobScheduleService, times(1)).triggerJob(1L, null);
    }

    @Test
    @DisplayName("手动触发任务 - 任务不存在")
    void triggerJob_NotFound() throws Exception {
        // Given
        when(jobScheduleService.triggerJob(999L, null)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/jobs/999/trigger")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("任务触发失败"));

        verify(jobScheduleService, times(1)).triggerJob(999L, null);
    }

    @Test
    @DisplayName("停止任务 - 成功")
    void stopJob_Success() throws Exception {
        // Given
        when(jobScheduleService.stopJob(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/stop")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务停止成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(jobScheduleService, times(1)).stopJob(1L);
    }

    @Test
    @DisplayName("停止任务 - 失败")
    void stopJob_Failed() throws Exception {
        // Given
        when(jobScheduleService.stopJob(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/stop")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("任务停止失败"));

        verify(jobScheduleService, times(1)).stopJob(1L);
    }

    @Test
    @DisplayName("启动任务 - 成功")
    void startJob_Success() throws Exception {
        // Given
        when(jobScheduleService.startJob(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/start")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务启动成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(jobScheduleService, times(1)).startJob(1L);
    }

    @Test
    @DisplayName("启动任务 - 失败")
    void startJob_Failed() throws Exception {
        // Given
        when(jobScheduleService.startJob(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/start")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("任务启动失败"));

        verify(jobScheduleService, times(1)).startJob(1L);
    }

    @Test
    @DisplayName("获取任务执行日志 - 成功")
    void getJobLog_Success() throws Exception {
        // Given
        String logContent = "2025-01-05 10:00:00 - 任务开始执行\n" +
                           "2025-01-05 10:00:05 - 任务执行成功\n" +
                           "2025-01-05 10:00:10 - 任务结束";

        when(jobScheduleService.getJobLog(1L, "2025-01-05")).thenReturn(logContent);

        // When & Then
        mockMvc.perform(get("/api/jobs/logs/1")
                        .param("logDateTim", "2025-01-05")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(logContent));

        verify(jobScheduleService, times(1)).getJobLog(1L, "2025-01-05");
    }

    @Test
    @DisplayName("获取任务执行日志 - 日志不存在")
    void getJobLog_NotFound() throws Exception {
        // Given
        when(jobScheduleService.getJobLog(999L, "2025-01-05")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/jobs/logs/999")
                        .param("logDateTim", "2025-01-05")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(jobScheduleService, times(1)).getJobLog(999L, "2025-01-05");
    }

    @Test
    @DisplayName("获取任务执行日志 - 系统异常")
    void getJobLog_SystemError() throws Exception {
        // Given
        when(jobScheduleService.getJobLog(1L, "2025-01-05"))
                .thenThrow(new RuntimeException("获取日志失败"));

        // When & Then
        mockMvc.perform(get("/api/jobs/logs/1")
                        .param("logDateTim", "2025-01-05")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("系统异常，获取日志失败"));

        verify(jobScheduleService, times(1)).getJobLog(1L, "2025-01-05");
    }

    @Test
    @DisplayName("手动触发任务 - 带执行器参数")
    void triggerJob_WithExecutorParam() throws Exception {
        // Given
        String executorParam = "{\"key\":\"value\"}";
        when(jobScheduleService.triggerJob(1L, executorParam)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/trigger")
                        .param("executorParam", executorParam)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务触发成功"));

        verify(jobScheduleService, times(1)).triggerJob(1L, executorParam);
    }

    @Test
    @DisplayName("手动触发任务 - 空执行器参数")
    void triggerJob_EmptyExecutorParam() throws Exception {
        // Given
        when(jobScheduleService.triggerJob(1L, "")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/jobs/1/trigger")
                        .param("executorParam", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(jobScheduleService, times(1)).triggerJob(1L, "");
    }
}