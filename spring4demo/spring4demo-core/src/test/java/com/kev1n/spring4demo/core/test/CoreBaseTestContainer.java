package com.kev1n.spring4demo.core.test;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Core模块测试容器基类
 * 提供集成测试所需的基础设施
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@Testcontainers
public abstract class CoreBaseTestContainer {

    // MySQL容器
    protected static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("spring4demo_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    /**
     * 启动测试容器
     */
    @BeforeAll
    static void setUpContainers() {
        mysqlContainer.start();
    }

    /**
     * 动态配置测试环境属性
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL配置
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);

        // JPA配置
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }
}