package com.junmo.platform.admin.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Junmo Platform 管理后台应用启动类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.junmo.platform")
public class JunmoAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(JunmoAdminApplication.class, args);
    }
}