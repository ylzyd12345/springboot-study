package com.kev1n.spring4demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring4demo 应用启动类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.kev1n.spring4demo")
public class Spring4demoApplication {
    public static void main(String[] args) {
        SpringApplication.run(Spring4demoApplication.class, args);
    }
}