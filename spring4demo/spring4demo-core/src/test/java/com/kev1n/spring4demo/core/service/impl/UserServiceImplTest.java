package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserServiceImpl 单元测试
 *
 * <p>测试 UserServiceImpl 的所有业务逻辑方法</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void findByUsername_Success() {
        // Given - 准备测试数据
        when(userMapper.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When - 执行测试方法
        Optional<User> result = userService.findByUsername("testuser");

        // Then - 验证结果
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        // 验证 mock 对象被调用
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("根据用户名查询用户 - 用户不存在")
    void findByUsername_NotFound() {
        // Given
        when(userMapper.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("根据邮箱查询用户 - 成功")
    void findByEmail_Success() {
        // Given
        when(userMapper.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("检查用户名是否存在 - 存在")
    void existsByUsername_True() {
        // Given
        when(userMapper.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = userService.existsByUsername("testuser");

        // Then
        assertThat(result).isTrue();
        verify(userMapper, times(1)).existsByUsername("testuser");
    }

    @Test
    @DisplayName("检查用户名是否存在 - 不存在")
    void existsByUsername_False() {
        // Given
        when(userMapper.existsByUsername("nonexistent")).thenReturn(false);

        // When
        boolean result = userService.existsByUsername("nonexistent");

        // Then
        assertThat(result).isFalse();
        verify(userMapper, times(1)).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("检查邮箱是否存在 - 存在")
    void existsByEmail_True() {
        // Given
        when(userMapper.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertThat(result).isTrue();
        verify(userMapper, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("根据状态查询用户列表 - 成功")
    void findByStatus_Success() {
        // Given
        User user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setStatus(1);

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");
        user2.setStatus(1);

        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userMapper.findByStatus(1)).thenReturn(expectedUsers);

        // When
        List<User> result = userService.findByStatus(1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("status").containsOnly(1);
        verify(userMapper, times(1)).findByStatus(1);
    }

    @Test
    @DisplayName("根据状态查询用户列表 - 空列表")
    void findByStatus_EmptyList() {
        // Given
        when(userMapper.findByStatus(99)).thenReturn(List.of());

        // When
        List<User> result = userService.findByStatus(99);

        // Then
        assertThat(result).isEmpty();
        verify(userMapper, times(1)).findByStatus(99);
    }

    @Test
    @DisplayName("统计指定状态的用户数量 - 成功")
    void countByStatus_Success() {
        // Given
        when(userMapper.countByStatus(1)).thenReturn(10L);

        // When
        long result = userService.countByStatus(1);

        // Then
        assertThat(result).isEqualTo(10L);
        verify(userMapper, times(1)).countByStatus(1);
    }

    @Test
    @DisplayName("查询最近活跃用户 - 成功")
    void findRecentActiveUsers_Success() {
        // Given
        User activeUser1 = new User();
        activeUser1.setId("1");
        activeUser1.setUsername("active1");

        User activeUser2 = new User();
        activeUser2.setId("2");
        activeUser2.setUsername("active2");

        List<User> activeUsers = Arrays.asList(activeUser1, activeUser2);
        when(userMapper.findRecentActiveUsers()).thenReturn(activeUsers);

        // When
        List<User> result = userService.findRecentActiveUsers();

        // Then
        assertThat(result).hasSize(2);
        verify(userMapper, times(1)).findRecentActiveUsers();
    }
}