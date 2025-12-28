package com.kev1n.spring4demo.core.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.test.CoreTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户Mapper单元测试
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("应该能够保存用户")
    void shouldSaveUser() {
        // Given
        User user = CoreTestDataFactory.createTestUser();

        // When
        int result = userMapper.insert(user);

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(user.getId()).isNotNull();
    }

    @Test
    @DisplayName("应该根据ID查找用户")
    void shouldFindUserById() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        User foundUser = userMapper.selectById(user.getId());

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该根据用户名查找用户")
    void shouldFindUserByUsername() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        Optional<User> foundUser = userMapper.findByUsername(user.getUsername());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("应该根据邮箱查找用户")
    void shouldFindUserByEmail() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        Optional<User> foundUser = userMapper.findByEmail(user.getEmail());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该检查用户名是否存在")
    void shouldCheckIfUsernameExists() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        boolean exists = userMapper.existsByUsername(user.getUsername());
        boolean notExists = userMapper.existsByUsername("nonexistent-username");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("应该检查邮箱是否存在")
    void shouldCheckIfEmailExists() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        boolean exists = userMapper.existsByEmail(user.getEmail());
        boolean notExists = userMapper.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("应该分页查询用户")
    void shouldFindUsersWithPagination() {
        // Given
        for (int i = 0; i < 5; i++) {
            User user = CoreTestDataFactory.createTestUser();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            userMapper.insert(user);
        }

        Page<User> page = new Page<>(1, 2);

        // When
        IPage<User> userPage = userMapper.selectPage(page, null);

        // Then
        assertThat(userPage.getRecords()).hasSize(2);
        assertThat(userPage.getTotal()).isEqualTo(5);
        assertThat(userPage.getCurrent()).isEqualTo(1);
    }

    @Test
    @DisplayName("应该根据状态查找用户")
    void shouldFindUsersByStatus() {
        // Given
        User activeUser = CoreTestDataFactory.createTestUser();
        activeUser.setStatus(1);
        userMapper.insert(activeUser);

        User inactiveUser = CoreTestDataFactory.createTestUser();
        inactiveUser.setUsername("inactive-user");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setStatus(0);
        userMapper.insert(inactiveUser);

        // When
        List<User> activeUsers = userMapper.findByStatus(1);
        List<User> inactiveUsers = userMapper.findByStatus(0);

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getStatus()).isEqualTo(1);
        assertThat(inactiveUsers).hasSize(1);
        assertThat(inactiveUsers.get(0).getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("应该根据用户名模糊查询用户")
    void shouldFindUsersByUsernameContaining() {
        // Given
        User user1 = CoreTestDataFactory.createTestUser();
        user1.setUsername("testuser1");
        userMapper.insert(user1);

        User user2 = CoreTestDataFactory.createTestUser();
        user2.setUsername("testuser2");
        user2.setEmail("testuser2@example.com");
        userMapper.insert(user2);

        Page<User> page = new Page<>(1, 10);

        // When
        IPage<User> userPage = userMapper.findByUsernameContainingIgnoreCase("test", page);

        // Then
        assertThat(userPage.getRecords()).hasSize(2);
    }

    @Test
    @DisplayName("应该能够删除用户")
    void shouldDeleteUser() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        userMapper.insert(user);

        // When
        int result = userMapper.deleteById(user.getId());

        // Then
        assertThat(result).isEqualTo(1);
        User deletedUser = userMapper.selectById(user.getId());
        assertThat(deletedUser).isNull();
    }

    @Test
    @DisplayName("应该统计用户总数")
    void shouldCountUsers() {
        // Given
        for (int i = 0; i < 3; i++) {
            User user = CoreTestDataFactory.createTestUser();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            userMapper.insert(user);
        }

        // When
        long count = userMapper.selectCount(null);

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("应该根据状态统计用户数量")
    void shouldCountUsersByStatus() {
        // Given
        for (int i = 0; i < 2; i++) {
            User user = CoreTestDataFactory.createTestUser();
            user.setUsername("active" + i);
            user.setEmail("active" + i + "@example.com");
            user.setStatus(1);
            userMapper.insert(user);
        }

        User inactiveUser = CoreTestDataFactory.createTestUser();
        inactiveUser.setUsername("inactive");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setStatus(0);
        userMapper.insert(inactiveUser);

        // When
        long activeCount = userMapper.countByStatus(1);
        long inactiveCount = userMapper.countByStatus(0);

        // Then
        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }
}