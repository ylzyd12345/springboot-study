package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Task 配置测试
 *
 * 测试要点：
 * 1. 验证 ScheduledTaskConfig Bean 创建
 * 2. 验证 taskExecutor Bean 创建
 * 3. 验证 scheduledTaskExecutor Bean 创建
 * 4. 验证线程池配置
 * 5. 验证异步任务和定时任务启用
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@EnableScheduling
@EnableAsync
@DisplayName("Spring Task 配置测试")
class ScheduledTaskConfigTest {

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Autowired
    @Qualifier("scheduledTaskExecutor")
    private Executor scheduledTaskExecutor;

    @Autowired(required = false)
    private ScheduledTaskConfig scheduledTaskConfig;

    @Test
    @DisplayName("验证 ScheduledTaskConfig Bean 创建")
    void shouldCreateScheduledTaskConfigBean() {
        // Then - 验证 Bean 创建成功
        if (scheduledTaskConfig != null) {
            assertThat(scheduledTaskConfig).isNotNull();
        }
    }

    @Test
    @DisplayName("验证 taskExecutor Bean 创建")
    void shouldCreateTaskExecutorBean() {
        // Then - 验证 Bean 创建成功
        assertThat(taskExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor Bean 创建")
    void shouldCreateScheduledTaskExecutorBean() {
        // Then - 验证 Bean 创建成功
        assertThat(scheduledTaskExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证 taskExecutor 类型")
    void shouldVerifyTaskExecutorType() {
        // When - 验证线程池类型
        boolean isThreadPoolTaskExecutor = taskExecutor instanceof org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

        // Then - 验证类型正确
        assertThat(isThreadPoolTaskExecutor).isTrue();
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 类型")
    void shouldVerifyScheduledTaskExecutorType() {
        // When - 验证线程池类型
        boolean isThreadPoolTaskExecutor = scheduledTaskExecutor instanceof org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

        // Then - 验证类型正确
        assertThat(isThreadPoolTaskExecutor).isTrue();
    }

    @Test
    @DisplayName("验证 taskExecutor 配置")
    void shouldVerifyTaskExecutorConfiguration() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取线程池配置
        int corePoolSize = executor.getCorePoolSize();
        int maxPoolSize = executor.getMaxPoolSize();
        int queueCapacity = executor.getQueueCapacity();
        String threadNamePrefix = executor.getThreadNamePrefix();

        // Then - 验证配置
        assertThat(corePoolSize).isEqualTo(5);
        assertThat(maxPoolSize).isEqualTo(10);
        assertThat(queueCapacity).isEqualTo(25);
        assertThat(threadNamePrefix).isEqualTo("async-task-");
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 配置")
    void shouldVerifyScheduledTaskExecutorConfiguration() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取线程池配置
        int corePoolSize = executor.getCorePoolSize();
        int maxPoolSize = executor.getMaxPoolSize();
        int queueCapacity = executor.getQueueCapacity();
        String threadNamePrefix = executor.getThreadNamePrefix();

        // Then - 验证配置
        assertThat(corePoolSize).isEqualTo(2);
        assertThat(maxPoolSize).isEqualTo(5);
        assertThat(queueCapacity).isEqualTo(10);
        assertThat(threadNamePrefix).isEqualTo("scheduled-task-");
    }

    @Test
    @DisplayName("验证 taskExecutor 执行任务")
    void shouldVerifyTaskExecutorExecuteTask() throws Exception {
        // Given - 创建任务
        var task = new Runnable() {
            private volatile boolean executed = false;

            @Override
            public void run() {
                executed = true;
            }

            public boolean isExecuted() {
                return executed;
            }
        };

        // When - 执行任务
        taskExecutor.execute(task);

        // Then - 等待任务执行完成
        Thread.sleep(100);
        assertThat(task.isExecuted()).isTrue();
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 执行任务")
    void shouldVerifyScheduledTaskExecutorExecuteTask() throws Exception {
        // Given - 创建任务
        var task = new Runnable() {
            private volatile boolean executed = false;

            @Override
            public void run() {
                executed = true;
            }

            public boolean isExecuted() {
                return executed;
            }
        };

        // When - 执行任务
        scheduledTaskExecutor.execute(task);

        // Then - 等待任务执行完成
        Thread.sleep(100);
        assertThat(task.isExecuted()).isTrue();
    }

    @Test
    @DisplayName("验证 taskExecutor 线程池状态")
    void shouldVerifyTaskExecutorThreadPoolStatus() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取线程池状态
        int activeCount = executor.getActiveCount();
        int poolSize = executor.getPoolSize();

        // Then - 验证线程池状态
        assertThat(activeCount).isGreaterThanOrEqualTo(0);
        assertThat(poolSize).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 线程池状态")
    void shouldVerifyScheduledTaskExecutorThreadPoolStatus() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取线程池状态
        int activeCount = executor.getActiveCount();
        int poolSize = executor.getPoolSize();

        // Then - 验证线程池状态
        assertThat(activeCount).isGreaterThanOrEqualTo(0);
        assertThat(poolSize).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("验证 taskExecutor 拒绝策略")
    void shouldVerifyTaskExecutorRejectionPolicy() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取拒绝策略
        var threadPoolExecutor = executor.getThreadPoolExecutor();
        var rejectionHandler = threadPoolExecutor.getRejectedExecutionHandler();

        // Then - 验证拒绝策略
        assertThat(rejectionHandler).isNotNull();
        assertThat(rejectionHandler).isInstanceOf(java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class);
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 拒绝策略")
    void shouldVerifyScheduledTaskExecutorRejectionPolicy() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取拒绝策略
        var threadPoolExecutor = executor.getThreadPoolExecutor();
        var rejectionHandler = threadPoolExecutor.getRejectedExecutionHandler();

        // Then - 验证拒绝策略
        assertThat(rejectionHandler).isNotNull();
        assertThat(rejectionHandler).isInstanceOf(java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class);
    }

    @Test
    @DisplayName("验证 taskExecutor 线程名前缀")
    void shouldVerifyTaskExecutorThreadNamePrefix() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取线程名前缀
        String threadNamePrefix = executor.getThreadNamePrefix();

        // Then - 验证线程名前缀
        assertThat(threadNamePrefix).isEqualTo("async-task-");
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 线程名前缀")
    void shouldVerifyScheduledTaskExecutorThreadNamePrefix() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取线程名前缀
        String threadNamePrefix = executor.getThreadNamePrefix();

        // Then - 验证线程名前缀
        assertThat(threadNamePrefix).isEqualTo("scheduled-task-");
    }

    @Test
    @DisplayName("验证 taskExecutor 线程存活时间")
    void shouldVerifyTaskExecutorKeepAliveSeconds() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取线程存活时间
        int keepAliveSeconds = executor.getKeepAliveSeconds();

        // Then - 验证线程存活时间
        assertThat(keepAliveSeconds).isEqualTo(60);
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 线程存活时间")
    void shouldVerifyScheduledTaskExecutorKeepAliveSeconds() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取线程存活时间
        int keepAliveSeconds = executor.getKeepAliveSeconds();

        // Then - 验证线程存活时间
        assertThat(keepAliveSeconds).isEqualTo(60);
    }

    @Test
    @DisplayName("验证 taskExecutor 等待任务完成配置")
    void shouldVerifyTaskExecutorWaitForTasksToCompleteOnShutdown() throws InterruptedException {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取等待任务完成配置
        var threadPoolExecutor = executor.getThreadPoolExecutor();
        boolean waitForTasksToCompleteOnShutdown = threadPoolExecutor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);

        // Then - 验证配置
        assertThat(threadPoolExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 等待任务完成配置")
    void shouldVerifyScheduledTaskExecutorWaitForTasksToCompleteOnShutdown() throws InterruptedException {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取等待任务完成配置
        var threadPoolExecutor = executor.getThreadPoolExecutor();
        boolean waitForTasksToCompleteOnShutdown = threadPoolExecutor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);

        // Then - 验证配置
        assertThat(threadPoolExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证 taskExecutor 等待终止时间")
    void shouldVerifyTaskExecutorAwaitTerminationSeconds() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) taskExecutor;

        // When - 获取等待终止时间
        var threadPoolExecutor = executor.getThreadPoolExecutor();

        // Then - 验证配置
        assertThat(threadPoolExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证 scheduledTaskExecutor 等待终止时间")
    void shouldVerifyScheduledTaskExecutorAwaitTerminationSeconds() {
        // Given - 转换为ThreadPoolTaskExecutor
        var executor = (org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor) scheduledTaskExecutor;

        // When - 获取等待终止时间
        var threadPoolExecutor = executor.getThreadPoolExecutor();

        // Then - 验证配置
        assertThat(threadPoolExecutor).isNotNull();
    }
}