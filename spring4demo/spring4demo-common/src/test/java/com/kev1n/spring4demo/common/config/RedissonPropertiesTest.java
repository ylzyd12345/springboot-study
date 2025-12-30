package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redisson 配置属性测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("Redisson 配置属性测试")
class RedissonPropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        RedissonProperties properties = new RedissonProperties();

        // Then - 验证默认值
        assertThat(properties.getAddress()).isEqualTo("redis://localhost:6379");
        assertThat(properties.getDatabase()).isZero();
        assertThat(properties.getPassword()).isNull();
        assertThat(properties.getTimeout()).isEqualTo(3000);
        assertThat(properties.getSentinel()).isNull();
        assertThat(properties.getCluster()).isNull();
    }

    @Test
    @DisplayName("配置绑定测试 - 单机模式")
    void testBindSingleServerMode() {
        // Given - 单机模式配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "redis://192.168.1.100:6379");
        config.put("spring.data.redis.redisson.database", 1);
        config.put("spring.data.redis.redisson.password", "redis-password");
        config.put("spring.data.redis.redisson.timeout", 5000);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证绑定结果
        assertThat(properties.getAddress()).isEqualTo("redis://192.168.1.100:6379");
        assertThat(properties.getDatabase()).isEqualTo(1);
        assertThat(properties.getPassword()).isEqualTo("redis-password");
        assertThat(properties.getTimeout()).isEqualTo(5000);
    }

    @Test
    @DisplayName("配置绑定测试 - SSL 连接")
    void testBindSslConnection() {
        // Given - SSL 连接配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "rediss://192.168.1.100:6379");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证 SSL 连接
        assertThat(properties.getAddress()).isEqualTo("rediss://192.168.1.100:6379");
    }

    @Test
    @DisplayName("配置绑定测试 - 连接池配置")
    void testBindConnectionPoolConfig() {
        // Given - 连接池配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "redis://localhost:6379");
        config.put("spring.data.redis.redisson.connection-pool.connection-pool-size", 128);
        config.put("spring.data.redis.redisson.connection-pool.connection-minimum-idle-size", 20);
        config.put("spring.data.redis.redisson.connection-pool.idle-connection-timeout", 20000);
        config.put("spring.data.redis.redisson.connection-pool.subscription-connection-pool-size", 100);
        config.put("spring.data.redis.redisson.connection-pool.subscription-connection-minimum-idle-size", 5);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证连接池配置
        assertThat(properties.getConnectionPool().getConnectionPoolSize()).isEqualTo(128);
        assertThat(properties.getConnectionPool().getConnectionMinimumIdleSize()).isEqualTo(20);
        assertThat(properties.getConnectionPool().getIdleConnectionTimeout()).isEqualTo(20000);
        assertThat(properties.getConnectionPool().getSubscriptionConnectionPoolSize()).isEqualTo(100);
        assertThat(properties.getConnectionPool().getSubscriptionConnectionMinimumIdleSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("配置绑定测试 - 哨兵模式")
    void testBindSentinelMode() {
        // Given - 哨兵模式配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.sentinel.master-name", "mymaster");
        config.put("spring.data.redis.redisson.sentinel.addresses", "192.168.1.101:26379,192.168.1.102:26379,192.168.1.103:26379");
        config.put("spring.data.redis.redisson.sentinel.password", "sentinel-password");
        config.put("spring.data.redis.redisson.sentinel.database", 2);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证哨兵配置
        assertThat(properties.getSentinel()).isNotNull();
        assertThat(properties.getSentinel().getMasterName()).isEqualTo("mymaster");
        assertThat(properties.getSentinel().getAddresses()).isNotNull();
        assertThat(properties.getSentinel().getAddresses()).hasSize(3);
        assertThat(properties.getSentinel().getPassword()).isEqualTo("sentinel-password");
        assertThat(properties.getSentinel().getDatabase()).isEqualTo(2);
    }

    @Test
    @DisplayName("配置绑定测试 - 集群模式")
    void testBindClusterMode() {
        // Given - 集群模式配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.cluster.nodes", "192.168.1.101:6379,192.168.1.102:6379,192.168.1.103:6379");
        config.put("spring.data.redis.redisson.cluster.scan-interval", 3000);
        config.put("spring.data.redis.redisson.cluster.password", "cluster-password");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证集群配置
        assertThat(properties.getCluster()).isNotNull();
        assertThat(properties.getCluster().getNodes()).isNotNull();
        assertThat(properties.getCluster().getNodes()).hasSize(3);
        assertThat(properties.getCluster().getScanInterval()).isEqualTo(3000);
        assertThat(properties.getCluster().getPassword()).isEqualTo("cluster-password");
    }

    @Test
    @DisplayName("配置绑定测试 - 线程池配置")
    void testBindThreadsConfig() {
        // Given - 线程池配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.threads.thread-pool-size", 32);
        config.put("spring.data.redis.redisson.threads.netty-threads", 64);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证线程池配置
        assertThat(properties.getThreads().getThreadPoolSize()).isEqualTo(32);
        assertThat(properties.getThreads().getNettyThreads()).isEqualTo(64);
    }

    @Test
    @DisplayName("配置绑定测试 - 单服务器配置")
    void testBindSingleServerConfig() {
        // Given - 单服务器配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.single-server-config.connection-idle-timeout", 20000);
        config.put("spring.data.redis.redisson.single-server-config.subscription-connection-timeout", 10000);
        config.put("spring.data.redis.redisson.single-server-config.monitor-lock-expiration", 60000);
        config.put("spring.data.redis.redisson.single-server-config.connection-pool-size", 128);
        config.put("spring.data.redis.redisson.single-server-config.keep-alive", false);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证单服务器配置
        assertThat(properties.getSingleServerConfig().getConnectionIdleTimeout()).isEqualTo(20000);
        assertThat(properties.getSingleServerConfig().getSubscriptionConnectionTimeout()).isEqualTo(10000);
        assertThat(properties.getSingleServerConfig().getMonitorLockExpiration()).isEqualTo(60000);
        assertThat(properties.getSingleServerConfig().getConnectionPoolSize()).isEqualTo(128);
        assertThat(properties.getSingleServerConfig().isKeepAlive()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "redis://localhost:6379");
        config.put("spring.data.redis.redisson.database", "5"); // 字符串转 int
        config.put("spring.data.redis.redisson.timeout", "10000"); // 字符串转 int
        config.put("spring.data.redis.redisson.connection-pool.connection-pool-size", "64"); // 字符串转 int

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证类型转换正确
        assertThat(properties.getDatabase()).isInstanceOf(Integer.class);
        assertThat(properties.getTimeout()).isInstanceOf(Integer.class);
        assertThat(properties.getConnectionPool().getConnectionPoolSize()).isInstanceOf(Integer.class);

        assertThat(properties.getDatabase()).isEqualTo(5);
        assertThat(properties.getTimeout()).isEqualTo(10000);
        assertThat(properties.getConnectionPool().getConnectionPoolSize()).isEqualTo(64);
    }

    @Test
    @DisplayName("配置绑定测试 - 边界值")
    void testBindBoundaryValues() {
        // Given - 边界值配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "redis://localhost:6379");
        config.put("spring.data.redis.redisson.database", 0); // 最小值
        config.put("spring.data.redis.redisson.timeout", 0); // 最小值
        config.put("spring.data.redis.redisson.connection-pool.connection-pool-size", Integer.MAX_VALUE); // 最大值
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证边界值正确处理
        assertThat(properties.getDatabase()).isZero();
        assertThat(properties.getTimeout()).isZero();
        assertThat(properties.getConnectionPool().getConnectionPoolSize()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.address", "redis://localhost:6379");
        config.put("spring.data.redis.redisson.password", null);
        config.put("spring.data.redis.redisson.sentinel", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证 null 值处理
        assertThat(properties.getPassword()).isNull();
        assertThat(properties.getSentinel()).isNull();
    }

    @Test
    @DisplayName("配置绑定测试 - 地址格式")
    void testBindAddressFormat() {
        // Given - 不同格式的地址
        Map<String, Object> config1 = new HashMap<>();
        config1.put("spring.data.redis.redisson.address", "redis://localhost:6379");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("spring.data.redis.redisson.address", "rediss://localhost:6379");

        Map<String, Object> config3 = new HashMap<>();
        config3.put("spring.data.redis.redisson.address", "redis://192.168.1.100:6379/0");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        RedissonProperties properties1 = binder1.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());
        RedissonProperties properties2 = binder2.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());
        RedissonProperties properties3 = binder3.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证地址格式
        assertThat(properties1.getAddress()).isEqualTo("redis://localhost:6379");
        assertThat(properties2.getAddress()).isEqualTo("rediss://localhost:6379");
        assertThat(properties3.getAddress()).isEqualTo("redis://192.168.1.100:6379/0");
    }

    @Test
    @DisplayName("配置绑定测试 - 哨兵地址数组")
    void testBindSentinelAddressesArray() {
        // Given - 哨兵地址数组配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.sentinel.master-name", "mymaster");
        config.put("spring.data.redis.redisson.sentinel.addresses", "192.168.1.101:26379,192.168.1.102:26379");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证哨兵地址数组
        assertThat(properties.getSentinel().getAddresses()).isNotNull();
        assertThat(properties.getSentinel().getAddresses()).hasSize(2);
        assertThat(properties.getSentinel().getAddresses()[0]).isEqualTo("192.168.1.101:26379");
        assertThat(properties.getSentinel().getAddresses()[1]).isEqualTo("192.168.1.102:26379");
    }

    @Test
    @DisplayName("配置绑定测试 - 集群节点数组")
    void testBindClusterNodesArray() {
        // Given - 集群节点数组配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.data.redis.redisson.cluster.nodes", "192.168.1.101:6379,192.168.1.102:6379,192.168.1.103:6379");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RedissonProperties properties = binder.bind("spring.data.redis.redisson", Bindable.of(RedissonProperties.class))
                .orElse(new RedissonProperties());

        // Then - 验证集群节点数组
        assertThat(properties.getCluster().getNodes()).isNotNull();
        assertThat(properties.getCluster().getNodes()).hasSize(3);
        assertThat(properties.getCluster().getNodes()[0]).isEqualTo("192.168.1.101:6379");
        assertThat(properties.getCluster().getNodes()[1]).isEqualTo("192.168.1.102:6379");
        assertThat(properties.getCluster().getNodes()[2]).isEqualTo("192.168.1.103:6379");
    }
}