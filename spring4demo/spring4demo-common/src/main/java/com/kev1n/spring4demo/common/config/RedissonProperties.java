package com.kev1n.spring4demo.common.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Redisson 配置属性
 *
 * <p>用于配置 Redisson 客户端的连接参数，包括单机、哨兵、集群等模式。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "spring.data.redis.redisson")
public class RedissonProperties {

    /**
     * Redis 服务器地址（单机模式）
     * <p>格式：redis://host:port 或 rediss://host:port（SSL）</p>
     */
    @NotBlank(message = "Redisson 地址不能为空")
    private String address = "redis://localhost:6379";

    /**
     * Redis 数据库索引
     */
    @Min(value = 0, message = "数据库索引不能小于0")
    private int database = 0;

    /**
     * Redis 密码
     */
    private String password;

    /**
     * 连接超时时间（毫秒）
     */
    private int timeout = 3000;

    /**
     * 连接池配置
     */
    private ConnectionPool connectionPool = new ConnectionPool();

    /**
     * 哨兵模式配置
     */
    private Sentinel sentinel;

    /**
     * 集群模式配置
     */
    private Cluster cluster;

    /**
     * 线程池配置
     */
    private Threads threads = new Threads();

    /**
     * 单服务器配置
     */
    private SingleServerConfig singleServerConfig = new SingleServerConfig();

    /**
     * 连接池配置
     */
    @Data
    public static class ConnectionPool {
        /**
         * 连接池大小
         */
        private int connectionPoolSize = 64;

        /**
         * 最小空闲连接数
         */
        private int connectionMinimumIdleSize = 10;

        /**
         * 空闲连接超时时间（毫秒）
         */
        private int idleConnectionTimeout = 10000;

        /**
         * 订阅连接池大小
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * 订阅连接最小空闲数
         */
        private int subscriptionConnectionMinimumIdleSize = 1;
    }

    /**
     * 哨兵模式配置
     */
    @Data
    public static class Sentinel {
        /**
         * 主节点名称
         */
        private String masterName;

        /**
         * 哨兵节点地址列表
         */
        private String[] addresses;

        /**
         * Redis 密码
         */
        private String password;

        /**
         * 数据库索引
         */
        private int database = 0;
    }

    /**
     * 集群模式配置
     */
    @Data
    public static class Cluster {
        /**
         * 集群节点地址列表
         */
        private String[] nodes;

        /**
         * 扫描间隔时间（毫秒）
         */
        private int scanInterval = 2000;

        /**
         * Redis 密码
         */
        private String password;
    }

    /**
     * 线程池配置
     */
    @Data
    public static class Threads {
        /**
         * 线程池大小
         */
        private int threadPoolSize = 16;

        /**
         * Netty 线程数
         */
        private int nettyThreads = 32;
    }

    /**
     * 单服务器配置
     */
    @Data
    public static class SingleServerConfig {
        /**
         * 连接空闲超时时间（毫秒）
         */
        private int connectionIdleTimeout = 10000;

        /**
         * 订阅连接超时时间（毫秒）
         */
        private int subscriptionConnectionTimeout = 5000;

        /**
         * 监控锁的过期时间（毫秒）
         */
        private int monitorLockExpiration = 30000;

        /**
         * 订阅发布超时时间（毫秒）
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * 订阅连接最小空闲数
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * 连接池大小
         */
        private int connectionPoolSize = 64;

        /**
         * 最小空闲连接数
         */
        private int connectionMinimumIdleSize = 10;

        /**
         * 保持订阅连接
         */
        private boolean keepAlive = true;
    }
}