package com.kev1n.spring4demo.core.test;

import org.junit.jupiter.api.BeforeEach;

/**
 * Core模块测试基类
 * 提供基础的测试配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public abstract class CoreTestBase {

    /**
     * 测试前的准备工作
     */
    @BeforeEach
    void setUp() {
        // 可以在这里添加通用的测试准备逻辑
    }
}