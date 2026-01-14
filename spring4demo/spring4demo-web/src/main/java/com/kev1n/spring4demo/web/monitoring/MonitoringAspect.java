package com.kev1n.spring4demo.web.monitoring;

import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 监控切面
 * 自动记录方法执行时间和调用次�? * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MonitoringAspect {

    private final CustomMetrics customMetrics;

    /**
     * 监控Controller方法
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object monitorController(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "controller");
    }

    /**
     * 监控Service方法
     */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object monitorService(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "service");
    }

    /**
     * 监控Repository方法
     */
    @Around("@within(org.springframework.stereotype.Repository)")
    public Object monitorRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "repository");
    }

    /**
     * 通用方法监控
     */
    private Object monitorMethod(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = customMetrics.startApiTimer();
        String[] tags = {"class", className, "method", methodName, "layer", layer};

        try {
            Object result = joinPoint.proceed();

            // 记录成功指标
            customMetrics.incrementCustomCounter("method.invocation.count", tags);
            customMetrics.stopTimer(sample, "method.execution.time", tags);

            log.debug("Method {}.{}, execution time recorded", className, methodName);
            return result;

        } catch (RuntimeException e) {
            // 记录运行时异常指标
            customMetrics.incrementCustomCounter("method.exception.count",
                    "class", className, "method", methodName, "layer", layer,
                    "exception", e.getClass().getSimpleName());

            customMetrics.incrementCustomCounter("error.count",
                    "type", "runtime_exception", "class", className, "method", methodName);

            log.error("Method {}.{}, runtime exception recorded: {}", className, methodName, e.getMessage());
            throw e;
        } catch (Error e) {
            // 记录错误指标
            customMetrics.incrementCustomCounter("method.exception.count",
                    "class", className, "method", methodName, "layer", layer,
                    "exception", e.getClass().getSimpleName());

            customMetrics.incrementCustomCounter("error.count",
                    "type", "error", "class", className, "method", methodName);

            log.error("Method {}.{}, error recorded: {}", className, methodName, e.getMessage());
            throw e;
        } catch (Throwable e) {
            // 记录其他异常指标
            customMetrics.incrementCustomCounter("method.exception.count",
                    "class", className, "method", methodName, "layer", layer,
                    "exception", e.getClass().getSimpleName());

            customMetrics.incrementCustomCounter("error.count",
                    "type", "checked_exception", "class", className, "method", methodName);

            log.error("Method {}.{}, checked exception recorded: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * 监控HTTP请求
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object monitorHttpRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        Timer.Sample sample = customMetrics.startApiTimer();

        try {
            Object result = joinPoint.proceed();

            // 记录API请求指标
            customMetrics.recordApiRequest(endpoint, method, 200);
            customMetrics.recordApiResponseTime(sample, endpoint, method);

            return result;

        } catch (IllegalArgumentException e) {
            // 记录参数错误指标
            customMetrics.recordApiRequest(endpoint, method, 400);
            customMetrics.recordApiResponseTime(sample, endpoint, method);
            log.warn("HTTP request parameter error: {} {} - {}", method, endpoint, e.getMessage());
            throw e;
        } catch (SecurityException e) {
            // 记录安全错误指标
            customMetrics.recordApiRequest(endpoint, method, 403);
            customMetrics.recordApiResponseTime(sample, endpoint, method);
            log.warn("HTTP request security error: {} {} - {}", method, endpoint, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            // 记录运行时异常指标
            int statusCode = getStatusCodeFromRuntimeException(e);
            customMetrics.recordApiRequest(endpoint, method, statusCode);
            customMetrics.recordApiResponseTime(sample, endpoint, method);
            log.error("HTTP request runtime error: {} {} - {}", method, endpoint, e.getMessage());
            throw e;
        } catch (Throwable e) {
            // 记录其他异常指标
            customMetrics.recordApiRequest(endpoint, method, 500);
            customMetrics.recordApiResponseTime(sample, endpoint, method);
            log.error("HTTP request error: {} {} - {}", method, endpoint, e.getMessage());
            throw e;
        }
    }

    /**
     * 从运行时异常中获取状态码
     */
    private int getStatusCodeFromRuntimeException(RuntimeException e) {
        if (e instanceof IllegalArgumentException) {
            return 400;
        } else if (e instanceof SecurityException) {
            return 403;
        } else {
            return 500;
        }
    }

    /**
     * 监控数据库操作
     */
    @Around("execution(* com.kev1n.spring4demo.core.repository.*.*(..))")
    public Object monitorDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = customMetrics.startDatabaseTimer();

        try {
            Object result = joinPoint.proceed();

            customMetrics.recordDatabaseQueryTime(sample, methodName, className);

            return result;

        } catch (java.sql.SQLException e) {
            customMetrics.recordDatabaseQueryTime(sample, methodName, className);
            customMetrics.recordBusinessException("SQLException", "database_operation");
            log.error("Database operation SQL error: {}.{} - {}", className, methodName, e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {
            customMetrics.recordDatabaseQueryTime(sample, methodName, className);
            customMetrics.recordBusinessException("DataAccessException", "database_operation");
            log.error("Database operation data access error: {}.{} - {}", className, methodName, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            customMetrics.recordDatabaseQueryTime(sample, methodName, className);
            customMetrics.recordBusinessException(e.getClass().getSimpleName(), "database_operation");
            log.error("Database operation runtime error: {}.{} - {}", className, methodName, e.getMessage());
            throw e;
        } catch (Throwable e) {
            customMetrics.recordDatabaseQueryTime(sample, methodName, className);
            customMetrics.recordBusinessException(e.getClass().getSimpleName(), "database_operation");
            log.error("Database operation error: {}.{} - {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * 监控缓存操作
     */
    @Around("@annotation(org.springframework.cache.annotation.Cacheable) || " +
            "@annotation(org.springframework.cache.annotation.CachePut) || " +
            "@annotation(org.springframework.cache.annotation.CacheEvict)")
    public Object monitorCacheOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start();

        try {
            Object result = joinPoint.proceed();

            customMetrics.recordCacheHit(joinPoint.getTarget().getClass().getSimpleName());
            customMetrics.recordApiResponseTime(sample, "cache", "access");

            return result;

        } catch (RuntimeException e) {
            customMetrics.recordCacheMiss(joinPoint.getTarget().getClass().getSimpleName());
            customMetrics.recordApiResponseTime(sample, "cache", "access");
            log.error("Cache operation runtime error: {} - {}", joinPoint.getTarget().getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Throwable e) {
            customMetrics.recordCacheMiss(joinPoint.getTarget().getClass().getSimpleName());
            customMetrics.recordApiResponseTime(sample, "cache", "access");
            log.error("Cache operation error: {} - {}", joinPoint.getTarget().getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}
