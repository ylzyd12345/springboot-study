package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KKFileView 配置属性测试
 * 
 * 测试要点：
 * 1. 验证配置属性绑定
 * 2. 验证默认值是否正确
 * 3. 验证配置值是否正确注入
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-30
 */
@SpringBootTest
@EnableConfigurationProperties(KKFileViewProperties.class)
@TestPropertySource(properties = {
    "kkfileview.server-url=http://custom-kkfileview:8012",
    "kkfileview.preview-path=/custom-preview",
    "kkfileview.use-cache=true",
    "kkfileview.cache-expire-time=7200",
    "kkfileview.current-server-url=http://custom-server:8080",
    "kkfileview.force-update-cache=false"
})
@DisplayName("KKFileView 配置属性测试")
class KKFileViewPropertiesTest {

    @Autowired
    private KKFileViewProperties properties;

    @Test
    @DisplayName("应该正确注入KKFileViewProperties Bean")
    void shouldInjectKKFileViewProperties() {
        assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该正确配置serverUrl")
    void shouldConfigureServerUrl() {
        assertThat(properties.getServerUrl()).isEqualTo("http://custom-kkfileview:8012");
    }

    @Test
    @DisplayName("应该正确配置previewPath")
    void shouldConfigurePreviewPath() {
        assertThat(properties.getPreviewPath()).isEqualTo("/custom-preview");
    }

    @Test
    @DisplayName("应该正确配置useCache")
    void shouldConfigureUseCache() {
        assertThat(properties.getUseCache()).isTrue();
    }

    @Test
    @DisplayName("应该正确配置cacheExpireTime")
    void shouldConfigureCacheExpireTime() {
        assertThat(properties.getCacheExpireTime()).isEqualTo(7200);
    }

    @Test
    @DisplayName("应该正确配置currentServerUrl")
    void shouldConfigureCurrentServerUrl() {
        assertThat(properties.getCurrentServerUrl()).isEqualTo("http://custom-server:8080");
    }

    @Test
    @DisplayName("应该正确配置forceUpdateCache")
    void shouldConfigureForceUpdateCache() {
        assertThat(properties.getForceUpdateCache()).isFalse();
    }

    @DisplayName("应该使用默认值当配置未设置")
    @EnableConfigurationProperties(KKFileViewProperties.class)
    class DefaultValuesTest {

        @Autowired
        private KKFileViewProperties properties;

        @Test
        @DisplayName("serverUrl应该使用默认值")
        void serverUrlShouldUseDefaultValue() {
            assertThat(properties.getServerUrl()).isEqualTo("http://localhost:8012");
        }

        @Test
        @DisplayName("previewPath应该使用默认值")
        void previewPathShouldUseDefaultValue() {
            assertThat(properties.getPreviewPath()).isEqualTo("/onlinePreview");
        }

        @Test
        @DisplayName("useCache应该使用默认值")
        void useCacheShouldUseDefaultValue() {
            assertThat(properties.getUseCache()).isFalse();
        }

        @Test
        @DisplayName("cacheExpireTime应该使用默认值")
        void cacheExpireTimeShouldUseDefaultValue() {
            assertThat(properties.getCacheExpireTime()).isEqualTo(3600);
        }

        @Test
        @DisplayName("currentServerUrl应该使用默认值")
        void currentServerUrlShouldUseDefaultValue() {
            assertThat(properties.getCurrentServerUrl()).isEqualTo("http://localhost:8080");
        }

        @Test
        @DisplayName("forceUpdateCache应该使用默认值")
        void forceUpdateCacheShouldUseDefaultValue() {
            assertThat(properties.getForceUpdateCache()).isTrue();
        }
    }
}