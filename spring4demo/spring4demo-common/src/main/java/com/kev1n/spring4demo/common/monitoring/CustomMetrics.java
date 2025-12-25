package com.kev1n.spring4demo.common.monitoring;

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
 * 自定义监控指标
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    // 业务计数器
    private final Counter userLoginCounter;
    private final Counter userRegistrationCounter;
    private final Counter apiRequestCounter;
    private final Counter errorCounter;

    // 业务计时器
    private final Timer apiResponseTimer;
    private final Timer databaseQueryTimer;
    private final Timer cacheAccessTimer;

    // 业务仪表盘
    private final AtomicLong activeUsers = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    // 自定义指标存储
    private final ConcurrentHashMap<String, AtomicLong> customGauges = new ConcurrentHashMap<>();

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // 初始化计数器
        this.userLoginCounter = Counter.builder("user.login.count")
                .description("用户登录次数")
                .register(meterRegistry);

        this.userRegistrationCounter = Counter.builder("user.registration.count")
                .description("用户注册次数")
                .register(meterRegistry);

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
        Gauge.builder("user.active.count")
                .description("活跃用户数")
                .register(meterRegistry, activeUsers, AtomicLong::get);

        Gauge.builder("cache.hits.count")
                .description("缓存命中次数")
                .register(meterRegistry, cacheHits, AtomicLong::get);

        Gauge.builder("cache.misses.count")
                .description("缓存未命中次数")
                .register(meterRegistry, cacheMisses, AtomicLong::get);
    }

    // 用户登录指标
    public void recordUserLogin(String username, boolean success) {
        userLoginCounter.increment("success", String.valueOf(success));
        if (success) {
            activeUsers.incrementAndGet();
        }
    }

    // 用户注册指标
    public void recordUserRegistration(String username) {
        userRegistrationCounter.increment();
    }

    // API请求指标
    public void recordApiRequest(String endpoint, String method, int statusCode) {
        apiRequestCounter.increment("endpoint", endpoint, "method", method, "status", String.valueOf(statusCode));
        
        if (statusCode >= 400) {
            errorCounter.increment("type", "api_error", "endpoint", endpoint, "status", String.valueOf(statusCode));
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

    // 数据库查询指标
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

    // 自定义业务指标
    public void incrementCustomCounter(String name, String... tags) {
        meterRegistry.counter(name, tags).increment();
    }

    public void setCustomGauge(String name, double value, String... tags) {
        AtomicLong gauge = customGauges.computeIfAbsent(name, k -> new AtomicLong(0));
        gauge.set((long) value);
        
        Gauge.builder(name)
                .tags(tags)
                .register(meterRegistry, gauge, AtomicLong::get);
    }

    // 业务异常指标
    public void recordBusinessException(String exceptionType, String operation) {
        errorCounter.increment("type", "business_exception", "exception", exceptionType, "operation", operation);
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

        // 线程数指标
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
                    Thread.sleep(30000); // 每30秒记录一次
                } catch (InterruptedException e) {
                    log.warn("System metrics reporting interrupted", e);
                    break;
                }
            }
        }).start();
    }
}