package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RustFS 配置属性测试
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
@EnableConfigurationProperties(RustFSProperties.class)
@TestPropertySource(properties = {
    "rustfs.endpoint=http://custom-endpoint:9000",
    "rustfs.access-key=custom-access-key",
    "rustfs.secret-key=custom-secret-key",
    "rustfs.bucket-name=custom-bucket",
    "rustfs.region=us-west-2",
    "rustfs.path-style-access=false",
    "rustfs.max-file-size=20",
    "rustfs.max-request-size=200"
})
@DisplayName("RustFS 配置属性测试")
class RustFSPropertiesTest {

    @Autowired
    private RustFSProperties properties;

    @Test
    @DisplayName("应该正确注入RustFSProperties Bean")
    void shouldInjectRustFSProperties() {
        assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该正确配置endpoint")
    void shouldConfigureEndpoint() {
        assertThat(properties.getEndpoint()).isEqualTo("http://custom-endpoint:9000");
    }

    @Test
    @DisplayName("应该正确配置accessKey")
    void shouldConfigureAccessKey() {
        assertThat(properties.getAccessKey()).isEqualTo("custom-access-key");
    }

    @Test
    @DisplayName("应该正确配置secretKey")
    void shouldConfigureSecretKey() {
        assertThat(properties.getSecretKey()).isEqualTo("custom-secret-key");
    }

    @Test
    @DisplayName("应该正确配置bucketName")
    void shouldConfigureBucketName() {
        assertThat(properties.getBucketName()).isEqualTo("custom-bucket");
    }

    @Test
    @DisplayName("应该正确配置region")
    void shouldConfigureRegion() {
        assertThat(properties.getRegion()).isEqualTo("us-west-2");
    }

    @Test
    @DisplayName("应该正确配置pathStyleAccess")
    void shouldConfigurePathStyleAccess() {
        assertThat(properties.getPathStyleAccess()).isFalse();
    }

    @Test
    @DisplayName("应该正确配置maxFileSize")
    void shouldConfigureMaxFileSize() {
        assertThat(properties.getMaxFileSize()).isEqualTo(20L);
    }

    @Test
    @DisplayName("应该正确配置maxRequestSize")
    void shouldConfigureMaxRequestSize() {
        assertThat(properties.getMaxRequestSize()).isEqualTo(200L);
    }

    @DisplayName("应该使用默认值当配置未设置")
    @EnableConfigurationProperties(RustFSProperties.class)
    class DefaultValuesTest {

        @Autowired
        private RustFSProperties properties;

        @Test
        @DisplayName("endpoint应该使用默认值")
        void endpointShouldUseDefaultValue() {
            assertThat(properties.getEndpoint()).isEqualTo("http://localhost:9000");
        }

        @Test
        @DisplayName("accessKey应该使用默认值")
        void accessKeyShouldUseDefaultValue() {
            assertThat(properties.getAccessKey()).isEqualTo("admin");
        }

        @Test
        @DisplayName("secretKey应该使用默认值")
        void secretKeyShouldUseDefaultValue() {
            assertThat(properties.getSecretKey()).isEqualTo("admin123");
        }

        @Test
        @DisplayName("bucketName应该使用默认值")
        void bucketNameShouldUseDefaultValue() {
            assertThat(properties.getBucketName()).isEqualTo("spring4demo");
        }

        @Test
        @DisplayName("region应该使用默认值")
        void regionShouldUseDefaultValue() {
            assertThat(properties.getRegion()).isEqualTo("us-east-1");
        }

        @Test
        @DisplayName("pathStyleAccess应该使用默认值")
        void pathStyleAccessShouldUseDefaultValue() {
            assertThat(properties.getPathStyleAccess()).isTrue();
        }

        @Test
        @DisplayName("maxFileSize应该使用默认值")
        void maxFileSizeShouldUseDefaultValue() {
            assertThat(properties.getMaxFileSize()).isEqualTo(10L);
        }

        @Test
        @DisplayName("maxRequestSize应该使用默认值")
        void maxRequestSizeShouldUseDefaultValue() {
            assertThat(properties.getMaxRequestSize()).isEqualTo(100L);
        }
    }
}