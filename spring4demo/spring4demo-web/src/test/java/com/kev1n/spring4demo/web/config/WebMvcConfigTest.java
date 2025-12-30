package com.kev1n.spring4demo.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web MVC 配置测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest(classes = WebMvcTestConfig.class)
@AutoConfigureMockMvc
@DisplayName("Web MVC 配置测试")
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("验证需要登录的路径被拦截 - /")
    void testProtectedPathBlocked_RootPath() throws Exception {
        // When & Then - 访问根路径，应该被拦截返回 401
        mockMvc.perform(get("/"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("验证需要登录的路径被拦截 - /api/users")
    void testProtectedPathBlocked_ApiUsers() throws Exception {
        // When & Then - 访问需要认证的 API 接口，应该被拦截返回 401
        mockMvc.perform(get("/api/users"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("验证需要登录的路径被拦截 - /api/profile")
    void testProtectedPathBlocked_ApiProfile() throws Exception {
        // When & Then - 访问需要认证的 API 接口，应该被拦截返回 401
        mockMvc.perform(get("/api/profile"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /auth/login (POST)")
    void testPublicPathAccessible_AuthLogin() throws Exception {
        // When & Then - 访问登录接口，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(post("/auth/login"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /auth/register (POST)")
    void testPublicPathAccessible_AuthRegister() throws Exception {
        // When & Then - 访问注册接口，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(post("/auth/register"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /doc.html")
    void testPublicPathAccessible_DocHtml() throws Exception {
        // When & Then - 访问文档页面，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/doc.html"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /v3/api-docs")
    void testPublicPathAccessible_ApiDocs() throws Exception {
        // When & Then - 访问 API 文档，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/v3/api-docs"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /actuator/health")
    void testPublicPathAccessible_ActuatorHealth() throws Exception {
        // When & Then - 访问健康检查端点，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/actuator/health"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证不需要登录的路径可以访问 - /error")
    void testPublicPathAccessible_Error() throws Exception {
        // When & Then - 访问错误页面，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/error"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证 API 公开路径可以访问 - /api/public/info")
    void testPublicApiPathAccessible() throws Exception {
        // When & Then - 访问公开 API 路径，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/api/public/info"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证 API 认证路径可以访问 - /api/auth/refresh (POST)")
    void testAuthApiPathAccessible() throws Exception {
        // When & Then - 访问认证相关 API 路径，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(post("/api/auth/refresh"))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("验证 API 健康检查路径可以访问 - /api/health/ping")
    void testHealthApiPathAccessible() throws Exception {
        // When & Then - 访问健康检查 API 路径，不应该被拦截，返回 404（因为没有对应的控制器）
        mockMvc.perform(get("/api/health/ping"))
               .andExpect(status().isNotFound());
    }
}