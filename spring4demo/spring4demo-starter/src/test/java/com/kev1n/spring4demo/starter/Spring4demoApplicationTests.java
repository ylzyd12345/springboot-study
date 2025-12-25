package com.kev1n.spring4demo.starter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 应用启动测试
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class Spring4demoApplicationTests  {

    @Test
    @DisplayName("应用上下文应该能够正常加载")
    void contextLoads() {
        // 如果测试通过，说明Spring上下文能够正常加载
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("应用应该能够正常启动")
    void applicationShouldStart() {
        // 基础启动测试
        assertThat(true).isTrue();
    }
}