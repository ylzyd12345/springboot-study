package com.kev1n.spring4demo.common.interceptor;

import com.kev1n.spring4demo.common.annotation.ApiVersion;
import com.kev1n.spring4demo.common.annotation.DeprecatedApi;
import com.kev1n.spring4demo.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * API版本拦截器
 * 处理API版本控制和废弃警告
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final String DEFAULT_API_VERSION = "1";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> controllerClass = handlerMethod.getBeanType();

        // 获取请求的API版本
        String requestVersion = request.getHeader(API_VERSION_HEADER);
        if (requestVersion == null) {
            requestVersion = DEFAULT_API_VERSION;
        }

        // 检查API版本
        checkApiVersion(controllerClass, method, requestVersion, response);

        return true;
    }

    /**
     * 检查API版本
     */
    private void checkApiVersion(Class<?> controllerClass, Method method, String requestVersion, HttpServletResponse response) {
        // 检查方法级别的版本注解
        ApiVersion methodVersion = method.getAnnotation(ApiVersion.class);
        if (methodVersion != null) {
            handleVersionCheck(methodVersion, requestVersion, response);
            return;
        }

        // 检查类级别的版本注解
        ApiVersion classVersion = controllerClass.getAnnotation(ApiVersion.class);
        if (classVersion != null) {
            handleVersionCheck(classVersion, requestVersion, response);
        }
    }

    /**
     * 处理版本检查
     */
    private void handleVersionCheck(ApiVersion apiVersion, String requestVersion, HttpServletResponse response) {
        int requestVersionNum = Integer.parseInt(requestVersion);
        int apiVersionNum = apiVersion.value();

        // 版本不匹配时返回错误
        if (requestVersionNum != apiVersionNum) {
            log.warn("API版本不匹配: 请求版本={}, API版本={}", requestVersion, apiVersionNum);
            throw new BusinessException("API版本不匹配，当前版本: " + apiVersionNum);
        }

        // 检查是否为废弃API
        DeprecatedApi deprecatedApi = getDeprecatedAnnotation(apiVersion);
        if (deprecatedApi != null) {
            handleDeprecatedApi(deprecatedApi, response);
        }
    }

    /**
     * 获取废弃注解
     */
    private DeprecatedApi getDeprecatedAnnotation(ApiVersion apiVersion) {
        // 这里可以根据版本号查找对应的废弃注解
        // 简化实现，实际应该从配置或数据库中查询
        if (apiVersion.value() < 2) {
            return new DeprecatedApi() {
                @Override
                public String reason() {
                    return "API版本过低，建议升级到v2.0";
                }

                @Override
                public String since() {
                    return "2024-01-01";
                }

                @Override
                public String useInstead() {
                    return "v2.0";
                }

                @Override
                public String removeAfter() {
                    return "2024-12-31";
                }

                @Override
                public Class<? extends java.lang.annotation.Annotation> annotationType() {
                    return DeprecatedApi.class;
                }
            };
        }
        return null;
    }

    /**
     * 处理废弃API
     */
    private void handleDeprecatedApi(DeprecatedApi deprecatedApi, HttpServletResponse response) {
        log.warn("使用废弃API: 原因={}, 建议使用={}", deprecatedApi.reason(), deprecatedApi.useInstead());
        
        // 添加废弃警告头
        response.setHeader("X-API-Deprecated", "true");
        response.setHeader("X-API-Deprecated-Reason", deprecatedApi.reason());
        response.setHeader("X-API-Deprecated-Since", deprecatedApi.since());
        response.setHeader("X-API-Use-Instead", deprecatedApi.useInstead());
        response.setHeader("X-API-Remove-After", deprecatedApi.removeAfter());
    }
}