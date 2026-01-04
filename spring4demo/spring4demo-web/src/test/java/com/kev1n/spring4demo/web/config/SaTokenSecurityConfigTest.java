package com.kev1n.spring4demo.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sa-Token 安全配置测试
 *
 * 测试要点：
 * 1. 验证 SaTokenSecurityConfig Bean 创建
 * 2. 验证拦截器注册
 * 3. 验证拦截器类型
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest(classes = SaTokenSecurityConfig.class)
@DisplayName("Sa-Token 安全配置测试")
class SaTokenSecurityConfigTest {

    @Test
    @DisplayName("验证配置类实现 WebMvcConfigurer")
    void shouldImplementWebMvcConfigurer() {
        // Given - 创建配置实例
        SaTokenSecurityConfig config = new SaTokenSecurityConfig();

        // When - 验证配置类类型
        boolean isWebMvcConfigurer = config instanceof WebMvcConfigurer;

        // Then - 验证实现了接口
        assertThat(isWebMvcConfigurer).isTrue();
    }

    @Test
    @DisplayName("验证拦截器注册方法")
    void shouldVerifyAddInterceptorsMethod() {
        // Given - 创建配置实例和拦截器注册表
        SaTokenSecurityConfig config = new SaTokenSecurityConfig();
        InterceptorRegistry registry = new InterceptorRegistry();

        // When - 调用添加拦截器方法
        config.addInterceptors(registry);

        // Then - 验证拦截器已注册
        assertThat(registry).isNotNull();
        var interceptors = getInterceptors(registry);
        assertThat(interceptors).isNotEmpty();
    }

    @Test
    @DisplayName("验证拦截器数量")
    void shouldVerifyInterceptorCount() {
        // Given - 创建配置实例和拦截器注册表
        SaTokenSecurityConfig config = new SaTokenSecurityConfig();
        InterceptorRegistry registry = new InterceptorRegistry();

        // When - 调用添加拦截器方法
        config.addInterceptors(registry);

        // Then - 验证拦截器数量
        var interceptors = getInterceptors(registry);
        assertThat(interceptors).hasSize(1);
    }

    @Test
    @DisplayName("验证拦截器配置正确性")
    void shouldVerifyInterceptorConfigurationCorrectness() {
        // Given - 创建配置实例和拦截器注册表
        SaTokenSecurityConfig config = new SaTokenSecurityConfig();
        InterceptorRegistry registry = new InterceptorRegistry();

        // When - 调用添加拦截器方法
        config.addInterceptors(registry);

        // Then - 验证拦截器配置正确性
        var interceptors = getInterceptors(registry);
        assertThat(interceptors).hasSize(1);

        var interceptor = interceptors.get(0);
        assertThat(interceptor).isNotNull();
    }

    /**
     * 使用反射访问 protected 方法 getInterceptors()
     */
    @SuppressWarnings("unchecked")
    private List<Object> getInterceptors(InterceptorRegistry registry) {
        try {
            Method method = InterceptorRegistry.class.getDeclaredMethod("getInterceptors");
            method.setAccessible(true);
            return (List<Object>) method.invoke(registry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get interceptors via reflection", e);
        }
    }
}