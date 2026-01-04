package com.kev1n.spring4demo.common.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 自动装配配置类
 * 
 * <p>配置 XXL-Job 执行器 Bean，使其能够与 XXL-Job 调度中心进行通信。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>创建 XxlJobSpringExecutor Bean</li>
 *   <li>配置调度中心地址和访问令牌</li>
 *   <li>配置执行器名称、IP、端口等</li>
 *   <li>配置日志路径和日志保留天数</li>
 * </ul>
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class XXLJobConfig {

    private final XXLJobProperties xxlJobProperties;

    /**
     * 创建 XxlJobSpringExecutor Bean
     * 
     * <p>该 Bean 会被 Spring 容器管理，负责与 XXL-Job 调度中心进行通信。</p>
     * 
     * @return XxlJobSpringExecutor 实例
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        
        // 设置调度中心地址
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        
        // 设置执行器应用名称
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getExecutor().getAppname());
        
        // 设置执行器注册地址
        xxlJobSpringExecutor.setAddress(xxlJobProperties.getExecutor().getAddress());
        
        // 设置执行器IP
        xxlJobSpringExecutor.setIp(xxlJobProperties.getExecutor().getIp());
        
        // 设置执行器端口
        xxlJobSpringExecutor.setPort(xxlJobProperties.getExecutor().getPort());
        
        // 设置访问令牌
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAdmin().getAccessToken());
        
        // 设置日志路径
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getExecutor().getLogpath());
        
        // 设置日志保留天数
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getExecutor().getLogretentiondays());
        if (log.isInfoEnabled()) {
            log.info(">>>>>>>>>>> xxl-job config success. adminAddresses={}, appname={}, port={}",
                    xxlJobProperties.getAdmin().getAddresses(),
                    xxlJobProperties.getExecutor().getAppname(),
                    xxlJobProperties.getExecutor().getPort());
        }

        return xxlJobSpringExecutor;
    }
}