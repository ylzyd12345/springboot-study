package com.junmo.platform.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz定时任务配置类
 * <p>
 * 配置Quartz调度器、线程池、JobDetail和Trigger
 * </p>
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>配置Quartz调度器工厂</li>
 *   <li>配置任务线程池</li>
 *   <li>配置Job持久化到数据库</li>
 *   <li>配置用户清理任务</li>
 *   <li>配置用户统计任务</li>
 *   <li>配置缓存刷新任务</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>复杂定时任务调度</li>
 *   <li>需要任务持久化的场景</li>
 *   <li>需要动态管理任务的场景</li>
 *   <li>多任务依赖的场景</li>
 * </ul>
 *
 * @author junmo-platform
 * @version 1.0.0
 * @since 2026-01-08
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final DataSource dataSource;

    /**
     * Quartz调度器工厂Bean
     * <p>
     * 配置调度器的各项属性，包括数据源、线程池、Job工厂等
     * </p>
     *
     * @return 调度器工厂Bean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        log.info("初始化Quartz调度器工厂");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        // 设置Job工厂，支持Spring Bean注入
        factory.setJobFactory(springBeanJobFactory());

        // 设置数据源，用于Job持久化
        factory.setDataSource(dataSource);

        // 设置Quartz属性
        Properties props = new Properties();

        // 调度器实例名称
        props.put("org.quartz.scheduler.instanceName", "QuartzScheduler");

        // 调度器实例ID（AUTO表示自动生成）
        props.put("org.quartz.scheduler.instanceId", "AUTO");

        // 线程池配置
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.put("org.quartz.threadPool.threadCount", "5");
        props.put("org.quartz.threadPool.threadPriority", "5");
        props.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        // JobStore配置（使用JDBC持久化）
        props.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        props.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        props.put("org.quartz.jobStore.isClustered", "false");
        props.put("org.quartz.jobStore.misfireThreshold", "60000");
        props.put("org.quartz.jobStore.useProperties", "false");

        // 集群配置（如果需要集群模式，可以启用）
        // props.put("org.quartz.jobStore.isClustered", "true");
        // props.put("org.quartz.jobStore.clusterCheckinInterval", "20000");

        factory.setQuartzProperties(props);

        // 延迟启动（应用启动后10秒再启动调度器）
        factory.setStartupDelay(10);

        // 自动启动
        factory.setAutoStartup(true);

        // 覆盖已存在的Job
        factory.setOverwriteExistingJobs(true);

        log.info("Quartz调度器工厂配置完成");
        return factory;
    }

    /**
     * Spring Bean Job工厂
     * <p>
     * 支持在Job中注入Spring Bean
     * </p>
     *
     * @return Spring Bean Job工厂
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        log.info("Spring Bean Job工厂配置完成");
        return jobFactory;
    }

    /**
     * 用户清理任务JobDetail
     * <p>
     * 每周日凌晨3点执行
     * </p>
     *
     * @return 用户清理任务JobDetail
     */
    @Bean
    public JobDetail userCleanJobDetail() {
        log.info("配置用户清理任务JobDetail");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("retentionDays", 90);

        return JobBuilder.newJob(com.junmo.platform.core.job.UserCleanJob.class)
                .withIdentity("userCleanJob", "userGroup")
                .withDescription("用户清理任务：清理过期用户和离线用户")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /**
     * 用户清理任务Trigger
     * <p>
     * Cron表达式：0 0 3 ? * SUN（每周日凌晨3点）
     * </p>
     *
     * @return 用户清理任务Trigger
     */
    @Bean
    public Trigger userCleanJobTrigger() {
        log.info("配置用户清理任务Trigger");

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 3 ? * SUN");

        return TriggerBuilder.newTrigger()
                .forJob(userCleanJobDetail())
                .withIdentity("userCleanJobTrigger", "userGroup")
                .withDescription("用户清理任务触发器：每周日凌晨3点执行")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 用户统计任务JobDetail
     * <p>
     * 每天凌晨1点执行
     * </p>
     *
     * @return 用户统计任务JobDetail
     */
    @Bean
    public JobDetail userStatsJobDetail() {
        log.info("配置用户统计任务JobDetail");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("statsType", "daily");

        return JobBuilder.newJob(com.junmo.platform.core.job.UserStatsJob.class)
                .withIdentity("userStatsJob", "userGroup")
                .withDescription("用户统计任务：统计用户数量和活跃度")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /**
     * 用户统计任务Trigger
     * <p>
     * Cron表达式：0 0 1 * * ?（每天凌晨1点）
     * </p>
     *
     * @return 用户统计任务Trigger
     */
    @Bean
    public Trigger userStatsJobTrigger() {
        log.info("配置用户统计任务Trigger");

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 1 * * ?");

        return TriggerBuilder.newTrigger()
                .forJob(userStatsJobDetail())
                .withIdentity("userStatsJobTrigger", "userGroup")
                .withDescription("用户统计任务触发器：每天凌晨1点执行")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 缓存刷新任务JobDetail
     * <p>
     * 每小时执行一次
     * </p>
     *
     * @return 缓存刷新任务JobDetail
     */
    @Bean
    public JobDetail cacheRefreshJobDetail() {
        log.info("配置缓存刷新任务JobDetail");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("refreshType", "all");

        return JobBuilder.newJob(com.junmo.platform.core.job.CacheRefreshJob.class)
                .withIdentity("cacheRefreshJob", "cacheGroup")
                .withDescription("缓存刷新任务：刷新用户缓存和配置缓存")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /**
     * 缓存刷新任务Trigger
     * <p>
     * Cron表达式：0 0 * * * ?（每小时执行）
     * </p>
     *
     * @return 缓存刷新任务Trigger
     */
    @Bean
    public Trigger cacheRefreshJobTrigger() {
        log.info("配置缓存刷新任务Trigger");

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 * * * ?");

        return TriggerBuilder.newTrigger()
                .forJob(cacheRefreshJobDetail())
                .withIdentity("cacheRefreshJobTrigger", "cacheGroup")
                .withDescription("缓存刷新任务触发器：每小时执行")
                .withSchedule(scheduleBuilder)
                .build();
    }
}