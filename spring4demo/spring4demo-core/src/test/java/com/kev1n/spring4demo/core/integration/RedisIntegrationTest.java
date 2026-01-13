package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis 集成测试
 * 测试 Redis 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("Redis 集成测试")
class RedisIntegrationTest {

    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        log.info("初始化 Redis 连接...");
        log.info("Redis 主机: {}", TestContainersConfig.getRedisHost());
        log.info("Redis 端口: {}", TestContainersConfig.getRedisPort());
        log.info("Redis 连接字符串: {}", TestContainersConfig.getRedisConnectionString());

        // 创建 Redis 连接工厂
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                TestContainersConfig.getRedisHost(),
                TestContainersConfig.getRedisPort()
        );

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();

        // 创建 RedisTemplate
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        log.info("Redis 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            redisTemplate.getConnectionFactory().getConnection().close();
            log.info("Redis 连接已关闭");
        }
    }

    @Test
    @DisplayName("应该成功连接到 Redis 容器")
    void shouldConnectToRedisContainer_whenContainerIsRunning() {
        // Given & When
        Boolean result = redisTemplate.getConnectionFactory().getConnection().ping();

        // Then
        assertThat(result).isEqualTo("PONG");
        log.info("Redis 连接测试通过: {}", result);
    }

    @Test
    @DisplayName("应该成功设置和获取键值对")
    void shouldSetAndGetValue_whenKeyAndValueAreProvided() {
        // Given
        String key = "test:key";
        String value = "test-value";

        // When
        redisTemplate.opsForValue().set(key, value);
        String retrievedValue = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(retrievedValue).isEqualTo(value);
        log.info("设置和获取键值对测试通过: {} = {}", key, retrievedValue);

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("应该成功设置带过期时间的键值对")
    void shouldSetValueWithExpiration_whenExpirationIsProvided() {
        // Given
        String key = "test:expire";
        String value = "test-expire-value";
        Duration expiration = Duration.ofSeconds(2);

        // When
        redisTemplate.opsForValue().set(key, value, expiration);
        String retrievedValue = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(retrievedValue).isEqualTo(value);
        log.info("设置带过期时间的键值对测试通过: {} = {} (过期时间: {}秒)", key, retrievedValue, expiration.getSeconds());

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("应该成功删除键值对")
    void shouldDeleteKey_whenKeyExists() {
        // Given
        String key = "test:delete";
        String value = "test-delete-value";
        redisTemplate.opsForValue().set(key, value);

        // When
        Boolean deleted = redisTemplate.delete(key);
        String retrievedValue = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(deleted).isTrue();
        assertThat(retrievedValue).isNull();
        log.info("删除键值对测试通过: {} 已删除", key);
    }

    @Test
    @DisplayName("应该成功检查键是否存在")
    void shouldCheckKeyExists_whenKeyIsProvided() {
        // Given
        String key = "test:exists";
        String value = "test-exists-value";
        redisTemplate.opsForValue().set(key, value);

        // When
        Boolean exists = redisTemplate.hasKey(key);

        // Then
        assertThat(exists).isTrue();
        log.info("检查键存在测试通过: {} 存在 = {}", key, exists);

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("应该成功批量设置和获取键值对")
    void shouldSetAndGetMultipleValues_whenMultipleKeysAreProvided() {
        // Given
        String key1 = "test:multi:1";
        String value1 = "value1";
        String key2 = "test:multi:2";
        String value2 = "value2";

        // When
        redisTemplate.opsForValue().multiSet(java.util.Map.of(
                key1, value1,
                key2, value2
        ));

        String retrievedValue1 = redisTemplate.opsForValue().get(key1);
        String retrievedValue2 = redisTemplate.opsForValue().get(key2);

        // Then
        assertThat(retrievedValue1).isEqualTo(value1);
        assertThat(retrievedValue2).isEqualTo(value2);
        log.info("批量设置和获取键值对测试通过: {} = {}, {} = {}", key1, retrievedValue1, key2, retrievedValue2);

        // Cleanup
        redisTemplate.delete(java.util.List.of(key1, key2));
    }

    @Test
    @DisplayName("应该成功追加值到现有键")
    void shouldAppendValue_whenKeyExists() {
        // Given
        String key = "test:append";
        String initialValue = "Hello";
        String appendValue = " World";
        redisTemplate.opsForValue().set(key, initialValue);

        // When
        Long newLength = redisTemplate.opsForValue().append(key, appendValue);
        String finalValue = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(finalValue).isEqualTo(initialValue + appendValue);
        assertThat(newLength).isEqualTo((long) finalValue.length());
        log.info("追加值测试通过: {} = {}", key, finalValue);

        // Cleanup
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("应该成功获取键的值长度")
    void shouldGetValueLength_whenKeyExists() {
        // Given
        String key = "test:length";
        String value = "test-value-length";
        redisTemplate.opsForValue().set(key, value);

        // When
        Long length = redisTemplate.opsForValue().size(key);

        // Then
        assertThat(length).isEqualTo((long) value.length());
        log.info("获取键的值长度测试通过: {} 长度 = {}", key, length);

        // Cleanup
        redisTemplate.delete(key);
    }
}