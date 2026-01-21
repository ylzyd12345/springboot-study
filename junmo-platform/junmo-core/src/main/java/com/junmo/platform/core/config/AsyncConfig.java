package com.junmo.platform.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 配置异步执行器
     *
     * @return 线程池执行器
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        log.info("初始化异步线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：线程池创建时初始化的线程数
        executor.setCorePoolSize(10);

        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(20);

        // 队列容量：用来缓冲执行任务的队列
        executor.setQueueCapacity(100);

        // 线程名称前缀
        executor.setThreadNamePrefix("async-");

        // 线程空闲时间：当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数等于corePoolSize
        executor.setKeepAliveSeconds(60);

        // 拒绝策略：当队列满了，并且线程数量达到最大线程数时，采取的策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间：设置为60秒
        executor.setAwaitTerminationSeconds(60);

        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);

        executor.initialize();
        log.info("异步线程池初始化完成: corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        return executor;
    }

    /**
     * 配置异步异常处理器
     *
     * @return 异步异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("异步任务执行异常 - 方法: {}.{}, 参数: {}, 异常: {}",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    params,
                    throwable.getMessage(),
                    throwable);
        };
    }
}