package com.kev1n.spring4demo.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

/**
 * Mock测试基类
 * 提供Mock测试的基础配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public abstract class MockTestBase {

    /**
     * 初始化Mock
     */
    @BeforeEach
    void setUpMocks() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 重置所有Mock对象
     */
    protected void resetAllMocks(Object... mocks) {
        for (Object mock : mocks) {
            if (mock != null) {
                org.mockito.Mockito.reset(mock);
            }
        }
    }
}