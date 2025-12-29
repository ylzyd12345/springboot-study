package com.kev1n.spring4demo.web.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Prometheus 监控配置
 * 配置通用标签和自定义指标
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class CustomPrometheusConfig {

    /**
     * 配置 Prometheus 通用标签
     * 这些标签会自动添加到所有指标中
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> prometheusMetricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "spring4demo")
                .commonTags("region", "default");
    }
}