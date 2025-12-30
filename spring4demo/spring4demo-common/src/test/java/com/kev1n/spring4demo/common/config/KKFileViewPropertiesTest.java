package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KKFileView 文件预览配置属性测试
 *
 * @author kev1n
 * @since 1.0.0
 */
@DisplayName("KKFileView 文件预览配置属性测试")
class KKFileViewPropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        KKFileViewProperties properties = new KKFileViewProperties();

        // Then - 验证默认值
        assertThat(properties.getServerUrl()).isEqualTo("http://localhost:8012");
        assertThat(properties.getPreviewPath()).isEqualTo("/onlinePreview");
        assertThat(properties.getUseCache()).isFalse();
        assertThat(properties.getCacheExpireTime()).isEqualTo(3600);
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://localhost:8080");
        assertThat(properties.getForceUpdateCache()).isTrue();
    }

    @Test
    @DisplayName("配置绑定测试 - 正常配置")
    void testBindConfiguration() {
        // Given - 配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", "http://kkfileview.example.com");
        config.put("kkfileview.preview-path", "/custom-preview");
        config.put("kkfileview.use-cache", true);
        config.put("kkfileview.cache-expire-time", 7200);
        config.put("kkfileview.current-server-url", "http://app.example.com");
        config.put("kkfileview.force-update-cache", false);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证绑定结果
        assertThat(properties.getServerUrl()).isEqualTo("http://kkfileview.example.com");
        assertThat(properties.getPreviewPath()).isEqualTo("/custom-preview");
        assertThat(properties.getUseCache()).isTrue();
        assertThat(properties.getCacheExpireTime()).isEqualTo(7200);
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://app.example.com");
        assertThat(properties.getForceUpdateCache()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - 部分配置")
    void testBindPartialConfiguration() {
        // Given - 部分配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", "http://custom-kkfileview.example.com");
        config.put("kkfileview.use-cache", true);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证绑定结果（配置的值使用配置值，未配置的值使用默认值）
        assertThat(properties.getServerUrl()).isEqualTo("http://custom-kkfileview.example.com");
        assertThat(properties.getUseCache()).isTrue();
        assertThat(properties.getPreviewPath()).isEqualTo("/onlinePreview"); // 默认值
        assertThat(properties.getCacheExpireTime()).isEqualTo(3600); // 默认值
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://localhost:8080"); // 默认值
        assertThat(properties.getForceUpdateCache()).isTrue(); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 空配置")
    void testBindEmptyConfiguration() {
        // Given - 空配置数据
        Map<String, Object> config = new HashMap<>();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证所有值都使用默认值
        assertThat(properties.getServerUrl()).isEqualTo("http://localhost:8012");
        assertThat(properties.getPreviewPath()).isEqualTo("/onlinePreview");
        assertThat(properties.getUseCache()).isFalse();
        assertThat(properties.getCacheExpireTime()).isEqualTo(3600);
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://localhost:8080");
        assertThat(properties.getForceUpdateCache()).isTrue();
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", "http://test.example.com");
        config.put("kkfileview.preview-path", "/test-path");
        config.put("kkfileview.use-cache", "false"); // 字符串转 Boolean
        config.put("kkfileview.cache-expire-time", "1800"); // 字符串转 Integer
        config.put("kkfileview.current-server-url", "http://test-app.example.com");
        config.put("kkfileview.force-update-cache", "false"); // 字符串转 Boolean

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证类型转换正确
        assertThat(properties.getServerUrl()).isInstanceOf(String.class);
        assertThat(properties.getPreviewPath()).isInstanceOf(String.class);
        assertThat(properties.getUseCache()).isInstanceOf(Boolean.class);
        assertThat(properties.getCacheExpireTime()).isInstanceOf(Integer.class);
        assertThat(properties.getCurrentServerUrl()).isInstanceOf(String.class);
        assertThat(properties.getForceUpdateCache()).isInstanceOf(Boolean.class);

        assertThat(properties.getUseCache()).isFalse();
        assertThat(properties.getCacheExpireTime()).isEqualTo(1800);
        assertThat(properties.getForceUpdateCache()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - 预览路径格式")
    void testBindPreviewPathFormat() {
        // Given - 不同格式的预览路径
        Map<String, Object> config1 = new HashMap<>();
        config1.put("kkfileview.preview-path", "/preview");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("kkfileview.preview-path", "preview-no-slash");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);

        // When - 绑定配置
        KKFileViewProperties properties1 = binder1.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());
        KKFileViewProperties properties2 = binder2.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证路径格式
        assertThat(properties1.getPreviewPath()).isEqualTo("/preview");
        assertThat(properties2.getPreviewPath()).isEqualTo("preview-no-slash");
    }

    @Test
    @DisplayName("配置绑定测试 - 缓存配置")
    void testBindCacheConfiguration() {
        // Given - 缓存相关配置
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.use-cache", true);
        config.put("kkfileview.cache-expire-time", 86400); // 24小时
        config.put("kkfileview.force-update-cache", false);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证缓存配置
        assertThat(properties.getUseCache()).isTrue();
        assertThat(properties.getCacheExpireTime()).isEqualTo(86400);
        assertThat(properties.getForceUpdateCache()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - URL 格式")
    void testBindUrlFormat() {
        // Given - 不同格式的URL
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", "https://kkfileview.example.com:8080/preview");
        config.put("kkfileview.current-server-url", "https://app.example.com:9090/api");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证 URL 格式
        assertThat(properties.getServerUrl()).isEqualTo("https://kkfileview.example.com:8080/preview");
        assertThat(properties.getCurrentServerUrl()).isEqualTo("https://app.example.com:9090/api");
    }

    @Test
    @DisplayName("配置绑定测试 - 边界值")
    void testBindBoundaryValues() {
        // Given - 边界值配置
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.cache-expire-time", 0); // 最小值
        config.put("kkfileview.cache-expire-time", Integer.MAX_VALUE); // 最大值
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证边界值正确处理
        assertThat(properties.getCacheExpireTime()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", null);
        config.put("kkfileview.preview-path", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证 null 值处理（使用默认值）
        assertThat(properties.getServerUrl()).isEqualTo("http://localhost:8012"); // 默认值
        assertThat(properties.getPreviewPath()).isEqualTo("/onlinePreview"); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 特殊字符")
    void testBindSpecialCharacters() {
        // Given - 包含特殊字符的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("kkfileview.server-url", "http://test.example.com:8012/path?query=value#fragment");
        config.put("kkfileview.preview-path", "/preview-path_123");
        config.put("kkfileview.current-server-url", "http://app.example.com:8080/api_v1");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        KKFileViewProperties properties = binder.bind("kkfileview", Bindable.of(KKFileViewProperties.class))
                .orElse(new KKFileViewProperties());

        // Then - 验证特殊字符正确处理
        assertThat(properties.getServerUrl()).isEqualTo("http://test.example.com:8012/path?query=value#fragment");
        assertThat(properties.getPreviewPath()).isEqualTo("/preview-path_123");
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://app.example.com:8080/api_v1");
    }
}