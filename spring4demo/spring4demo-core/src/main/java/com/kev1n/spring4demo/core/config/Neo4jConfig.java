package com.kev1n.spring4demo.core.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Neo4j 图数据库配置
 *
 * <p>配置 Neo4j 数据库连接和 Spring Data Neo4j 仓库扫描。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.kev1n.spring4demo.core.repository.neo4j")
@EnableTransactionManagement
public class Neo4jConfig extends AbstractNeo4jConfig {

    @Value("${spring.neo4j.uri:bolt://localhost:7687}")
    private String uri;

    @Value("${spring.neo4j.authentication.username:neo4j}")
    private String username;

    @Value("${spring.neo4j.authentication.password:password}")
    private String password;

    /**
     * 创建 Neo4j Driver Bean
     *
     * @return Neo4j Driver 实例
     */
    @Bean
    @Override
    public Driver driver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
}