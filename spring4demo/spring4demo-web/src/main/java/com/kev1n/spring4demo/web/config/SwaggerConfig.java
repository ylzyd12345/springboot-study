package com.kev1n.spring4demo.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger API文档配置
 * 配置OpenAPI 3.0规范和Knife4j增强功能
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .tags(tagList());
    }

    /**
     * API文档基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title("Spring4demo API")
                .description("Spring Boot 3.2.10 企业级生态环境集成项目API文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Spring4demo Team")
                        .email("support@spring4demo.com")
                        .url("https://github.com/ylzyd12345/springboot-study"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 服务器列表
     */
    private List<Server> serverList() {
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("开发环境");
        
        Server prodServer = new Server()
                .url("https://api.spring4demo.com")
                .description("生产环境");
        
        return List.of(devServer, prodServer);
    }

    /**
     * API标签列表
     */
    private List<Tag> tagList() {
        return List.of(
                new Tag()
                        .name("用户管理")
                        .description("用户相关的API接口"),
                new Tag()
                        .name("认证授权")
                        .description("用户认证和授权相关接口"),
                new Tag()
                        .name("系统管理")
                        .description("系统配置和管理相关接口"),
                new Tag()
                        .name("API版本")
                        .description("API版本管理相关接口"),
                new Tag()
                        .name("健康检查")
                        .description("系统健康检查相关接口")
        );
    }
}