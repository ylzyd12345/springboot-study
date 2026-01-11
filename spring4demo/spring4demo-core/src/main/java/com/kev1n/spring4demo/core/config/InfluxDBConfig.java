package com.kev1n.spring4demo.core.config;

import com.influxdb.v3.client.InfluxDBClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxDB3 时序数据库配置
 *
 * <p>配置 InfluxDB3 客户端连接。</p>
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Slf4j
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url:http://localhost:8181}")
    private String url;

    @Value("${influxdb.token:}")
    private String token;

    @Value("${influxdb.database:spring4demo}")
    private String database;

    /**
     * 创建 InfluxDB3 Client Bean
     *
     * @return InfluxDB3 Client 实例
     */
    @Bean
    public InfluxDBClient influxDBClient() {
        log.info("初始化 InfluxDB3 Client: url={}, database={}", url, database);
        return InfluxDBClient.getInstance(url, token.toCharArray(), database);
    }
}