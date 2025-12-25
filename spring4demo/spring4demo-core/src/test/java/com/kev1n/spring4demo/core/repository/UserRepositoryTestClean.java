package com.kev1n.spring4demo.core.repository;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.test.CoreTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户Repository单元测试 - 清理版本
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTestClean {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("应该能够保存用户")
    void shouldSaveUser() {
        // Given
        User user = CoreTestDataFactory.createTestUser();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该能够根据用户名查找用户")
    void shouldFindUserByUsername() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("应该能够检查用户名是否存在")
    void shouldCheckUsernameExists() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByUsername(user.getUsername());
        boolean notExists = userRepository.existsByUsername("nonexistent-username");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("应该能够根据邮箱查找用户")
    void shouldFindUserByEmail() {
        // Given
        User user = CoreTestDataFactory.createTestUser();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("应该能够统计用户总数")
    void shouldCountUsers() {
        // Given
        User[] users = CoreTestDataFactory.createTestUsers(3);
        for (User user : users) {
            entityManager.persistAndFlush(user);
        }

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }
}