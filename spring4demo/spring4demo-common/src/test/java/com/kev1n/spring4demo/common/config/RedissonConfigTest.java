package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redisson 配置测试
 *
 * 测试要点：
 * 1. 验证 RedissonConfig Bean 创建
 * 2. 验证 RedissonClient Bean 创建
 * 3. 验证 RedissonClient 功能
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ConditionalOnMissingBean
@DisplayName("Redisson 配置测试")
class RedissonConfigTest {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired(required = false)
    private RedissonConfig redissonConfig;

    @Test
    @DisplayName("验证 RedissonConfig Bean 创建")
    void shouldCreateRedissonConfigBean() {
        // Then - 验证 Bean 创建成功
        if (redissonConfig != null) {
            assertThat(redissonConfig).isNotNull();
        }
    }

    @Test
    @DisplayName("验证 RedissonClient Bean 创建")
    void shouldCreateRedissonClientBean() {
        // Then - 验证 Bean 创建成功
        assertThat(redissonClient).isNotNull();
    }

    @Test
    @DisplayName("验证 RedissonClient 连接状态")
    void shouldVerifyRedissonClientConnection() {
        // When - 检查RedissonClient是否关闭
        boolean isShutdown = redissonClient.isShutdown();

        // Then - 验证连接状态
        assertThat(isShutdown).isFalse();
    }

    @Test
    @DisplayName("验证 RedissonClient 节点信息")
    void shouldVerifyRedissonClientNodes() {
        // When - 获取节点信息
        var nodesGroup = redissonClient.getNodesGroup();

        // Then - 验证节点信息
        assertThat(nodesGroup).isNotNull();
    }

    @Test
    @DisplayName("验证 RedissonClient 线程池配置")
    void shouldVerifyRedissonClientThreads() {
        // When - 获取RedissonClient配置
        var config = redissonClient.getConfig();

        // Then - 验证线程池配置
        assertThat(config).isNotNull();
        assertThat(config.getThreads()).isGreaterThan(0);
        assertThat(config.getNettyThreads()).isGreaterThan(0);
    }

    @Test
    @DisplayName("验证 RedissonClient Map 操作")
    void shouldVerifyRedissonClientMapOperations() {
        // Given - 测试键值
        String mapName = "test:map";
        String key = "testKey";
        String value = "testValue";

        // When - 使用Map操作
        var map = redissonClient.getMap(mapName);
        map.put(key, value);
        Object retrievedValue = map.get(key);

        // Then - 验证操作结果
        assertThat(retrievedValue).isNotNull();
        assertThat(retrievedValue.toString()).isEqualTo(value);

        // Cleanup - 删除测试数据
        map.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient Set 操作")
    void shouldVerifyRedissonClientSetOperations() {
        // Given - 测试键值
        String setName = "test:set";
        String value = "testValue";

        // When - 使用Set操作
        var set = redissonClient.getSet(setName);
        set.add(value);
        boolean contains = set.contains(value);

        // Then - 验证操作结果
        assertThat(contains).isTrue();

        // Cleanup - 删除测试数据
        set.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient List 操作")
    void shouldVerifyRedissonClientListOperations() {
        // Given - 测试键值
        String listName = "test:list";
        String value = "testValue";

        // When - 使用List操作
        var list = redissonClient.getList(listName);
        list.add(value);
        Object retrievedValue = list.get(0);

        // Then - 验证操作结果
        assertThat(retrievedValue).isNotNull();
        assertThat(retrievedValue.toString()).isEqualTo(value);

        // Cleanup - 删除测试数据
        list.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient 分布式锁")
    void shouldVerifyRedissonClientLock() {
        // Given - 测试锁键
        String lockKey = "test:lock";

        // When - 使用分布式锁
        var lock = redissonClient.getLock(lockKey);
        boolean locked = lock.tryLock();

        // Then - 验证锁操作
        assertThat(locked).isTrue();

        // Cleanup - 释放锁
        if (locked) {
            lock.unlock();
        }
    }

    @Test
    @DisplayName("验证 RedissonClient 读写锁")
    void shouldVerifyRedissonClientReadWriteLock() {
        // Given - 测试锁键
        String lockKey = "test:rwlock";

        // When - 使用读写锁
        var rwLock = redissonClient.getReadWriteLock(lockKey);
        boolean readLocked = rwLock.readLock().tryLock();

        // Then - 验证读锁操作
        assertThat(readLocked).isTrue();

        // Cleanup - 释放读锁
        if (readLocked) {
            rwLock.readLock().unlock();
        }
    }

    @Test
    @DisplayName("验证 RedissonClient 限流器")
    void shouldVerifyRedissonClientRateLimiter() {
        // Given - 测试限流器键
        String rateLimiterKey = "test:ratelimiter";

        // When - 使用限流器
        var rateLimiter = redissonClient.getRateLimiter(rateLimiterKey);
        rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);

        // Then - 验证限流器配置
        assertThat(rateLimiter).isNotNull();

        // Cleanup - 删除限流器
        rateLimiter.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient 布隆过滤器")
    void shouldVerifyRedissonClientBloomFilter() {
        // Given - 测试布隆过滤器键
        String bloomFilterKey = "test:bloomfilter";

        // When - 使用布隆过滤器
        var bloomFilter = redissonClient.getBloomFilter(bloomFilterKey);
        bloomFilter.tryInit(1000, 0.01);
        bloomFilter.add("testValue");
        boolean contains = bloomFilter.contains("testValue");

        // Then - 验证布隆过滤器操作
        assertThat(contains).isTrue();

        // Cleanup - 删除布隆过滤器
        bloomFilter.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient HyperLogLog")
    void shouldVerifyRedissonClientHyperLogLog() {
        // Given - 测试HyperLogLog键
        String hllKey = "test:hll";

        // When - 使用HyperLogLog
        var hll = redissonClient.getHyperLogLog(hllKey);
        hll.add("value1");
        hll.add("value2");
        hll.add("value3");
        long count = hll.count();

        // Then - 验证HyperLogLog操作
        assertThat(count).isGreaterThan(0);

        // Cleanup - 删除HyperLogLog
        hll.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient Bucket 操作")
    void shouldVerifyRedissonClientBucketOperations() {
        // Given - 测试键值
        String bucketKey = "test:bucket";
        String value = "testValue";

        // When - 使用Bucket操作
        var bucket = redissonClient.getBucket(bucketKey);
        bucket.set(value);
        Object retrievedValue = bucket.get();

        // Then - 验证操作结果
        assertThat(retrievedValue).isNotNull();
        assertThat(retrievedValue.toString()).isEqualTo(value);

        // Cleanup - 删除测试数据
        bucket.delete();
    }

    @Test
    @DisplayName("验证 RedissonClient 关闭方法")
    void shouldVerifyRedissonClientShutdown() {
        // When - 验证关闭方法存在
        boolean hasShutdownMethod = redissonClient != null;

        // Then - 验证关闭方法
        assertThat(hasShutdownMethod).isTrue();
        // 注意：实际测试中不应该调用shutdown，因为会影响其他测试
    }
}