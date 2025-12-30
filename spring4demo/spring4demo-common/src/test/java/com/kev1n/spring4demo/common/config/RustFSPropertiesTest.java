package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RustFS 文件存储配置属性测试
 *
 * @author kev1n
 * @since 1.0.0
 */
@DisplayName("RustFS 文件存储配置属性测试")
class RustFSPropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        RustFSProperties properties = new RustFSProperties();

        // Then - 验证默认值
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:9000");
        assertThat(properties.getAccessKey()).isEqualTo("admin");
        assertThat(properties.getSecretKey()).isEqualTo("admin123");
        assertThat(properties.getBucketName()).isEqualTo("spring4demo");
        assertThat(properties.getRegion()).isEqualTo("us-east-1");
        assertThat(properties.getPathStyleAccess()).isTrue();
        assertThat(properties.getMaxFileSize()).isEqualTo(10L);
        assertThat(properties.getMaxRequestSize()).isEqualTo(100L);
    }

    @Test
    @DisplayName("配置绑定测试 - 正常配置")
    void testBindConfiguration() {
        // Given - 配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.endpoint", "http://rustfs.example.com");
        config.put("rustfs.access-key", "test-access-key");
        config.put("rustfs.secret-key", "test-secret-key");
        config.put("rustfs.bucket-name", "test-bucket");
        config.put("rustfs.region", "us-west-2");
        config.put("rustfs.path-style-access", false);
        config.put("rustfs.max-file-size", 50L);
        config.put("rustfs.max-request-size", 200L);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证绑定结果
        assertThat(properties.getEndpoint()).isEqualTo("http://rustfs.example.com");
        assertThat(properties.getAccessKey()).isEqualTo("test-access-key");
        assertThat(properties.getSecretKey()).isEqualTo("test-secret-key");
        assertThat(properties.getBucketName()).isEqualTo("test-bucket");
        assertThat(properties.getRegion()).isEqualTo("us-west-2");
        assertThat(properties.getPathStyleAccess()).isFalse();
        assertThat(properties.getMaxFileSize()).isEqualTo(50L);
        assertThat(properties.getMaxRequestSize()).isEqualTo(200L);
    }

    @Test
    @DisplayName("配置绑定测试 - 部分配置")
    void testBindPartialConfiguration() {
        // Given - 部分配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.endpoint", "http://custom-rustfs.example.com");
        config.put("rustfs.access-key", "custom-access-key");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证绑定结果（配置的值使用配置值，未配置的值使用默认值）
        assertThat(properties.getEndpoint()).isEqualTo("http://custom-rustfs.example.com");
        assertThat(properties.getAccessKey()).isEqualTo("custom-access-key");
        assertThat(properties.getSecretKey()).isEqualTo("admin123"); // 默认值
        assertThat(properties.getBucketName()).isEqualTo("spring4demo"); // 默认值
        assertThat(properties.getRegion()).isEqualTo("us-east-1"); // 默认值
        assertThat(properties.getPathStyleAccess()).isTrue(); // 默认值
        assertThat(properties.getMaxFileSize()).isEqualTo(10L); // 默认值
        assertThat(properties.getMaxRequestSize()).isEqualTo(100L); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 空配置")
    void testBindEmptyConfiguration() {
        // Given - 空配置数据
        Map<String, Object> config = new HashMap<>();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证所有值都使用默认值
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:9000");
        assertThat(properties.getAccessKey()).isEqualTo("admin");
        assertThat(properties.getSecretKey()).isEqualTo("admin123");
        assertThat(properties.getBucketName()).isEqualTo("spring4demo");
        assertThat(properties.getRegion()).isEqualTo("us-east-1");
        assertThat(properties.getPathStyleAccess()).isTrue();
        assertThat(properties.getMaxFileSize()).isEqualTo(10L);
        assertThat(properties.getMaxRequestSize()).isEqualTo(100L);
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.endpoint", "http://test.example.com");
        config.put("rustfs.access-key", "string-access-key");
        config.put("rustfs.secret-key", "string-secret-key");
        config.put("rustfs.bucket-name", "string-bucket");
        config.put("rustfs.region", "string-region");
        config.put("rustfs.path-style-access", "true"); // 字符串转 Boolean
        config.put("rustfs.max-file-size", "20"); // 字符串转 Long
        config.put("rustfs.max-request-size", "150"); // 字符串转 Long

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证类型转换正确
        assertThat(properties.getEndpoint()).isInstanceOf(String.class);
        assertThat(properties.getAccessKey()).isInstanceOf(String.class);
        assertThat(properties.getSecretKey()).isInstanceOf(String.class);
        assertThat(properties.getBucketName()).isInstanceOf(String.class);
        assertThat(properties.getRegion()).isInstanceOf(String.class);
        assertThat(properties.getPathStyleAccess()).isInstanceOf(Boolean.class);
        assertThat(properties.getMaxFileSize()).isInstanceOf(Long.class);
        assertThat(properties.getMaxRequestSize()).isInstanceOf(Long.class);

        assertThat(properties.getPathStyleAccess()).isTrue();
        assertThat(properties.getMaxFileSize()).isEqualTo(20L);
        assertThat(properties.getMaxRequestSize()).isEqualTo(150L);
    }

    @Test
    @DisplayName("配置绑定测试 - 特殊字符")
    void testBindSpecialCharacters() {
        // Given - 包含特殊字符的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.endpoint", "http://test.example.com:9000/path?query=value#fragment");
        config.put("rustfs.access-key", "access-key-with-special-chars-!@#$%");
        config.put("rustfs.secret-key", "secret-key-with-special-chars-!@#$%");
        config.put("rustfs.bucket-name", "bucket-name_123");
        config.put("rustfs.region", "region-name_123");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证特殊字符正确处理
        assertThat(properties.getEndpoint()).isEqualTo("http://test.example.com:9000/path?query=value#fragment");
        assertThat(properties.getAccessKey()).isEqualTo("access-key-with-special-chars-!@#$%");
        assertThat(properties.getSecretKey()).isEqualTo("secret-key-with-special-chars-!@#$%");
        assertThat(properties.getBucketName()).isEqualTo("bucket-name_123");
        assertThat(properties.getRegion()).isEqualTo("region-name_123");
    }

    @Test
    @DisplayName("配置绑定测试 - 边界值")
    void testBindBoundaryValues() {
        // Given - 边界值配置
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.max-file-size", 0L); // 最小值
        config.put("rustfs.max-request-size", Long.MAX_VALUE); // 最大值
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证边界值正确处理
        assertThat(properties.getMaxFileSize()).isEqualTo(0L);
        assertThat(properties.getMaxRequestSize()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("rustfs.endpoint", null);
        config.put("rustfs.access-key", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        RustFSProperties properties = binder.bind("rustfs", Bindable.of(RustFSProperties.class))
                .orElse(new RustFSProperties());

        // Then - 验证 null 值处理（使用默认值）
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:9000"); // 默认值
        assertThat(properties.getAccessKey()).isEqualTo("admin"); // 默认值
    }
}