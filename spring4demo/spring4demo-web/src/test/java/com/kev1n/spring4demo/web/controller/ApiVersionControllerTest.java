package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.web.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ApiVersionController 测试
 *
 * <p>测试 ApiVersionController 的所有 REST API 接口</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("API版本控制器测试")
class ApiVersionControllerTest extends BaseWebTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // ApiVersionController 不需要登录，所以不需要 mockLogin
    }

    @Test
    @DisplayName("获取API版本列表 - 成功")
    void getApiVersions_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.versions").isMap())
                .andExpect(jsonPath("$.data.current").value("v2"))
                .andExpect(jsonPath("$.data.recommended").value("v2"))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.versions.v1.version").value("1.0"))
                .andExpect(jsonPath("$.data.versions.v1.status").value("DEPRECATED"))
                .andExpect(jsonPath("$.data.versions.v2.version").value("2.0"))
                .andExpect(jsonPath("$.data.versions.v2.status").value("STABLE"));
    }

    @Test
    @DisplayName("获取版本详情 - v1版本")
    void getVersionInfo_V1() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.version").value("1.0"))
                .andExpect(jsonPath("$.data.status").value("DEPRECATED"))
                .andExpect(jsonPath("$.data.deprecatedSince").value("2024-01-01"))
                .andExpect(jsonPath("$.data.removeAfter").value("2024-12-31"))
                .andExpect(jsonPath("$.data.useInstead").value("v2.0"))
                .andExpect(jsonPath("$.data.endpoints").isArray())
                .andExpect(jsonPath("$.data.endpoints.length()").value(5));
    }

    @Test
    @DisplayName("获取版本详情 - v2版本")
    void getVersionInfo_V2() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.version").value("2.0"))
                .andExpect(jsonPath("$.data.status").value("STABLE"))
                .andExpect(jsonPath("$.data.deprecatedSince").isEmpty())
                .andExpect(jsonPath("$.data.removeAfter").isEmpty())
                .andExpect(jsonPath("$.data.useInstead").isEmpty())
                .andExpect(jsonPath("$.data.endpoints").isArray())
                .andExpect(jsonPath("$.data.endpoints.length()").value(7));
    }

    @Test
    @DisplayName("获取版本详情 - 不支持的版本")
    void getVersionInfo_NotSupported() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("不支持的版本: v3"));
    }

    @Test
    @DisplayName("检查版本兼容性 - 稳定版本")
    void checkCompatibility_Stable() throws Exception {
        // Given
        String requestBody = """
            {
                "version": "v2"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.compatible").value(true))
                .andExpect(jsonPath("$.data.message").value("版本兼容，推荐使用"))
                .andExpect(jsonPath("$.data.recommended").isEmpty());
    }

    @Test
    @DisplayName("检查版本兼容性 - 已弃用版本")
    void checkCompatibility_Deprecated() throws Exception {
        // Given
        String requestBody = """
            {
                "version": "v1"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.compatible").value(true))
                .andExpect(jsonPath("$.data.message").value("版本兼容，但建议升级到v2.0"))
                .andExpect(jsonPath("$.data.recommended").value("v2.0"));
    }

    @Test
    @DisplayName("检查版本兼容性 - 不支持的版本")
    void checkCompatibility_NotSupported() throws Exception {
        // Given
        String requestBody = """
            {
                "version": "v99"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.compatible").value(false))
                .andExpect(jsonPath("$.data.message").value("不支持的版本: v99"))
                .andExpect(jsonPath("$.data.supportedVersions").isArray())
                .andExpect(jsonPath("$.data.supportedVersions.length()").value(2));
    }

    @Test
    @DisplayName("检查版本兼容性 - 版本为空")
    void checkCompatibility_EmptyVersion() throws Exception {
        // Given
        String requestBody = """
            {
                "version": ""
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("客户端版本不能为空"));
    }

    @Test
    @DisplayName("检查版本兼容性 - 版本为null")
    void checkCompatibility_NullVersion() throws Exception {
        // Given
        String requestBody = """
            {
                "version": null
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("客户端版本不能为空"));
    }

    @Test
    @DisplayName("检查版本兼容性 - 无效的JSON")
    void checkCompatibility_InvalidJson() throws Exception {
        // Given
        String requestBody = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/version/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取版本详情 - v1版本的端点列表验证")
    void getVersionInfo_V1Endpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.endpoints[0]").value("GET /api/v1/users"))
                .andExpect(jsonPath("$.data.endpoints[1]").value("POST /api/v1/users"))
                .andExpect(jsonPath("$.data.endpoints[2]").value("GET /api/v1/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[3]").value("PUT /api/v1/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[4]").value("DELETE /api/v1/users/{id}"));
    }

    @Test
    @DisplayName("获取版本详情 - v2版本的端点列表验证")
    void getVersionInfo_V2Endpoints() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.endpoints[0]").value("GET /api/v2/users"))
                .andExpect(jsonPath("$.data.endpoints[1]").value("POST /api/v2/users"))
                .andExpect(jsonPath("$.data.endpoints[2]").value("GET /api/v2/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[3]").value("PUT /api/v2/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[4]").value("PATCH /api/v2/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[5]").value("DELETE /api/v2/users/{id}"))
                .andExpect(jsonPath("$.data.endpoints[6]").value("GET /api/v2/users/search"));
    }

    @Test
    @DisplayName("获取版本详情 - v1版本描述验证")
    void getVersionInfo_V1Description() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("用户管理API v1.0"));
    }

    @Test
    @DisplayName("获取版本详情 - v2版本描述验证")
    void getVersionInfo_V2Description() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("用户管理API v2.0 - 稳定版本"));
    }

    @Test
    @DisplayName("获取API版本列表 - 版本数量验证")
    void getApiVersions_VersionCount() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/version")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.versions.v1").exists())
                .andExpect(jsonPath("$.data.versions.v2").exists());
    }
}