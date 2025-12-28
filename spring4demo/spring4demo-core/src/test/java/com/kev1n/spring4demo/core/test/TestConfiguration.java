package com.kev1n.spring4demo.core.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Core模块测试配置类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@MapperScan("com.kev1n.spring4demo.core.mapper")
public class TestConfiguration {
}