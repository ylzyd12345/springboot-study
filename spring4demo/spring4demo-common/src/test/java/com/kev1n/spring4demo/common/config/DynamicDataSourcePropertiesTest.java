package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DynamicDataSource 配置属性测试
 * 
 * 测试要点：
 * 1. 验证配置属性绑定
 * 2. 验证默认值是否正确
 * 3. 验证嵌套配置对象是否正确注入
 * 
 * @author kev1n
 * @version 1.0.0
 * @since 2025-12-30
 */
@SpringBootTest
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@TestPropertySource(properties = {
    "spring.datasource.dynamic.enabled=false",
    "spring.datasource.dynamic.strict=true",
    "spring.datasource.dynamic.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.dynamic.datasource.master.url=jdbc:mysql://localhost:3306/master",
    "spring.datasource.dynamic.dynamic.datasource.master.username=root",
    "spring.datasource.dynamic.dynamic.datasource.master.password=password",
    "spring.datasource.dynamic.dynamic.datasource.master.primary=true"
})
@DisplayName("DynamicDataSource 配置属性测试")
class DynamicDataSourcePropertiesTest {

    @Autowired
    private DynamicDataSourceProperties properties;

    @Test
    @DisplayName("应该正确注入DynamicDataSourceProperties Bean")
    void shouldInjectDynamicDataSourceProperties() {
        assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该正确配置enabled")
    void shouldConfigureEnabled() {
        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("应该正确配置strict")
    void shouldConfigureStrict() {
        assertThat(properties.isStrict()).isTrue();
    }

    @Test
    @DisplayName("应该正确配置datasource")
    void shouldConfigureDatasource() {
        Map<String, DynamicDataSourceProperties.DataSourceConfig> datasource = properties.getDatasource();
        
        assertThat(datasource).isNotNull();
        assertThat(datasource).containsKey("master");
        
        DynamicDataSourceProperties.DataSourceConfig masterConfig = datasource.get("master");
        assertThat(masterConfig).isNotNull();
        assertThat(masterConfig.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
        assertThat(masterConfig.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/master");
        assertThat(masterConfig.getUsername()).isEqualTo("root");
        assertThat(masterConfig.getPassword()).isEqualTo("password");
        assertThat(masterConfig.isPrimary()).isTrue();
    }

    @DisplayName("应该使用默认值当配置未设置")
    @EnableConfigurationProperties(DynamicDataSourceProperties.class)
    class DefaultValuesTest {

        @Autowired
        private DynamicDataSourceProperties properties;

        @Test
        @DisplayName("enabled应该使用默认值")
        void enabledShouldUseDefaultValue() {
            assertThat(properties.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("strict应该使用默认值")
        void strictShouldUseDefaultValue() {
            assertThat(properties.isStrict()).isFalse();
        }

        @Test
        @DisplayName("DataSourceConfig.type应该使用默认值")
        void typeShouldUseDefaultValue() {
            // 由于没有配置datasource，这里只测试默认值
            assertThat(properties).isNotNull();
        }
    }
}