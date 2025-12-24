package com.kev1n.spring4demo.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.time.Duration;

@TestConfiguration
@Profile("test")
public class TestContainersConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Primary
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("spring4demo_test")
                .withUsername("test")
                .withPassword("test")
                .withStartupTimeout(Duration.ofMinutes(2))
                .waitingFor(Wait.forLogMessage("ready for connections", 1));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management-alpine"))
                .withAdminUsername("admin")
                .withAdminPassword("admin")
                .withStartupTimeout(Duration.ofMinutes(2))
                .waitingFor(Wait.forLogMessage("Server startup complete", 1));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
                .withStartupTimeout(Duration.ofMinutes(2))
                .waitingFor(Wait.forLogMessage(".*Kafka server started.*", 1));
    }

    @Bean
    @Primary
    public DataSource dataSource(MySQLContainer<?> mysqlContainer) {
        return DataSourceBuilder.create()
                .driverClassName(mysqlContainer.getDriverClassName())
                .url(mysqlContainer.getJdbcUrl())
                .username(mysqlContainer.getUsername())
                .password(mysqlContainer.getPassword())
                .build();
    }
}