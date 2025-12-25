package com.kev1n.spring4demo.common.monitoring;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 监控切面
 * 自动记录方法执行时间和调用次数
 * 
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
            
        } catch (Exception e) {
            // 记录异常指标
            customMetrics.incrementCustomCounter("method.exception.count", 
                    "class", className, "method", methodName, "layer", layer, 
                    "exception", e.getClass().getSimpleName());
            
            customMetrics.incrementCustomCounter("error.count", 
                    "type", "method_exception", "class", className, "method", methodName);
            
            log.error("Method {}.{}, exception recorded: {}", className, methodName, e.getMessage());
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
            
        } catch (Exception e) {
            // 记录错误指标
            int statusCode = getStatusCodeFromException(e);
            customMetrics.recordApiRequest(endpoint, method, statusCode);
            customMetrics.recordApiResponseTime(sample, endpoint, method);
            
            throw e;
        }
    }

    /**
     * 从异常中获取状态码
     */
    private int getStatusCodeFromException(Exception e) {
        // 这里可以根据具体的异常类型返回不同的状态码
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
            
        } catch (Exception e) {
            customMetrics.recordDatabaseQueryTime(sample, methodName, className);
            customMetrics.recordBusinessException(e.getClass().getSimpleName(), "database_operation");
            
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
            sample.stop(customMetrics.apiResponseTimer);
            
            return result;
            
        } catch (Exception e) {
            customMetrics.recordCacheMiss(joinPoint.getTarget().getClass().getSimpleName());
            sample.stop(customMetrics.apiResponseTimer);
            
            throw e;
        }
    }
}