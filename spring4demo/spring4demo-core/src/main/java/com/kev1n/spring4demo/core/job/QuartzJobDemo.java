package com.kev1n.spring4demo.core.job;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Quartz 示例任务类
 * 
 * 功能说明：
 * 1. 演示Quartz Job的基本用法
 * 2. 演示如何创建和管理Quartz任务
 * 3. 演示动态任务调度
 * 
 * 使用场景：
 * - 复杂定时任务
 * - 需要任务持久化的场景
 * - 需要动态管理任务的场景
 * - 多任务依赖的场景
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobDemo {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Scheduler scheduler;

    /**
     * 示例Job：每日数据清理
     * 
     * 说明：每天凌晨3点执行数据清理任务
     * 使用方式：通过QuartzConfig配置或动态创建
     */
    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class DailyCleanupJob implements Job {
        
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            log.info("[Quartz Job] 每日清理任务开始执行 - 时间: {}", 
                    LocalDateTime.now().format(FORMATTER));
            
            // 获取Job参数
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String cleanupType = dataMap.getString("cleanupType");
            int retentionDays = dataMap.getInt("retentionDays");
            
            log.info("[Quartz Job] 清理类型: {}, 保留天数: {}", cleanupType, retentionDays);
            
            try {
                // 执行清理逻辑
                performCleanup(cleanupType, retentionDays);
                
                log.info("[Quartz Job] 每日清理任务执行完成 - 时间: {}", 
                        LocalDateTime.now().format(FORMATTER));
            } catch (Exception e) {
                log.error("[Quartz Job] 每日清理任务执行失败", e);
                throw new JobExecutionException(e);
            }
        }
        
        private void performCleanup(String cleanupType, int retentionDays) {
            // 实现具体的清理逻辑
            log.info("[Quartz Job] 执行清理逻辑 - 类型: {}, 保留天数: {}", cleanupType, retentionDays);
        }
    }

    /**
     * 示例Job：报表生成
     * 
     * 说明：每天上午8点生成报表
     */
    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class ReportGenerationJob implements Job {
        
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            log.info("[Quartz Job] 报表生成任务开始执行 - 时间: {}", 
                    LocalDateTime.now().format(FORMATTER));
            
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String reportType = dataMap.getString("reportType");
            String recipient = dataMap.getString("recipient");
            
            log.info("[Quartz Job] 报表类型: {}, 接收人: {}", reportType, recipient);
            
            try {
                // 生成报表
                generateReport(reportType, recipient);
                
                log.info("[Quartz Job] 报表生成任务执行完成 - 时间: {}", 
                        LocalDateTime.now().format(FORMATTER));
            } catch (Exception e) {
                log.error("[Quartz Job] 报表生成任务执行失败", e);
                throw new JobExecutionException(e);
            }
        }
        
        private void generateReport(String reportType, String recipient) {
            // 实现报表生成逻辑
            log.info("[Quartz Job] 生成报表 - 类型: {}, 接收人: {}", reportType, recipient);
        }
    }

    /**
     * 动态创建定时任务
     * 
     * 说明：通过代码动态创建和管理Quartz任务
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @param jobClass Job类
     * @param cronExpression Cron表达式
     * @param jobDataMap 任务参数
     * @throws Exception
     */
    public void createJob(String jobName, String jobGroup, Class<? extends Job> jobClass, 
                         String cronExpression, JobDataMap jobDataMap) throws Exception {
        
        log.info("[Quartz Job] 创建任务 - 名称: {}, 组: {}", jobName, jobGroup);
        
        // 创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
        
        // 创建Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "Trigger", jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        
        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);
        
        log.info("[Quartz Job] 任务创建成功 - 名称: {}, 组: {}, Cron: {}", 
                jobName, jobGroup, cronExpression);
    }

    /**
     * 暂停任务
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @throws SchedulerException
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        log.info("[Quartz Job] 暂停任务 - 名称: {}, 组: {}", jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复任务
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @throws SchedulerException
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        log.info("[Quartz Job] 恢复任务 - 名称: {}, 组: {}", jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除任务
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @throws SchedulerException
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        log.info("[Quartz Job] 删除任务 - 名称: {}, 组: {}", jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.deleteJob(jobKey);
    }

    /**
     * 更新任务的Cron表达式
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @param newCronExpression 新的Cron表达式
     * @throws SchedulerException
     */
    public void updateJobCron(String jobName, String jobGroup, String newCronExpression) 
            throws SchedulerException {
        
        log.info("[Quartz Job] 更新任务Cron表达式 - 名称: {}, 组: {}, 新Cron: {}", 
                jobName, jobGroup, newCronExpression);
        
        TriggerKey triggerKey = new TriggerKey(jobName + "Trigger", jobGroup);
        
        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                .build();
        
        scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    /**
     * 获取任务状态
     * 
     * @param jobName 任务名称
     * @param jobGroup 任务组名
     * @return 任务状态
     * @throws SchedulerException
     */
    public Trigger.TriggerState getJobStatus(String jobName, String jobGroup)
            throws SchedulerException {
        
        TriggerKey triggerKey = new TriggerKey(jobName + "Trigger", jobGroup);
        return scheduler.getTriggerState(triggerKey);
    }
}