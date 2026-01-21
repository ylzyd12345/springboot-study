package com.junmo.platform.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Junmo Platform 应用启动类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.junmo.platform")
public class JunmoPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(JunmoPlatformApplication.class, args);
    }
}