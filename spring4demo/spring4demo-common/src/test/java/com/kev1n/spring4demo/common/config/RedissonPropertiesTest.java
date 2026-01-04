package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redisson 配置属性测试
 * 
 * 测试要点：
 * 1. 验证配置属性绑定
 * 2. 验证默认值是否正确
 * 3. 验证嵌套配置对象是否正确注入
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-30
 */
@SpringBootTest
@EnableConfigurationProperties(RedissonProperties.class)
@TestPropertySource(properties = {
    "spring.data.redis.redisson.address=redis://custom-redis:6380",
    "spring.data.redis.redisson.database=1",
    "spring.data.redis.redisson.password=custom-password",
    "spring.data.redis.redisson.timeout=5000"
})
@DisplayName("Redisson 配置属性测试")
class RedissonPropertiesTest {

    @Autowired
    private RedissonProperties properties;

    @Test
    @DisplayName("应该正确注入RedissonProperties Bean")
    void shouldInjectRedissonProperties() {
        assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该正确配置address")
    void shouldConfigureAddress() {
        assertThat(properties.getAddress()).isEqualTo("redis://custom-redis:6380");
    }

    @Test
    @DisplayName("应该正确配置database")
    void shouldConfigureDatabase() {
        assertThat(properties.getDatabase()).isEqualTo(1);
    }

    @Test
    @DisplayName("应该正确配置password")
    void shouldConfigurePassword() {
        assertThat(properties.getPassword()).isEqualTo("custom-password");
    }

    @Test
    @DisplayName("应该正确配置timeout")
    void shouldConfigureTimeout() {
        assertThat(properties.getTimeout()).isEqualTo(5000);
    }

    @Test
    @DisplayName("应该正确配置connectionPool")
    void shouldConfigureConnectionPool() {
        RedissonProperties.ConnectionPool connectionPool = properties.getConnectionPool();
        
        assertThat(connectionPool).isNotNull();
        assertThat(connectionPool.getConnectionPoolSize()).isEqualTo(64);
    }

    @Test
    @DisplayName("应该正确配置threads")
    void shouldConfigureThreads() {
        RedissonProperties.Threads threads = properties.getThreads();
        
        assertThat(threads).isNotNull();
        assertThat(threads.getThreadPoolSize()).isEqualTo(16);
        assertThat(threads.getNettyThreads()).isEqualTo(32);
    }

    @Test
    @DisplayName("应该正确配置singleServerConfig")
    void shouldConfigureSingleServerConfig() {
        RedissonProperties.SingleServerConfig singleServerConfig = properties.getSingleServerConfig();
        
        assertThat(singleServerConfig).isNotNull();
        assertThat(singleServerConfig.getConnectionIdleTimeout()).isEqualTo(10000);
    }

    @DisplayName("应该使用默认值当配置未设置")
    @EnableConfigurationProperties(RedissonProperties.class)
    class DefaultValuesTest {

        @Autowired
        private RedissonProperties properties;

        @Test
        @DisplayName("address应该使用默认值")
        void addressShouldUseDefaultValue() {
            assertThat(properties.getAddress()).isEqualTo("redis://localhost:6379");
        }

        @Test
        @DisplayName("database应该使用默认值")
        void databaseShouldUseDefaultValue() {
            assertThat(properties.getDatabase()).isEqualTo(0);
        }

        @Test
        @DisplayName("timeout应该使用默认值")
        void timeoutShouldUseDefaultValue() {
            assertThat(properties.getTimeout()).isEqualTo(3000);
        }

        @Test
        @DisplayName("connectionPool应该使用默认值")
        void connectionPoolShouldUseDefaultValue() {
            RedissonProperties.ConnectionPool connectionPool = properties.getConnectionPool();
            
            assertThat(connectionPool.getConnectionPoolSize()).isEqualTo(64);
            assertThat(connectionPool.getConnectionMinimumIdleSize()).isEqualTo(10);
            assertThat(connectionPool.getIdleConnectionTimeout()).isEqualTo(10000);
        }

        @Test
        @DisplayName("threads应该使用默认值")
        void threadsShouldUseDefaultValue() {
            RedissonProperties.Threads threads = properties.getThreads();
            
            assertThat(threads.getThreadPoolSize()).isEqualTo(16);
            assertThat(threads.getNettyThreads()).isEqualTo(32);
        }

        @Test
        @DisplayName("singleServerConfig应该使用默认值")
        void singleServerConfigShouldUseDefaultValue() {
            RedissonProperties.SingleServerConfig singleServerConfig = properties.getSingleServerConfig();
            
            assertThat(singleServerConfig.getConnectionIdleTimeout()).isEqualTo(10000);
            assertThat(singleServerConfig.isKeepAlive()).isTrue();
        }
    }
}