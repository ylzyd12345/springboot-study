package com.kev1n.spring4demo.core.test;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Core模块测试配置类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EntityScan(basePackages = "com.kev1n.spring4demo.core.entity")
@EnableJpaRepositories(basePackages = "com.kev1n.spring4demo.core.repository")
public class TestConfiguration {
}