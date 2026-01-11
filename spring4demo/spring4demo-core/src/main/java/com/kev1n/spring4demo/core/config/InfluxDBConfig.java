package com.kev1n.spring4demo.core.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxDB 时序数据库配置
 *
 * <p>配置 InfluxDB 客户端连接。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url:http://localhost:8086}")
    private String url;

    @Value("${influxdb.token:}")
    private String token;

    @Value("${influxdb.org:spring4demo}")
    private String org;

    @Value("${influxdb.bucket:spring4demo}")
    private String bucket;

    /**
     * 创建 InfluxDB Client Bean
     *
     * @return InfluxDB Client 实例
     */
    @Bean
    public InfluxDBClient influxDBClient() {
        log.info("初始化 InfluxDB Client: url={}, org={}, bucket={}", url, org, bucket);
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}