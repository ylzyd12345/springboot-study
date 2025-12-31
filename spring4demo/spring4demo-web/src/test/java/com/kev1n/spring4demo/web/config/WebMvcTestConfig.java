package com.kev1n.spring4demo.web.config;

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
}