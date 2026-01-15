package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.api.dto.ApiResponse;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.service.JobScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务调度控制器
 * 
 * <p>提供任务调度的 REST API，包括任务触发、停止、启动、日志查询等功能。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "任务调度", description = "任务调度相关接口")
public class JobScheduleController {

    private final JobScheduleService jobScheduleService;

    /**
     * 手动触发任务
     * 
     * @param jobId 任务ID
     * @param executorParam 执行器参数（可选）
     * @return 触发结果
     */
    @PostMapping("/{jobId}/trigger")
    @Operation(summary = "手动触发任务", description = "手动触发指定任务执行")
    public ResponseEntity<ApiResponse<Boolean>> triggerJob(
            @Parameter(description = "任务ID") @PathVariable Long jobId,
            @Parameter(description = "执行器参数") @RequestParam(required = false) String executorParam) {
        
        log.info("手动触发任务请求: jobId={}, executorParam={}", jobId, executorParam);
        
        try {
            boolean result = jobScheduleService.triggerJob(jobId, executorParam);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("任务触发成功", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("任务触发失败"));
            }
        } catch (IllegalArgumentException e) {
            log.error("任务参数错误: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("任务参数错误"));
        } catch (BusinessException e) {
            log.error("任务触发失败: jobId={}, error={}", jobId, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务触发失败"));
        } catch (Exception e) {
            log.error("手动触发任务失败: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务触发失败"));
        }
    }

    /**
     * 停止任务
     * 
     * @param jobId 任务ID
     * @return 停止结果
     */
    @PostMapping("/{jobId}/stop")
    @Operation(summary = "停止任务", description = "停止正在运行的任务")
    public ResponseEntity<ApiResponse<Boolean>> stopJob(
            @Parameter(description = "任务ID") @PathVariable Long jobId) {
        
        log.info("停止任务请求: jobId={}", jobId);
        
        try {
            boolean result = jobScheduleService.stopJob(jobId);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("任务停止成功", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("任务停止失败"));
            }
        } catch (IllegalArgumentException e) {
            log.error("任务参数错误: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("任务参数错误"));
        } catch (BusinessException e) {
            log.error("任务停止失败: jobId={}, error={}", jobId, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务停止失败"));
        } catch (Exception e) {
            log.error("停止任务失败: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务停止失败"));
        }
    }

    /**
     * 启动任务
     * 
     * @param jobId 任务ID
     * @return 启动结果
     */
    @PostMapping("/{jobId}/start")
    @Operation(summary = "启动任务", description = "启动已停止的任务")
    public ResponseEntity<ApiResponse<Boolean>> startJob(
            @Parameter(description = "任务ID") @PathVariable Long jobId) {
        
        log.info("启动任务请求: jobId={}", jobId);
        
        try {
            boolean result = jobScheduleService.startJob(jobId);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("任务启动成功", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("任务启动失败"));
            }
        } catch (IllegalArgumentException e) {
            log.error("任务参数错误: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("任务参数错误"));
        } catch (BusinessException e) {
            log.error("任务启动失败: jobId={}, error={}", jobId, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务启动失败"));
        } catch (Exception e) {
            log.error("启动任务失败: jobId={}", jobId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，任务启动失败"));
        }
    }

    /**
     * 获取任务执行日志
     * 
     * @param logId 日志ID
     * @param logDateTim 日志日期
     * @return 日志内容
     */
    @GetMapping("/logs/{logId}")
    @Operation(summary = "获取任务执行日志", description = "获取指定任务的执行日志")
    public ResponseEntity<ApiResponse<String>> getJobLog(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Parameter(description = "日志日期") @RequestParam String logDateTim) {
        
        log.info("获取任务执行日志请求: logId={}, logDateTim={}", logId, logDateTim);
        
        try {
            String logContent = jobScheduleService.getJobLog(logId, logDateTim);
            return ResponseEntity.ok(ApiResponse.success(logContent));
        } catch (IllegalArgumentException e) {
            log.error("日志参数错误: logId={}, logDateTim={}", logId, logDateTim, e);
            return ResponseEntity.ok(ApiResponse.error("日志参数错误"));
        } catch (BusinessException e) {
            log.error("获取任务执行日志失败: logId={}, error={}", logId, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("运行时异常: logId={}", logId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，获取日志失败"));
        } catch (Exception e) {
            log.error("获取任务执行日志失败: logId={}", logId, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，获取日志失败"));
        }
    }
}