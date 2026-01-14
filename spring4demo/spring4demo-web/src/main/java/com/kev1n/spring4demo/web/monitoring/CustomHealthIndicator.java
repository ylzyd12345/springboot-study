package com.kev1n.spring4demo.web.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义健康检查指示器
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        try {
            // 检查数据库连接
            boolean databaseHealthy = checkDatabaseConnection();
            details.put("database", databaseHealthy ? "UP" : "DOWN");

            // 检查内存使用情况
            Map<String, Object> memoryInfo = getMemoryInfo();
            details.put("memory", memoryInfo);

            // 检查线程状态
            Map<String, Object> threadInfo = getThreadInfo();
            details.put("threads", threadInfo);

            // 检查系统负载
            double systemLoadAverage = getSystemLoadAverage();
            details.put("systemLoadAverage", systemLoadAverage);

            // 综合健康状态判断
            boolean isHealthy = databaseHealthy && isMemoryHealthy(memoryInfo) && isSystemLoadHealthy(systemLoadAverage);

            if (isHealthy) {
                return Health.up()
                        .withDetails(details)
                        .build();
            } else {
                return Health.down()
                        .withDetails(details)
                        .build();
            }

        } catch (java.sql.SQLException e) {
            log.error("Database health check failed", e);
            details.put("error", e.getMessage());
            details.put("errorType", "SQLException");
            return Health.down()
                    .withDetails(details)
                    .build();
        } catch (NumberFormatException e) {
            log.error("Health check parsing error", e);
            details.put("error", e.getMessage());
            details.put("errorType", "NumberFormatException");
            return Health.down()
                    .withDetails(details)
                    .build();
        } catch (RuntimeException e) {
            log.error("Health check runtime error", e);
            details.put("error", e.getMessage());
            details.put("errorType", e.getClass().getSimpleName());
            return Health.down()
                    .withDetails(details)
                    .build();
        } catch (Exception e) {
            log.error("Health check unexpected error", e);
            details.put("error", e.getMessage());
            details.put("errorType", "UnexpectedException");
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }

    /**
     * 检查数据库连接
     */
    private boolean checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5秒
        } catch (java.sql.SQLException e) {
            log.warn("Database SQL health check failed", e);
            return false;
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Database data access health check failed", e);
            return false;
        } catch (RuntimeException e) {
            log.warn("Database runtime health check failed", e);
            return false;
        } catch (Exception e) {
            log.warn("Database health check failed", e);
            return false;
        }
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("max", maxMemory / 1024 / 1024 + " MB");
        memoryInfo.put("total", totalMemory / 1024 / 1024 + " MB");
        memoryInfo.put("used", usedMemory / 1024 / 1024 + " MB");
        memoryInfo.put("free", freeMemory / 1024 / 1024 + " MB");
        memoryInfo.put("usage", String.format("%.2f%%", (double) usedMemory / maxMemory * 100));

        return memoryInfo;
    }

    /**
     * 获取线程信息
     */
    private Map<String, Object> getThreadInfo() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int activeThreads = threadGroup.activeCount();
        int totalThreads = threadGroup.activeCount();

        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put("active", activeThreads);
        threadInfo.put("total", totalThreads);

        return threadInfo;
    }

    /**
     * 获取系统负载
     */
    private double getSystemLoadAverage() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    /**
     * 检查内存是否健
     */
    private boolean isMemoryHealthy(Map<String, Object> memoryInfo) {
        String usageStr = (String) memoryInfo.get("usage");
        double usage = Double.parseDouble(usageStr.replace("%", ""));
        return usage < 80.0; // 内存使用率低�?0%认为健康
    }

    /**
     * 检查系统负载是否健
     */
    private boolean isSystemLoadHealthy(double systemLoadAverage) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return systemLoadAverage < availableProcessors * 0.8; // 系统负载低于CPU核心数的80%认为健康
    }
}
