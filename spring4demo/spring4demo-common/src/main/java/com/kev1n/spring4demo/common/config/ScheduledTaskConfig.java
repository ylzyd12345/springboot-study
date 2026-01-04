package com.kev1n.spring4demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Spring Task 配置类
 * 
 * 功能说明：
 * 1. 启用定时任务调度 (@EnableScheduling)
 * 2. 启用异步任务执行 (@EnableAsync)
 * 3. 配置异步任务线程池
 * 
 * 适用场景：
 * - 简单定时任务（数据清理、缓存刷新等）
 * - 异步任务执行（邮件发送、消息推送等）
 * - 固定频率/固定延迟任务
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-24
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ScheduledTaskConfig {

    /**
     * 配置异步任务线程池
     * 
     * 线程池配置：
     * - 核心线程数：5
     * - 最大线程数：10
     * - 队列容量：25
     * - 线程名前缀：async-task-
     * - 拒绝策略：CallerRunsPolicy（由调用线程执行任务）
     * 
     * @return 异步任务执行器
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(5);
        
        // 最大线程数
        executor.setMaxPoolSize(10);
        
        // 队列容量
        executor.setQueueCapacity(25);
        
        // 线程名前缀
        executor.setThreadNamePrefix("async-task-");
        
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        // 拒绝策略：由调用线程执行该任务
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // 初始化
        executor.initialize();
        
        return executor;
    }

    /**
     * 配置定时任务专用线程池
     * 
     * 线程池配置：
     * - 核心线程数：2
     * - 最大线程数：5
     * - 队列容量：10
     * - 线程名前缀：scheduled-task-
     * 
     * @return 定时任务执行器
     */
    @Bean(name = "scheduledTaskExecutor")
    public Executor scheduledTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数（定时任务通常不需要太多线程）
        executor.setCorePoolSize(2);
        
        // 最大线程数
        executor.setMaxPoolSize(5);
        
        // 队列容量
        executor.setQueueCapacity(10);
        
        // 线程名前缀
        executor.setThreadNamePrefix("scheduled-task-");
        
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        // 拒绝策略：由调用线程执行该任务
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // 初始化
        executor.initialize();
        
        return executor;
    }
}