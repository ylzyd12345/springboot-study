package com.junmo.platform.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代码生成器启动类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.UseUtilityClass","PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal"})
@SpringBootApplication
public class GeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
    }
}