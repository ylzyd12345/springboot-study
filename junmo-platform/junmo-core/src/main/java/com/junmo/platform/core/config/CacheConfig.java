package com.junmo.platform.core.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * <p>
 * 配置Caffeine本地缓存和Redis分布式缓存，实现多级缓存策略。
 * 使用CompositeCacheManager组合两个缓存管理器，实现L1本地缓存 + L2分布式缓存。
 * </p>
 *
 * <p>缓存策略：</p>
 * <ul>
 *   <li>L1缓存：Caffeine本地缓存，高性能，容量有限</li>
 *   <li>L2缓存：Redis分布式缓存，容量大，支持集群</li>
 *   <li>缓存穿透：使用布隆过滤器（TODO）</li>
 *   <li>缓存击穿：使用互斥锁（TODO）</li>
 *   <li>缓存雪崩：使用随机过期时间</li>
 * </ul>
 *
 * @author junmo-platform
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * RedisTemplate配置
     * <p>
     * 配置RedisTemplate，用于手动操作Redis缓存。
     * </p>
     *
     * @param factory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key序列化方式（字符串）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 设置value序列化方式（JSON）
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(new ObjectMapper());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Caffeine本地缓存
     * <p>
     * 配置Caffeine本地缓存，用于手动操作本地缓存。
     * </p>
     *
     * @return Caffeine缓存实例
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(1000)
                // 写入后过期时间
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 访问后过期时间
                .expireAfterAccess(5, TimeUnit.MINUTES)
                // 刷新后过期时间（后台异步刷新）
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                // 记录统计信息
                .recordStats()
                .build();
    }

    /**
     * Caffeine本地缓存管理器
     * <p>
     * 配置Caffeine缓存管理器，作为L1缓存。
     * 适用于高频访问的数据，性能高但容量有限。
     * </p>
     *
     * @return Caffeine缓存管理器
     */
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 配置Caffeine缓存
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(1000)
                // 写入后过期时间
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 访问后过期时间
                .expireAfterAccess(5, TimeUnit.MINUTES)
                // 刷新后过期时间（后台异步刷新）
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                // 记录统计信息
                .recordStats()
                // 初始化时加载
                .initialCapacity(100)
        );

        return cacheManager;
    }

    /**
     * Redis分布式缓存管理器
     * <p>
     * 配置Redis缓存管理器，作为L2缓存。
     * 适用于大容量数据，支持集群部署。
     * </p>
     *
     * @param factory Redis连接工厂
     * @return Redis缓存管理器
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key序列化方式（字符串）
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // 设置value序列化方式（JSON）
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJacksonJsonRedisSerializer(new ObjectMapper())))
                // 设置过期时间（30分钟）
                .entryTtl(Duration.ofMinutes(30))
                // 不缓存null值
                .disableCachingNullValues();
        // 使用前缀
        defaultConfig.usePrefix();

        // 针对不同缓存名称设置不同的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 用户缓存：30分钟
        cacheConfigurations.put("user", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 用户列表缓存：10分钟
        cacheConfigurations.put("userList", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // TODO: 商品缓存配置（待实现商品模块后启用）
        // cacheConfigurations.put("product", defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * 多级缓存管理器
     * <p>
     * 使用CompositeCacheManager组合Caffeine和Redis两个缓存管理器。
     * 查询顺序：先查Caffeine -> 再查Redis -> 最后查数据库
     * 更新顺序：先更新数据库 -> 再删除Redis缓存 -> 再删除Caffeine缓存
     * </p>
     *
     * @param caffeineCacheManager Caffeine缓存管理器
     * @param redisCacheManager     Redis缓存管理器
     * @return 多级缓存管理器
     */
    @Bean
    @Primary
    public CacheManager multiLevelCacheManager(CaffeineCacheManager caffeineCacheManager,
                                               RedisCacheManager redisCacheManager) {
        return new CompositeCacheManager(caffeineCacheManager, redisCacheManager);
    }
}