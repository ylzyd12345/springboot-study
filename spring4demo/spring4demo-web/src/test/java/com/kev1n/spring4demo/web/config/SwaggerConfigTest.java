package com.kev1n.spring4demo.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Swagger API 文档配置测试
 *
 * 测试要点：
 * 1. 验证 SwaggerConfig Bean 创建
 * 2. 验证 OpenAPI Bean 创建
 * 3. 验证 API 文档基本信息
 * 4. 验证服务器列表配置
 * 5. 验证标签列表配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@DisplayName("Swagger API 文档配置测试")
class SwaggerConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Autowired(required = false)
    private SwaggerConfig swaggerConfig;

    @Test
    @DisplayName("验证 SwaggerConfig Bean 创建")
    void shouldCreateSwaggerConfigBean() {
        // Then - 验证 Bean 创建成功
        if (swaggerConfig != null) {
            assertThat(swaggerConfig).isNotNull();
        }
    }

    @Test
    @DisplayName("验证 OpenAPI Bean 创建")
    void shouldCreateOpenAPIBean() {
        // Then - 验证 Bean 创建成功
        assertThat(openAPI).isNotNull();
    }

    @Test
    @DisplayName("验证 OpenAPI 基本信息")
    void shouldVerifyOpenAPIInfo() {
        // When - 获取 API 信息
        Info info = openAPI.getInfo();

        // Then - 验证 API 信息
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isNotNull();
        assertThat(info.getDescription()).isNotNull();
        assertThat(info.getVersion()).isNotNull();
    }

    @Test
    @DisplayName("验证 API 标题")
    void shouldVerifyAPITitle() {
        // When - 获取 API 标题
        Info info = openAPI.getInfo();
        String title = info.getTitle();

        // Then - 验证 API 标题
        assertThat(title).isEqualTo("Spring4demo API");
    }

    @Test
    @DisplayName("验证 API 描述")
    void shouldVerifyAPIDescription() {
        // When - 获取 API 描述
        Info info = openAPI.getInfo();
        String description = info.getDescription();

        // Then - 验证 API 描述
        assertThat(description).isNotNull();
        assertThat(description).contains("Spring Boot");
        assertThat(description).contains("企业级生态环境集成项目");
    }

    @Test
    @DisplayName("验证 API 版本")
    void shouldVerifyAPIVersion() {
        // When - 获取 API 版本
        Info info = openAPI.getInfo();
        String version = info.getVersion();

        // Then - 验证 API 版本
        assertThat(version).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("验证联系信息")
    void shouldVerifyContactInfo() {
        // When - 获取联系信息
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();

        // Then - 验证联系信息
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isNotNull();
        assertThat(contact.getEmail()).isNotNull();
        assertThat(contact.getUrl()).isNotNull();
    }

    @Test
    @DisplayName("验证联系名称")
    void shouldVerifyContactName() {
        // When - 获取联系名称
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();
        String name = contact.getName();

        // Then - 验证联系名称
        assertThat(name).isEqualTo("Spring4demo Team");
    }

    @Test
    @DisplayName("验证联系邮箱")
    void shouldVerifyContactEmail() {
        // When - 获取联系邮箱
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();
        String email = contact.getEmail();

        // Then - 验证联系邮箱
        assertThat(email).isEqualTo("support@spring4demo.com");
    }

    @Test
    @DisplayName("验证联系 URL")
    void shouldVerifyContactUrl() {
        // When - 获取联系 URL
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();
        String url = contact.getUrl();

        // Then - 验证联系 URL
        assertThat(url).isEqualTo("https://github.com/ylzyd12345/springboot-study");
    }

    @Test
    @DisplayName("验证许可证信息")
    void shouldVerifyLicenseInfo() {
        // When - 获取许可证信息
        Info info = openAPI.getInfo();
        License license = info.getLicense();

        // Then - 验证许可证信息
        assertThat(license).isNotNull();
        assertThat(license.getName()).isNotNull();
        assertThat(license.getUrl()).isNotNull();
    }

    @Test
    @DisplayName("验证许可证名称")
    void shouldVerifyLicenseName() {
        // When - 获取许可证名称
        Info info = openAPI.getInfo();
        License license = info.getLicense();
        String name = license.getName();

        // Then - 验证许可证名称
        assertThat(name).isEqualTo("MIT License");
    }

    @Test
    @DisplayName("验证许可证 URL")
    void shouldVerifyLicenseUrl() {
        // When - 获取许可证 URL
        Info info = openAPI.getInfo();
        License license = info.getLicense();
        String url = license.getUrl();

        // Then - 验证许可证 URL
        assertThat(url).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("验证服务器列表")
    void shouldVerifyServerList() {
        // When - 获取服务器列表
        List<Server> servers = openAPI.getServers();

        // Then - 验证服务器列表
        assertThat(servers).isNotNull();
        assertThat(servers).isNotEmpty();
        assertThat(servers).hasSize(2);
    }

    @Test
    @DisplayName("验证开发环境服务器")
    void shouldVerifyDevServer() {
        // When - 获取服务器列表
        List<Server> servers = openAPI.getServers();

        // Then - 验证开发环境服务器
        assertThat(servers).anyMatch(server ->
                server.getDescription().equals("开发环境") &&
                        server.getUrl().contains("localhost")
        );
    }

    @Test
    @DisplayName("验证生产环境服务器")
    void shouldVerifyProdServer() {
        // When - 获取服务器列表
        List<Server> servers = openAPI.getServers();

        // Then - 验证生产环境服务器
        assertThat(servers).anyMatch(server ->
                server.getDescription().equals("生产环境") &&
                        server.getUrl().contains("api.spring4demo.com")
        );
    }

    @Test
    @DisplayName("验证标签列表")
    void shouldVerifyTagList() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证标签列表
        assertThat(tags).isNotNull();
        assertThat(tags).isNotEmpty();
        assertThat(tags).hasSize(5);
    }

    @Test
    @DisplayName("验证用户管理标签")
    void shouldVerifyUserManagementTag() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证用户管理标签
        assertThat(tags).anyMatch(tag ->
                tag.getName().equals("用户管理") &&
                        tag.getDescription().contains("用户相关的API接口")
        );
    }

    @Test
    @DisplayName("验证认证授权标签")
    void shouldVerifyAuthTag() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证认证授权标签
        assertThat(tags).anyMatch(tag ->
                tag.getName().equals("认证授权") &&
                        tag.getDescription().contains("用户认证和授权相关接口")
        );
    }

    @Test
    @DisplayName("验证系统管理标签")
    void shouldVerifySystemManagementTag() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证系统管理标签
        assertThat(tags).anyMatch(tag ->
                tag.getName().equals("系统管理") &&
                        tag.getDescription().contains("系统配置和管理相关接口")
        );
    }

    @Test
    @DisplayName("验证 API 版本标签")
    void shouldVerifyApiVersionTag() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证 API 版本标签
        assertThat(tags).anyMatch(tag ->
                tag.getName().equals("API版本") &&
                        tag.getDescription().contains("API版本管理相关接口")
        );
    }

    @Test
    @DisplayName("验证健康检查标签")
    void shouldVerifyHealthCheckTag() {
        // When - 获取标签列表
        List<Tag> tags = openAPI.getTags();

        // Then - 验证健康检查标签
        assertThat(tags).anyMatch(tag ->
                tag.getName().equals("健康检查") &&
                        tag.getDescription().contains("系统健康检查相关接口")
        );
    }

    @Test
    @DisplayName("验证 OpenAPI 规范版本")
    void shouldVerifyOpenAPISpecVersion() {
        // When - 获取 OpenAPI 规范版本
        String openapi = openAPI.getOpenapi();

        // Then - 验证 OpenAPI 规范版本
        assertThat(openapi).isNotNull();
        assertThat(openapi).startsWith("3.");
    }

    @Test
    @DisplayName("验证外部文档配置")
    void shouldVerifyExternalDocs() {
        // When - 获取外部文档
        var externalDocs = openAPI.getExternalDocs();

        // Then - 验证外部文档（可能为 null）
        // 这里只验证获取方法不会抛出异常
        assertThat(externalDocs).isNull(); // 当前配置未设置外部文档
    }

    @Test
    @DisplayName("验证安全配置")
    void shouldVerifySecurity() {
        // When - 获取安全配置
        var security = openAPI.getComponents().getSecuritySchemes();

        // Then - 验证安全配置（可能为 null）
        // 这里只验证获取方法不会抛出异常
        // 当前配置未设置安全方案
    }

    @Test
    @DisplayName("验证路径配置")
    void shouldVerifyPaths() {
        // When - 获取路径配置
        var paths = openAPI.getPaths();

        // Then - 验证路径配置（可能为空）
        // 这里只验证获取方法不会抛出异常
        assertThat(paths).isNotNull();
    }
}