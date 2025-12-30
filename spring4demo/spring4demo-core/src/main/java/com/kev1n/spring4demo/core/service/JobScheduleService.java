package com.kev1n.spring4demo.core.service;

/**
 * 任务调度服务接口
 * 
 * <p>提供任务调度的相关服务，包括任务触发、任务状态查询等。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public interface JobScheduleService {

    /**
     * 手动触发任务
     * 
     * @param jobId 任务ID
     * @param executorParam 执行器参数
     * @return 触发结果
     */
    boolean triggerJob(Long jobId, String executorParam);

    /**
     * 停止任务
     * 
     * @param jobId 任务ID
     * @return 停止结果
     */
    boolean stopJob(Long jobId);

    /**
     * 启动任务
     * 
     * @param jobId 任务ID
     * @return 启动结果
     */
    boolean startJob(Long jobId);

    /**
     * 获取任务执行日志
     * 
     * @param logId 日志ID
     * @param logDateTim 日志日期
     * @return 日志内容
     */
    String getJobLog(Long logId, String logDateTim);
}