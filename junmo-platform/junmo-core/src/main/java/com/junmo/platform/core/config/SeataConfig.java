package com.junmo.platform.core.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Seata分布式事务配置
 *
 * 配置Seata全局事务扫描器和数据源代理
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@org.springframework.context.annotation.Configuration
@Slf4j
public class SeataConfig {

    /**
     * 应用名称
     */
    @Value("${spring.application.name:junmo-platform}")
    private String applicationName;

    /**
     * Seata事务组名称
     */
    @Value("${seata.tx-service-group:my_test_tx_group}")
    private String txServiceGroup;

    /**
     * 配置全局事务扫描器
     *
     * GlobalTransactionScanner会扫描@GlobalTransactional注解，
     * 并为标注了该注解的方法创建分布式事务代理
     *
     * @return 全局事务扫描器
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        log.info("初始化Seata全局事务扫描器: applicationName={}, txServiceGroup={}",
                applicationName, txServiceGroup);
        return new GlobalTransactionScanner(applicationName, txServiceGroup);
    }
}