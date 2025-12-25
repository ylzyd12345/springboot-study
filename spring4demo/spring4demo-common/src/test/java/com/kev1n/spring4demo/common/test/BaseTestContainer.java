package com.kev1n.spring4demo.common.test;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * 测试容器基类
 * 提供集成测试所需的基础设施
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@Testcontainers
public abstract class BaseTestContainer {

    // MySQL容器
    protected static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("spring4demo_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    // Redis容器
    protected static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    // RabbitMQ容器
    protected static final RabbitMQContainer rabbitmqContainer = new RabbitMQContainer("rabbitmq:3.12-management-alpine")
            .withReuse(true);

    // Kafka容器
    protected static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);

    /**
     * 启动所有测试容器
     */
    @BeforeAll
    static void setUpContainers() {
        mysqlContainer.start();
        redisContainer.start();
        rabbitmqContainer.start();
        kafkaContainer.start();
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

        // Redis配置
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());

        // RabbitMQ配置
        registry.add("spring.rabbitmq.host", rabbitmqContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitmqContainer.getMappedPort(5672).toString());
        registry.add("spring.rabbitmq.username", rabbitmqContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmqContainer::getAdminPassword);

        // Kafka配置
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        // JPA配置
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }
}