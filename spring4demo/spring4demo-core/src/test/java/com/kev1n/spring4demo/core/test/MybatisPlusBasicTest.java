package com.kev1n.spring4demo.core.test;

import com.kev1n.spring4demo.core.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatis-Plus基础测试
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class MybatisPlusBasicTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldAutowiredUserMapper() {
        // Basic injection test
        assertThat(userMapper).isNotNull();
    }
}