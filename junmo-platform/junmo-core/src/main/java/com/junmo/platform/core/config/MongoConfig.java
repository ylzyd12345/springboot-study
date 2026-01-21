package com.junmo.platform.core.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB配置类
 *
 * 配置MongoClient、MongoTemplate，并启用MongoRepository扫描
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = "com.junmo.platform.core.repository.mongo")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/junmo-platform}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:junmo-platform}")
    private String databaseName;

    /**
     * 创建MongoClient
     *
     * @return MongoClient实例
     */
    @Override
    @Bean
    public MongoClient mongoClient() {
        log.info("初始化MongoClient，连接地址: {}", mongoUri);
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    /**
     * 创建MongoTemplate
     *
     * @return MongoTemplate实例
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        log.info("初始化MongoTemplate，数据库: {}", databaseName);
        return new MongoTemplate(mongoClient(), databaseName);
    }
}