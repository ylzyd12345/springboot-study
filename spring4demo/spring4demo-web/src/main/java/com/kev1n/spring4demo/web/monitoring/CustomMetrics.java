package com.kev1n.spring4demo.web.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    // 业务计数
    private final Counter apiRequestCounter;
    private final Counter errorCounter;

    // 业务计时
    private final Timer apiResponseTimer;
    private final Timer databaseQueryTimer;
    private final Timer cacheAccessTimer;

    // 业务仪表
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    // 自定义指标
    private final ConcurrentHashMap<String, AtomicLong> customGauges = new ConcurrentHashMap<>();

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.apiRequestCounter = Counter.builder("api.request.count")
                .description("API请求次数")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("error.count")
                .description("错误次数")
                .register(meterRegistry);

        // 初始化计时器
        this.apiResponseTimer = Timer.builder("api.response.time")
                .description("API响应时间")
                .register(meterRegistry);

        this.databaseQueryTimer = Timer.builder("database.query.time")
                .description("数据库查询时间")
                .register(meterRegistry);

        this.cacheAccessTimer = Timer.builder("cache.access.time")
                .description("缓存访问时间")
                .register(meterRegistry);

        // 初始化仪表盘

        Gauge.builder("cache.hits.count", cacheHits, AtomicLong::get)
                .description("缓存命中次数")
                .register(meterRegistry);

        Gauge.builder("cache.misses.count", cacheMisses, AtomicLong::get)
                .description("缓存未命中次数")
                .register(meterRegistry);
    }

    // API请求指标
    public void recordApiRequest(String endpoint, String method, int statusCode) {
        Counter apiCounter = Counter.builder("api_request")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry);
        apiCounter.increment();
        
        if (statusCode >= 400) {
            Counter errorCounter = Counter.builder("api_error")
                    .tag("type", "api_error")
                    .tag("endpoint", endpoint)
                    .tag("status", String.valueOf(statusCode))
                    .register(meterRegistry);
            errorCounter.increment();
        }
    }

    // API响应时间指标
    public Timer.Sample startApiTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordApiResponseTime(Timer.Sample sample, String endpoint, String method) {
        sample.stop(Timer.builder("api.response.time")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry));
    }

    // 数据库查询
    public Timer.Sample startDatabaseTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordDatabaseQueryTime(Timer.Sample sample, String operation, String table) {
        sample.stop(Timer.builder("database.query.time")
                .tag("operation", operation)
                .tag("table", table)
                .register(meterRegistry));
    }

    // 缓存访问指标
    public void recordCacheHit(String cacheName) {
        cacheHits.incrementAndGet();
        meterRegistry.counter("cache.access.count", "cache", cacheName, "result", "hit").increment();
    }

    public void recordCacheMiss(String cacheName) {
        cacheMisses.incrementAndGet();
        meterRegistry.counter("cache.access.count", "cache", cacheName, "result", "miss").increment();
    }

    // 自定义业务
    public void incrementCustomCounter(String name, String... tags) {
        meterRegistry.counter(name, tags).increment();
    }

    // 停止计时
    public void stopTimer(Timer.Sample sample, String timerName, String... tags) {
        sample.stop(Timer.builder(timerName)
                .tags(tags)
                .register(meterRegistry));
    }

    public void setCustomGauge(String name, double value, String... tags) {
        AtomicLong gauge = customGauges.computeIfAbsent(name, k -> new AtomicLong(0));
        gauge.set((long) value);
        
        Gauge.builder(name, gauge, AtomicLong::get)
                .tags(tags)
                .register(meterRegistry);
    }

    // 业务异常指标
    public void recordBusinessException(String exceptionType, String operation) {
        Counter counter = Counter.builder("business_exception")
                .tag("type", "business_exception")
                .tag("exception", exceptionType)
                .tag("operation", operation)
                .register(meterRegistry);
        counter.increment();
    }

    // 系统资源指标
    public void recordSystemMetrics() {
        // JVM内存指标
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        meterRegistry.gauge("jvm.memory.used.bytes", usedMemory);
        meterRegistry.gauge("jvm.memory.free.bytes", freeMemory);
        meterRegistry.gauge("jvm.memory.total.bytes", totalMemory);

        // 线程数指
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int activeThreads = threadGroup.activeCount();
        meterRegistry.gauge("jvm.threads.active.count", activeThreads);
    }

    // 定时记录系统指标
    public void startSystemMetricsReporting() {
        new Thread(() -> {
            while (true) {
                try {
                    recordSystemMetrics();
                    Thread.sleep(30000);
                    } catch (InterruptedException e) {
                    log.warn("System metrics reporting interrupted", e);
                    break;
                }
            }
        }).start();
    }
}
