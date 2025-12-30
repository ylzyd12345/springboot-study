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
 * 动态数据源配置属性测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("动态数据源配置属性测试")
class DynamicDataSourcePropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        DynamicDataSourceProperties properties = new DynamicDataSourceProperties();

        // Then - 验证默认值
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.isStrict()).isFalse();
        assertThat(properties.getDatasource()).isNull();
    }

    @Test
    @DisplayName("配置绑定测试 - 单数据源")
    void testBindSingleDataSource() {
        // Given - 单数据源配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.enabled", true);
        config.put("spring.datasource.dynamic.strict", false);
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.driver-class-name", "com.mysql.cj.jdbc.Driver");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/spring4demo");
        config.put("spring.datasource.dynamic.datasource.master.username", "root");
        config.put("spring.datasource.dynamic.datasource.master.password", "password");
        config.put("spring.datasource.dynamic.datasource.master.primary", true);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证绑定结果
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.isStrict()).isFalse();
        assertThat(properties.getDatasource()).isNotNull();
        assertThat(properties.getDatasource()).containsKey("master");

        DynamicDataSourceProperties.DataSourceConfig master = properties.getDatasource().get("master");
        assertThat(master.getName()).isEqualTo("master");
        assertThat(master.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
        assertThat(master.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/spring4demo");
        assertThat(master.getUsername()).isEqualTo("root");
        assertThat(master.getPassword()).isEqualTo("password");
        assertThat(master.isPrimary()).isTrue();
    }

    @Test
    @DisplayName("配置绑定测试 - 多数据源")
    void testBindMultipleDataSources() {
        // Given - 多数据源配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.enabled", true);
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/master");
        config.put("spring.datasource.dynamic.datasource.master.primary", true);
        config.put("spring.datasource.dynamic.datasource.slave.name", "slave");
        config.put("spring.datasource.dynamic.datasource.slave.url", "jdbc:mysql://localhost:3306/slave");
        config.put("spring.datasource.dynamic.datasource.slave.readonly", true);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证绑定结果
        assertThat(properties.getDatasource()).isNotNull();
        assertThat(properties.getDatasource()).hasSize(2);
        assertThat(properties.getDatasource()).containsKeys("master", "slave");

        DynamicDataSourceProperties.DataSourceConfig master = properties.getDatasource().get("master");
        assertThat(master.isPrimary()).isTrue();
        assertThat(master.isReadonly()).isFalse();

        DynamicDataSourceProperties.DataSourceConfig slave = properties.getDatasource().get("slave");
        assertThat(slave.isPrimary()).isFalse();
        assertThat(slave.isReadonly()).isTrue();
    }

    @Test
    @DisplayName("配置绑定测试 - Druid 连接池配置")
    void testBindDruidConfig() {
        // Given - Druid 连接池配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/test");
        config.put("spring.datasource.dynamic.datasource.master.druid.initial-size", 10);
        config.put("spring.datasource.dynamic.datasource.master.druid.min-idle", 5);
        config.put("spring.datasource.dynamic.datasource.master.druid.max-active", 50);
        config.put("spring.datasource.dynamic.datasource.master.druid.max-wait", 60000);
        config.put("spring.datasource.dynamic.datasource.master.druid.test-while-idle", true);
        config.put("spring.datasource.dynamic.datasource.master.druid.validation-query", "SELECT 1");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证 Druid 配置
        DynamicDataSourceProperties.DataSourceConfig master = properties.getDatasource().get("master");
        assertThat(master.getDruid()).isNotNull();
        assertThat(master.getDruid().getInitialSize()).isEqualTo(10);
        assertThat(master.getDruid().getMinIdle()).isEqualTo(5);
        assertThat(master.getDruid().getMaxActive()).isEqualTo(50);
        assertThat(master.getDruid().getMaxWait()).isEqualTo(60000);
        assertThat(master.getDruid().isTestWhileIdle()).isTrue();
        assertThat(master.getDruid().getValidationQuery()).isEqualTo("SELECT 1");
    }

    @Test
    @DisplayName("配置绑定测试 - HikariCP 连接池配置")
    void testBindHikariConfig() {
        // Given - HikariCP 连接池配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/test");
        config.put("spring.datasource.dynamic.datasource.master.hikari.minimum-idle", 10);
        config.put("spring.datasource.dynamic.datasource.master.hikari.maximum-pool-size", 30);
        config.put("spring.datasource.dynamic.datasource.master.hikari.connection-timeout", 30000);
        config.put("spring.datasource.dynamic.datasource.master.hikari.idle-timeout", 600000);
        config.put("spring.datasource.dynamic.datasource.master.hikari.max-lifetime", 1800000);
        config.put("spring.datasource.dynamic.datasource.master.hikari.pool-name", "TestHikariCP");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证 HikariCP 配置
        DynamicDataSourceProperties.DataSourceConfig master = properties.getDatasource().get("master");
        assertThat(master.getHikari()).isNotNull();
        assertThat(master.getHikari().getMinimumIdle()).isEqualTo(10);
        assertThat(master.getHikari().getMaximumPoolSize()).isEqualTo(30);
        assertThat(master.getHikari().getConnectionTimeout()).isEqualTo(30000);
        assertThat(master.getHikari().getIdleTimeout()).isEqualTo(600000);
        assertThat(master.getHikari().getMaxLifetime()).isEqualTo(1800000);
        assertThat(master.getHikari().getPoolName()).isEqualTo("TestHikariCP");
    }

    @Test
    @DisplayName("配置绑定测试 - 数据源类型")
    void testBindDataSourceType() {
        // Given - 不同数据源类型
        Map<String, Object> config1 = new HashMap<>();
        config1.put("spring.datasource.dynamic.datasource.master.name", "master");
        config1.put("spring.datasource.dynamic.datasource.master.type", "com.alibaba.druid.pool.DruidDataSource");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("spring.datasource.dynamic.datasource.master.name", "master");
        config2.put("spring.datasource.dynamic.datasource.master.type", "com.zaxxer.hikari.HikariDataSource");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);

        // When - 绑定配置
        DynamicDataSourceProperties properties1 = binder1.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());
        DynamicDataSourceProperties properties2 = binder2.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证数据源类型
        assertThat(properties1.getDatasource().get("master").getType()).isEqualTo("com.alibaba.druid.pool.DruidDataSource");
        assertThat(properties2.getDatasource().get("master").getType()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
    }

    @Test
    @DisplayName("配置绑定测试 - 严格模式")
    void testBindStrictMode() {
        // Given - 严格模式配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.strict", true);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证严格模式
        assertThat(properties.isStrict()).isTrue();
    }

    @Test
    @DisplayName("配置绑定测试 - Druid 监控配置")
    void testBindDruidMonitorConfig() {
        // Given - Druid 监控配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/test");
        config.put("spring.datasource.dynamic.datasource.master.druid.stat-view-servlet-enabled", true);
        config.put("spring.datasource.dynamic.datasource.master.druid.stat-view-servlet-url-pattern", "/druid/*");
        config.put("spring.datasource.dynamic.datasource.master.druid.stat-view-servlet-login-username", "admin");
        config.put("spring.datasource.dynamic.datasource.master.druid.stat-view-servlet-login-password", "admin123");
        config.put("spring.datasource.dynamic.datasource.master.druid.web-stat-filter-enabled", true);
        config.put("spring.datasource.dynamic.datasource.master.druid.web-stat-filter-url-pattern", "/*");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证 Druid 监控配置
        DynamicDataSourceProperties.DataSourceConfig master = properties.getDatasource().get("master");
        assertThat(master.getDruid().isStatViewServletEnabled()).isTrue();
        assertThat(master.getDruid().getStatViewServletUrlPattern()).isEqualTo("/druid/*");
        assertThat(master.getDruid().getStatViewServletLoginUsername()).isEqualTo("admin");
        assertThat(master.getDruid().getStatViewServletLoginPassword()).isEqualTo("admin123");
        assertThat(master.getDruid().isWebStatFilterEnabled()).isTrue();
        assertThat(master.getDruid().getWebStatFilterUrlPattern()).isEqualTo("/*");
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.enabled", "true"); // 字符串转 boolean
        config.put("spring.datasource.dynamic.strict", "false"); // 字符串转 boolean
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.druid.initial-size", "15"); // 字符串转 int
        config.put("spring.datasource.dynamic.datasource.master.druid.max-wait", "30000"); // 字符串转 int

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证类型转换正确
        assertThat(properties.isEnabled()).isInstanceOf(Boolean.class);
        assertThat(properties.isStrict()).isInstanceOf(Boolean.class);
        assertThat(properties.getDatasource().get("master").getDruid().getInitialSize()).isInstanceOf(Integer.class);
        assertThat(properties.getDatasource().get("master").getDruid().getMaxWait()).isInstanceOf(Integer.class);

        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.isStrict()).isFalse();
        assertThat(properties.getDatasource().get("master").getDruid().getInitialSize()).isEqualTo(15);
        assertThat(properties.getDatasource().get("master").getDruid().getMaxWait()).isEqualTo(30000);
    }

    @Test
    @DisplayName("配置绑定测试 - 边界值")
    void testBindBoundaryValues() {
        // Given - 边界值配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/test");
        config.put("spring.datasource.dynamic.datasource.master.druid.initial-size", 0); // 最小值
        config.put("spring.datasource.dynamic.datasource.master.druid.max-active", Integer.MAX_VALUE); // 最大值
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证边界值正确处理
        assertThat(properties.getDatasource().get("master").getDruid().getInitialSize()).isZero();
        assertThat(properties.getDatasource().get("master").getDruid().getMaxActive()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证 null 值处理（使用默认值）
        assertThat(properties.isEnabled()).isTrue(); // 默认值
        assertThat(properties.isStrict()).isFalse(); // 默认值
        assertThat(properties.getDatasource()).isNull();
    }

    @Test
    @DisplayName("配置绑定测试 - JDBC URL 格式")
    void testBindJdbcUrlFormat() {
        // Given - 不同格式的JDBC URL
        Map<String, Object> config = new HashMap<>();
        config.put("spring.datasource.dynamic.datasource.master.name", "master");
        config.put("spring.datasource.dynamic.datasource.master.url", "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        DynamicDataSourceProperties properties = binder.bind("spring.datasource.dynamic", Bindable.of(DynamicDataSourceProperties.class))
                .orElse(new DynamicDataSourceProperties());

        // Then - 验证 JDBC URL 格式
        assertThat(properties.getDatasource().get("master").getUrl())
                .isEqualTo("jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC");
    }
}