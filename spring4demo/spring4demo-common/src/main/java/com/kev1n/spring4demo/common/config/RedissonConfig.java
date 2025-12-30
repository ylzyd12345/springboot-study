package com.kev1n.spring4demo.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * <p>自动配置 Redisson 客户端，支持单机、哨兵、集群等多种模式。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

    private final RedissonProperties redissonProperties;

    public RedissonConfig(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }

    /**
     * 创建 Redisson 客户端
     *
     * <p>根据配置自动选择单机、哨兵或集群模式。</p>
     *
     * @return RedissonClient 实例
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 配置线程池
        config.setThreads(redissonProperties.getThreads().getThreadPoolSize());
        config.setNettyThreads(redissonProperties.getThreads().getNettyThreads());

        // 根据配置选择模式
        if (redissonProperties.getCluster() != null && redissonProperties.getCluster().getNodes() != null) {
            // 集群模式
            config.useClusterServers()
                    .addNodeAddress(redissonProperties.getCluster().getNodes())
                    .setScanInterval(redissonProperties.getCluster().getScanInterval())
                    .setPassword(redissonProperties.getCluster().getPassword());
            log.info("Redisson 集群模式初始化完成，节点数：{}", redissonProperties.getCluster().getNodes().length);
        } else if (redissonProperties.getSentinel() != null && redissonProperties.getSentinel().getAddresses() != null) {
            // 哨兵模式
            config.useSentinelServers()
                    .setMasterName(redissonProperties.getSentinel().getMasterName())
                    .addSentinelAddress(redissonProperties.getSentinel().getAddresses())
                    .setPassword(redissonProperties.getSentinel().getPassword())
                    .setDatabase(redissonProperties.getSentinel().getDatabase());
            log.info("Redisson 哨兵模式初始化完成，主节点：{}", redissonProperties.getSentinel().getMasterName());
        } else {
            // 单机模式
            SingleServerConfig singleServerConfig = config.useSingleServer()
                    .setAddress(redissonProperties.getAddress())
                    .setDatabase(redissonProperties.getDatabase())
                    .setTimeout(redissonProperties.getTimeout())
                    .setConnectionPoolSize(redissonProperties.getConnectionPool().getConnectionPoolSize())
                    .setConnectionMinimumIdleSize(redissonProperties.getConnectionPool().getConnectionMinimumIdleSize())
                    .setIdleConnectionTimeout(redissonProperties.getConnectionPool().getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(redissonProperties.getConnectionPool().getSubscriptionConnectionPoolSize())
                    .setSubscriptionConnectionMinimumIdleSize(redissonProperties.getConnectionPool().getSubscriptionConnectionMinimumIdleSize());

            // 设置密码
            if (redissonProperties.getPassword() != null && !redissonProperties.getPassword().isEmpty()) {
                singleServerConfig.setPassword(redissonProperties.getPassword());
            }

            log.info("Redisson 单机模式初始化完成，地址：{}", redissonProperties.getAddress());
        }

        return Redisson.create(config);
    }
}