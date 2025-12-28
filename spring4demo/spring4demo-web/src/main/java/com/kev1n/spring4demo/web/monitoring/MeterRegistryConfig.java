package com.kev1n.spring4demo.web.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer监控配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class MeterRegistryConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "spring4demo")
                .commonTags("region", "default");
    }

    /**
     * 记录方法执行时间
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 停止计时器并记录
     */
    public void stopTimer(Timer.Sample sample, String timerName, String... tags) {
        sample.stop(Timer.builder(timerName)
                .tags(tags)
                .register(meterRegistry));
    }

    /**
     * 记录计数*/
    public void incrementCounter(String counterName, String... tags) {
        meterRegistry.counter(counterName, tags).increment();
    }

    /**
     * 记录仪表盘数*/
    public void recordGauge(String gaugeName, double value, Iterable<Tag> tags) {
        meterRegistry.gauge(gaugeName, tags, value);
    }
}
