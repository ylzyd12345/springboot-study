package com.kev1n.spring4demo.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * 集成测试基类
 * 提供集成测试的基础配置和工具方法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase extends BaseTestContainer {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    /**
     * 设置MockMvc
     */
    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    /**
     * 清理数据库
     */
    @BeforeEach
    void cleanDatabase() {
        // 可以在这里添加清理逻辑
        // 例如：清理测试数据
    }
}