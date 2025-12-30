package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.core.BaseTest;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserService 集成测试
 *
 * <p>测试 MyBatis-Plus Service 的 CRUD 功能</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("用户服务集成测试")
class UserServiceIT extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("测试 MyBatis-Plus Service CRUD 功能")
    void testMyBatisPlusServiceCRUD() {
        // Given - 创建测试用户
        User user = new User();
        user.setUsername("integration_test_user");
        user.setEmail("integration@test.com");
        user.setRealName("集成测试用户");
        user.setPhone("13800138000");
        user.setStatus(1);

        // When - 保存用户
        boolean saveResult = userService.save(user);
        assertThat(saveResult).isTrue();
        assertThat(user.getId()).isNotNull();

        // When - 查询用户
        User savedUser = userService.getById(user.getId());
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("integration_test_user");

        // When - 更新用户
        savedUser.setRealName("更新后的用户");
        boolean updateResult = userService.updateById(savedUser);
        assertThat(updateResult).isTrue();

        User updatedUser = userService.getById(user.getId());
        assertThat(updatedUser.getRealName()).isEqualTo("更新后的用户");

        // When - 删除用户
        boolean removeResult = userService.removeById(user.getId());
        assertThat(removeResult).isTrue();

        // Then - 验证用户已被删除
        User deletedUser = userService.getById(user.getId());
        assertThat(deletedUser).isNull();
    }
}