package com.kev1n.spring4demo.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API版本控制配置
 * 使用Spring Boot 4（Spring Framework 7）的原生API版本控制特性
 * 
 * Spring Boot 4 API版本控制特性：
 * - 在映射注解上使用version属性（如@GetMapping(version = "1.1")）
 * - 支持语义版本和基线（如"1.2+"表示1.2及所有兼容的新版本）
 * - 内置版本解析策略（header、query param、path variable等）
 * - 支持弃用头（Deprecation、Sunset）引导客户端升级
 *
 * 使用示例：
 * @GetMapping(path = "/book/{id}", version = "1.1")
 * public ResponseEntity<Book> getBookV11(@PathVariable int id) { ... }
 *
 * @GetMapping(path = "/book/{id}", version = "1.2+")
 * public ResponseEntity<BookV2> getBookV12Plus(@PathVariable int id) { ... }
 *
 * 客户端请求示例：
 * curl -H "API-Version: 1.1" http://localhost:8080/book/1
 * curl -H "API-Version: 1.2" http://localhost:8080/book/1
 *
 * 注意：
 * Spring Framework 7的API版本控制是框架级别的功能，通常不需要额外的配置。
 * 只需要在Controller的映射方法上使用version属性即可。
 * 版本解析策略默认从请求头"API-Version"读取版本信息。
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    // Spring Boot 4的API版本控制是开箱即用的
    // 只需要在Controller方法上使用version属性即可
    // 不需要额外的配置
}