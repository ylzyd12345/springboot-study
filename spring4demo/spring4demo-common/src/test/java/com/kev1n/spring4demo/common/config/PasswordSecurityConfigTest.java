package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 密码安全配置测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest(classes = PasswordSecurityConfig.class)
@DisplayName("密码安全配置测试")
class PasswordSecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("验证 PasswordEncoder Bean 创建")
    void testPasswordEncoderBeanCreated() {
        // Then - 验证 Bean 创建成功
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    @DisplayName("验证 BCrypt 密码编码")
    void testBCryptPasswordEncoder() {
        // Given - 原始密码
        String rawPassword = "admin123";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密结果
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encodedPassword).startsWith("$2a$");
    }

    @Test
    @DisplayName("验证 BCrypt 密码匹配")
    void testBCryptPasswordEncoderMatches() {
        // Given - 原始密码和加密后的密码
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // When - 验证密码匹配
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // Then - 验证密码匹配成功
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("验证 BCrypt 密码不匹配")
    void testBCryptPasswordEncoderNotMatches() {
        // Given - 原始密码和不同的加密密码
        String rawPassword = "admin123";
        String wrongPassword = "wrong-password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // When - 验证密码不匹配
        boolean matches = passwordEncoder.matches(wrongPassword, encodedPassword);

        // Then - 验证密码不匹配
        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("验证 BCrypt 每次加密结果不同")
    void testBCryptPasswordEncoderDifferentResults() {
        // Given - 原始密码
        String rawPassword = "admin123";

        // When - 多次加密同一密码
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);
        String encodedPassword3 = passwordEncoder.encode(rawPassword);

        // Then - 验证每次加密结果不同（由于盐值随机）
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
        assertThat(encodedPassword2).isNotEqualTo(encodedPassword3);
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword3);
    }

    @Test
    @DisplayName("验证 BCrypt 密码长度")
    void testBCryptPasswordEncoderLength() {
        // Given - 原始密码
        String rawPassword = "admin123";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密后密码长度固定（60字符）
        assertThat(encodedPassword).hasSize(60);
    }

    @Test
    @DisplayName("验证 BCrypt 空密码处理")
    void testBCryptPasswordEncoderEmptyPassword() {
        // Given - 空密码
        String rawPassword = "";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密结果
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).startsWith("$2a$");
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("验证 BCrypt 特殊字符密码")
    void testBCryptPasswordEncoderSpecialCharacters() {
        // Given - 包含特殊字符的密码
        String rawPassword = "p@ssw0rd!#$%^&*()";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密结果
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).startsWith("$2a$");
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("验证 BCrypt 长密码处理")
    void testBCryptPasswordEncoderLongPassword() {
        // Given - 长密码
        String rawPassword = "a".repeat(100);

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密结果
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).startsWith("$2a$");
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("验证 BCrypt 中文字符密码")
    void testBCryptPasswordEncoderChineseCharacters() {
        // Given - 包含中文字符的密码
        String rawPassword = "密码123";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密结果
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).startsWith("$2a$");
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("验证 BCrypt 密码编码器类型")
    void testBCryptPasswordEncoderType() {
        // Then - 验证密码编码器类型
        assertThat(passwordEncoder).isInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("验证 BCrypt 密码强度")
    void testBCryptPasswordEncoderStrength() {
        // Given - 原始密码
        String rawPassword = "admin123";

        // When - 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - 验证加密后的密码强度（BCrypt 默认强度为 10）
        // BCrypt 加密后的密码格式：$2a$[cost]$[22字符salt]$[31字符hash]
        // cost 值表示加密强度，默认为 10
        // salt 和 hash 只包含特定字符集：./A-Za-z0-9
        assertThat(encodedPassword).matches("^\\$2a\\$10\\$[./A-Za-z0-9]{53}$");
    }
}
