package com.kev1n.spring4demo.core.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * XXL-Job 示例任务处理器
 * 
 * <p>演示 XXL-Job 的基本使用方法，包括简单任务、分片广播任务等。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@Slf4j
public class DemoJobHandler {

    /**
     * 简单任务示例
     * 
     * <p>该任务演示了基本的任务执行方式，每分钟执行一次。</p>
     * 
     * <p>使用方法：</p>
     * <ol>
     *   <li>在 XXL-Job 调度中心创建任务</li>
     *   <li>JobHandler 填写：demoJobHandler</li>
     *   <li>运行模式选择：BEAN</li>
     *   <li>Cron 表达式：0 0/1 * * * ? （每分钟执行一次）</li>
     * </ol>
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() {
        XxlJobHelper.log("XXL-JOB, Hello World.");
        log.info("XXL-JOB demoJobHandler 执行成功，执行时间：{}", System.currentTimeMillis());
    }

    /**
     * 分片广播任务示例
     * 
     * <p>该任务演示了分片广播的使用方式，适用于大数据量业务处理。</p>
     * 
     * <p>分片广播特性：</p>
     * <ul>
     *   <li>执行器集群部署时，任务路由策略选择"分片广播"</li>
     *   <li>一次任务调度将会广播触发集群中所有执行器执行一次任务</li>
     *   <li>可根据分片参数开发分片任务，协同进行业务处理</li>
     *   <li>支持动态扩容执行器集群从而动态增加分片数量</li>
     * </ul>
     * 
     * <p>使用方法：</p>
     * <ol>
     *   <li>在 XXL-Job 调度中心创建任务</li>
     *   <li>JobHandler 填写：shardingJobHandler</li>
     *   <li>路由策略选择：分片广播</li>
     * </ol>
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();  // 分片序号（从0开始）
        int shardTotal = XxlJobHelper.getShardTotal();  // 分片总数

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 分片总数 = {}", shardIndex, shardTotal);
        log.info("XXL-JOB shardingJobHandler 执行成功，分片序号：{}，分片总数：{}", shardIndex, shardTotal);

        // 业务逻辑处理
        // 根据分片参数处理不同的数据
        // 例如：shardIndex = 0 处理 ID % 2 = 0 的数据
        //       shardIndex = 1 处理 ID % 2 = 1 的数据
    }

    /**
     * 任务参数传递示例
     * 
     * <p>该任务演示了如何获取任务参数。</p>
     * 
     * <p>使用方法：</p>
     * <ol>
     *   <li>在 XXL-Job 调度中心创建任务</li>
     *   <li>JobHandler 填写：paramJobHandler</li>
     *   <li>任务参数填写：key1=value1;key2=value2</li>
     * </ol>
     */
    @XxlJob("paramJobHandler")
    public void paramJobHandler() {
        // 获取任务参数
        String param = XxlJobHelper.getJobParam();
        XxlJobHelper.log("任务参数：{}", param);
        log.info("XXL-JOB paramJobHandler 执行成功，任务参数：{}", param);

        // 解析任务参数
        if (param != null && !param.isEmpty()) {
            String[] params = param.split(";");
            for (String p : params) {
                String[] kv = p.split("=");
                if (kv.length == 2) {
                    String key = kv[0];
                    String value = kv[1];
                    XxlJobHelper.log("参数：{} = {}", key, value);
                }
            }
        }
    }

    /**
     * 任务失败重试示例
     * 
     * <p>该任务演示了任务失败时的重试机制。重试机制通过 XXL-Job Admin 配置实现。</p>
     * 
     * <p>使用方法：</p>
     * <ol>
     *   <li>在 XXL-Job 调度中心创建任务</li>
     *   <li>JobHandler 填写：retryJobHandler</li>
     *   <li>配置"失败重试次数"：3</li>
     *   <li>任务抛出异常时，XXL-Job 会自动重试</li>
     * </ol>
     * 
     * <p>注意：重试次数由 XXL-Job Admin 控制，任务处理器中无法直接获取当前重试次数。</p>
     */
    @XxlJob("retryJobHandler")
    public void retryJobHandler() {
        XxlJobHelper.log("XXL-JOB, 重试任务示例.");

        // 获取任务参数，可以用来控制重试行为
        String param = XxlJobHelper.getJobParam();
        XxlJobHelper.log("任务参数：{}", param);

        // 模拟任务失败
        // 可以通过任务参数或其他业务逻辑来控制何时成功
        boolean shouldFail = true;

        if (shouldFail) {
            XxlJobHelper.log("任务执行失败，准备重试...");
            log.error("XXL-JOB retryJobHandler 执行失败");
            // 抛出异常，XXL-Job 会根据配置自动重试
            throw new RuntimeException("任务执行失败");
        } else {
            XxlJobHelper.log("任务执行成功！");
            log.info("XXL-JOB retryJobHandler 执行成功");
        }
    }
}