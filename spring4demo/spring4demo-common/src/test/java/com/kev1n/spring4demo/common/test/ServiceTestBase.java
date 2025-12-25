package com.kev1n.spring4demo.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service层测试基类
 * 提供Service层测试的基础配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class ServiceTestBase extends BaseTestContainer {

    /**
     * 测试前置处理
     */
    @BeforeEach
    void setUp() {
        // 子类可以重写此方法进行特定的测试前置处理
    }

    /**
     * 清理测试数据
     */
    @BeforeEach
    void cleanTestData() {
        // 子类可以重写此方法进行特定的测试数据清理
    }
}