package com.kev1n.spring4demo.web.config;

import cn.dev33.satoken.dao.SaTokenDao;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.kev1n.spring4demo.web.handler.GlobalExceptionHandler;
import com.kev1n.spring4demo.web.interceptor.ApiVersionInterceptor;
import com.kev1n.spring4demo.web.interceptor.MetricsInterceptor;
import com.kev1n.spring4demo.web.interceptor.SaTokenInterceptor;
import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;

/**
 * Web MVC 测试配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootApplication(
    scanBasePackages = "com.kev1n.spring4demo.web",
    exclude = {
        DataSourceAutoConfiguration.class,
        DynamicDataSourceAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        SeataAutoConfiguration.class,
        RedissonAutoConfigurationV2.class,
    }
)
@ComponentScan(
    basePackages = "com.kev1n.spring4demo.web",
    useDefaultFilters = false,
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            WebMvcConfig.class,
            GlobalExceptionHandler.class,
            SaTokenInterceptor.class,
            ApiVersionInterceptor.class,
            MetricsInterceptor.class,
        }
    )
)
public class WebMvcTestConfig {

    @Bean
    @Primary
    public ApiVersionInterceptor apiVersionInterceptor() {
        return mock(ApiVersionInterceptor.class);
    }

    @Bean
    @Primary
    public MetricsInterceptor metricsInterceptor() {
        return mock(MetricsInterceptor.class);
    }

    /**
     * Sa-Token 内存存储配置（测试用）
     */
    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new SaTokenDao() {
            // 使用 ConcurrentHashMap 作为内存存储
            private final Map<String, Object> dataMap = new ConcurrentHashMap<>();

            @Override
            public String get(String key) {
                return (String) dataMap.get(key);
            }

            @Override
            public void set(String key, String value, long timeout) {
                dataMap.put(key, value);
            }

            @Override
            public void update(String key, String value) {
                dataMap.put(key, value);
            }

            @Override
            public void delete(String key) {
                dataMap.remove(key);
            }

            @Override
            public long getTimeout(String key) {
                return 0;
            }

            @Override
            public void updateTimeout(String key, long timeout) {
            }

            @Override
            public Object getObject(String key) {
                return dataMap.get(key);
            }

            @Override
            public long getObjectTimeout(String key) {
                return 0;
            }

            @Override
            public void setObject(String key, Object value, long timeout) {
                dataMap.put(key, value);
            }

            @Override
            public void updateObject(String key, Object value) {
                dataMap.put(key, value);
            }

            @Override
            public void updateObjectTimeout(String key, long timeout) {
            }

            @Override
            public void deleteObject(String key) {
                dataMap.remove(key);
            }

            @Override
            public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
                return List.of();
            }
        };
    }
}