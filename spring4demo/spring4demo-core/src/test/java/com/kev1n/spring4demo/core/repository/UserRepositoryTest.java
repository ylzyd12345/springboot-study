package com.kev1n.spring4demo.core.repository;

import com.kev1n.spring4demo.common.test.BaseTestContainer;
import com.kev1n.spring4demo.common.test.TestDataFactory;
import com.kev1n.spring4demo.core.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户Repository单元测试
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest extends BaseTestContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("应该能够保存用户")
    void shouldSaveUser() {
        // Given
        User user = TestDataFactory.createTestUser();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该能够根据ID查找用户")
    void shouldFindUserById() {
        // Given
        User user = TestDataFactory.createTestUser();
        User savedUser = entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("应该能够根据用户名查找用户")
    void shouldFindUserByUsername() {
        // Given
        User user = TestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该能够根据邮箱查找用户")
    void shouldFindUserByEmail() {
        // Given
        User user = TestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("应该能够检查用户名是否存在")
    void shouldCheckUsernameExists() {
        // Given
        User user = TestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByUsername(user.getUsername());
        boolean notExists = userRepository.existsByUsername("nonexistent-username");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("应该能够检查邮箱是否存在")
    void shouldCheckEmailExists() {
        // Given
        User user = TestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail(user.getEmail());
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("应该能够分页查询用户")
    void shouldFindUsersByPage() {
        // Given
        User[] users = TestDataFactory.createTestUsers(15);
        for (User user : users) {
            entityManager.persistAndFlush(user);
        }
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> userPage = userRepository.findAll(pageable);

        // Then
        assertThat(userPage.getContent()).hasSize(10);
        assertThat(userPage.getTotalElements()).isEqualTo(15);
        assertThat(userPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("应该能够根据状态查询用户")
    void shouldFindUsersByStatus() {
        // Given
        User activeUser = TestDataFactory.createTestUser();
        activeUser.setStatus(1);
        entityManager.persistAndFlush(activeUser);

        User inactiveUser = TestDataFactory.createTestUser();
        inactiveUser.setStatus(0);
        entityManager.persistAndFlush(inactiveUser);

        // When
        List<User> activeUsers = userRepository.findByStatus(1);
        List<User> inactiveUsers = userRepository.findByStatus(0);

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo(activeUser.getUsername());
        assertThat(inactiveUsers).hasSize(1);
        assertThat(inactiveUsers.get(0).getUsername()).isEqualTo(inactiveUser.getUsername());
    }

    @Test
    @DisplayName("应该能够软删除用户")
    void shouldSoftDeleteUser() {
        // Given
        User user = TestDataFactory.createTestUser();
        User savedUser = entityManager.persistAndFlush(user);
        entityManager.clear();

        // When
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("应该能够统计用户总数")
    void shouldCountUsers() {
        // Given
        User[] users = TestDataFactory.createTestUsers(5);
        for (User user : users) {
            entityManager.persistAndFlush(user);
        }

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(5);
    }
}