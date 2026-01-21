package com.junmo.platform.core.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Spring Task 示例任务类
 * 
 * 功能说明：
 * 1. 演示Spring Task的基本用法
 * 2. 演示不同类型的定时任务
 * 3. 演示异步任务执行
 * 
 * 使用场景：
 * - 固定频率执行的任务
 * - 固定延迟执行的任务
 * - Cron表达式定义的任务
 * - 异步执行的任务
 * 
 * @author junmo-platform
 * @version 1.0.0
 * @since 2025-12-24
 */
@Slf4j
@Component
public class SpringTaskDemo {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 固定频率执行的任务
     * 
     * 说明：每隔5秒执行一次
     * 适用场景：数据同步、缓存刷新等
     */
    @Scheduled(fixedRate = 5000)
    public void fixedRateTask() {
        log.info("[Spring Task] 固定频率任务执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 固定延迟执行的任务
     * 
     * 说明：上次任务执行完成后，延迟5秒再执行下一次
     * 适用场景：需要等待上一次任务完成的场景
     */
    @Scheduled(fixedDelay = 5000)
    public void fixedDelayTask() {
        log.info("[Spring Task] 固定延迟任务执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
    }

    /**
     * Cron表达式定义的任务
     * 
     * 说明：每天凌晨2点执行
     * Cron表达式格式：秒 分 时 日 月 周
     * 
     * 常用Cron表达式：
     * - 0 0 2 * * ? : 每天凌晨2点
     * - 0 0/5 * * * ? : 每5分钟
     * - 0 0 12 * * MON-FRI : 周一到周五中午12点
     * - 0 0 0 * * 6 : 每周六凌晨
     * - 0 0 0 1 * ? : 每月1号凌晨
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cronTask() {
        log.info("[Spring Task] Cron任务执行 - 每天凌晨2点 - 时间: {}", LocalDateTime.now().format(FORMATTER));
        // 执行业务逻辑，如数据清理、报表生成等
    }

    /**
     * 异步执行的任务
     * 
     * 说明：使用@Async注解标记，在独立线程池中执行
     * 适用场景：耗时较长的任务，如邮件发送、消息推送等
     */
    @Async("taskExecutor")
    @Scheduled(fixedRate = 10000)
    public void asyncTask() {
        log.info("[Spring Task] 异步任务开始执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
        
        try {
            // 模拟耗时操作
            Thread.sleep(2000);
            log.info("[Spring Task] 异步任务执行完成 - 时间: {}", LocalDateTime.now().format(FORMATTER));
        } catch (InterruptedException e) {
            log.error("[Spring Task] 异步任务执行异常", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 带初始延迟的任务
     * 
     * 说明：应用启动后延迟10秒开始执行，之后每隔5秒执行一次
     * 适用场景：需要等待系统初始化完成的任务
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5000)
    public void initialDelayTask() {
        log.info("[Spring Task] 带初始延迟的任务执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 每小时执行的任务
     * 
     * 说明：每小时的第0分钟执行
     * 适用场景：统计数据更新、缓存刷新等
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyTask() {
        log.info("[Spring Task] 每小时任务执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
    }

    /**
     * 每天执行的任务（工作日）
     * 
     * 说明：周一到周五每天上午9点执行
     * 适用场景：工作日报、工作提醒等
     */
    @Scheduled(cron = "0 0 9 ? * MON-FRI")
    public void weekdayTask() {
        log.info("[Spring Task] 工作日任务执行 - 时间: {}", LocalDateTime.now().format(FORMATTER));
    }
}