package com.junmo.platform.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.dao.DataAccessException;

/**
 * Job异常处理辅助类
 *
 * <p>统一处理Job层异常，消除重复的异常处理代码。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
public class JobExceptionHandler {

    /**
     * 处理Job执行异常
     *
     * @param jobName Job名称
     * @param taskName 任务名称
     * @param runnable 要执行的任务
     * @throws JobExecutionException Job执行异常
     */
    public static void execute(String jobName, String taskName, Runnable runnable) throws JobExecutionException {
        try {
            runnable.run();
        } catch (DataAccessException e) {
            log.error("[{}] 数据库访问失败，无法{}: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("数据库访问失败", e);
        } catch (IllegalArgumentException e) {
            log.error("[{}] 参数错误，无法{}: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("参数错误", e);
        } catch (RuntimeException e) {
            log.error("[{}] {}时发生运行时异常: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("运行时异常", e);
        } catch (Exception e) {
            log.error("[{}] {}时发生未知异常", jobName, taskName, e);
            throw new JobExecutionException("未知异常", e);
        }
    }

    /**
     * 处理Job执行异常（带返回值）
     *
     * @param jobName Job名称
     * @param taskName 任务名称
     * @param supplier 要执行的任务
     * @param <T> 返回值类型
     * @return 执行结果
     * @throws JobExecutionException Job执行异常
     */
    public static <T> T execute(String jobName, String taskName, java.util.function.Supplier<T> supplier) throws JobExecutionException {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            log.error("[{}] 数据库访问失败，无法{}: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("数据库访问失败", e);
        } catch (IllegalArgumentException e) {
            log.error("[{}] 参数错误，无法{}: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("参数错误", e);
        } catch (RuntimeException e) {
            log.error("[{}] {}时发生运行时异常: {}", jobName, taskName, e.getMessage(), e);
            throw new JobExecutionException("运行时异常", e);
        } catch (Exception e) {
            log.error("[{}] {}时发生未知异常", jobName, taskName, e);
            throw new JobExecutionException("未知异常", e);
        }
    }
}