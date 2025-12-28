package com.kev1n.spring4demo.web.interceptor;

import com.kev1n.spring4demo.web.monitoring.CustomMetrics;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 监控拦截器
 * 记录HTTP请求的监控指标
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {

    private final CustomMetrics customMetrics;

    private static final String START_TIME_ATTR = "startTime";
    private static final String USER_ID_ATTR = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        
        // 记录用户信息（如果已认证）
        String userId = getCurrentUserId(request);
        if (userId != null) {
            request.setAttribute(USER_ID_ATTR, userId);
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            // 计算请求处理时间
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                
                String endpoint = request.getRequestURI();
                String method = request.getMethod();
                int statusCode = response.getStatus();
                String userId = (String) request.getAttribute(USER_ID_ATTR);
                
                // 记录API请求指标
                customMetrics.recordApiRequest(endpoint, method, statusCode);
                
                // 记录响应时间指标
                customMetrics.setCustomGauge("api.response.duration.ms", duration, 
                        "endpoint", endpoint, "method", method);
                
                // 记录用户相关指标
                if (userId != null) {
                    customMetrics.incrementCustomCounter("user.api.request.count", 
                            "userId", userId, "endpoint", endpoint, "method", method);
                }
                
                // 记录异常指标
                if (ex != null) {
                    customMetrics.recordBusinessException(ex.getClass().getSimpleName(), 
                            "http_request");
                }
                
                // 记录详细日志
                log.debug("API request completed: {} {} - {}ms - Status: {}", 
                        method, endpoint, duration, statusCode);
            }
        } catch (Exception e) {
            log.error("Error recording metrics", e);
        }
    }

    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId(HttpServletRequest request) {
        // 这里可以从SecurityContext或Sa-Token中获取用户ID
        // 简化实现，实际应该从Spring Security获取
        return request.getHeader("X-User-Id");
    }
}