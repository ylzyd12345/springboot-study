package com.kev1n.spring4demo.core;

import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 *
 * <p>提供通用的测试配置和生命周期管理</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseTest {

    /**
     * 每个测试方法执行前的初始化操作
     */
    @BeforeEach
    void setUp() {
        // 清除之前的登录状态
        StpUtil.logout();
        setupTest();
    }

    /**
     * 每个测试方法执行后的清理操作
     */
    @AfterEach
    void tearDown() {
        cleanupTest();
        // 清除登录状态
        StpUtil.logout();
    }

    /**
     * 测试初始化钩子方法，子类可以覆盖
     */
    protected void setupTest() {
        // 默认空实现，子类可以覆盖
    }

    /**
     * 测试清理钩子方法，子类可以覆盖
     */
    protected void cleanupTest() {
        // 默认空实现，子类可以覆盖
    }

    /**
     * 模拟用户登录
     *
     * @param userId 用户ID
     */
    protected void mockLogin(Long userId) {
        StpUtil.login(userId);
        StpUtil.getSession().set("loginId", userId);
    }

    /**
     * 模拟管理员登录
     */
    protected void mockAdminLogin() {
        mockLogin(1L);
        StpUtil.getSession().set("role", "ADMIN");
    }

    /**
     * 模拟普通用户登录
     */
    protected void mockUserLogin() {
        mockLogin(2L);
        StpUtil.getSession().set("role", "USER");
    }

    /**
     * 等待一段时间（用于异步测试）
     *
     * @param millis 毫秒数
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}