package com.kev1n.spring4demo.common.config;

import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz 任务调度配置类
 * 
 * 功能说明：
 * 1. 配置Quartz调度器
 * 2. 支持任务持久化（可选）
 * 3. 支持集群模式（可选）
 * 
 * 适用场景：
 * - 复杂定时任务（多任务依赖、动态调度）
 * - 需要任务持久化的场景
 * - 需要任务监控和管理的场景
 * 
 * 使用说明：
 * 1. 在application.yml中配置quartz.enabled=true启用Quartz
 * 2. 如需持久化，配置quartz.jobStore相关参数
 * 3. 创建Job类实现QuartzJobBean或Job接口
 * 4. 使用@XxlJob注解标记任务方法（可选）
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-24
 */
@Configuration
@ConditionalOnProperty(prefix = "quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
public class QuartzConfig {

    /**
     * 配置Quartz调度器工厂Bean
     * 
     * @param dataSource 数据源（可选，用于任务持久化）
     * @param quartzProperties Quartz配置属性
     * @param quartzPropertiesBean Spring Boot自动配置的Quartz属性
     * @return SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            QuartzProperties quartzProperties,
            QuartzProperties quartzPropertiesBean) {
        
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        
        // 设置调度器名称
        factory.setSchedulerName(quartzProperties.getInstanceName());
        
        // 设置应用上下文
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        
        // 配置Quartz属性
        Properties props = new Properties();
        
        // 调度器实例ID
        props.put("org.quartz.scheduler.instanceId", quartzProperties.getInstanceId());
        
        // 跳过更新检查
        props.put("org.quartz.scheduler.skipUpdateCheck", "true");
        
        // 任务存储配置
        if (dataSource != null) {
            // 使用JDBC持久化
            props.put("org.quartz.jobStore.class", quartzProperties.getJobStore().getJobStoreClass());
            props.put("org.quartz.jobStore.driverDelegateClass", 
                    quartzProperties.getJobStore().getDriverDelegateClass());
            props.put("org.quartz.jobStore.tablePrefix", 
                    quartzProperties.getJobStore().getTablePrefix());
            props.put("org.quartz.jobStore.isClustered", 
                    quartzProperties.getJobStore().isClustered());
            props.put("org.quartz.jobStore.clusterCheckinInterval", 
                    quartzProperties.getJobStore().getClusterCheckinInterval());
            props.put("org.quartz.jobStore.useProperties", 
                    quartzProperties.getJobStore().isUseProperties());
            
            // 设置数据源
            factory.setDataSource(dataSource);
        } else {
            // 使用内存存储
            props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        }
        
        // 线程池配置
        props.put("org.quartz.threadPool.class", 
                quartzProperties.getThreadPool().getThreadPoolClass());
        props.put("org.quartz.threadPool.threadCount", 
                quartzProperties.getThreadPool().getThreadCount());
        props.put("org.quartz.threadPool.threadPriority", 
                quartzProperties.getThreadPool().getThreadPriority());
        props.put("org.quartz.threadPool.inheritContextClassLoaderOfInitializingThread", 
                quartzProperties.getThreadPool().isInheritContextClassLoaderOfInitializingThread());
        
        factory.setQuartzProperties(props);
        
        // 设置自动启动
        factory.setAutoStartup(true);
        
        // 设置延迟启动（秒）
        factory.setStartupDelay(10);
        
        // 设置是否覆盖已存在的任务
        factory.setOverwriteExistingJobs(true);
        
        return factory;
    }

    /**
     * 配置Quartz调度器Bean
     * 
     * @param schedulerFactoryBean 调度器工厂Bean
     * @return Scheduler
     */
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws Exception {
        return schedulerFactoryBean.getScheduler();
    }
}