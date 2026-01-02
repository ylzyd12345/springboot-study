package com.kev1n.spring4demo.core;

import com.kev1n.spring4demo.common.config.RedissonConfig;
import com.kev1n.spring4demo.common.config.SeataConfig;
import org.apache.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * 测试应用配置类
 *
 * <p>用于集成测试的 Spring Boot 应用配置</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootApplication(
    exclude = {
        DataRedisAutoConfiguration.class,
        DataRedisReactiveAutoConfiguration.class,
        RedissonAutoConfigurationV2.class,
        SeataAutoConfiguration.class
    }
)
@ComponentScan(
    basePackages = "com.kev1n.spring4demo",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {RedissonConfig.class, SeataConfig.class}
    )
)
public class TestApplication {

    /**
     * 创建 Mock 的 RedissonClient
     *
     * @return Mock 的 RedissonClient
     */
    @Bean
    @Primary
    public RedissonClient mockRedissonClient() {
        return mock(RedissonClient.class);
    }
}