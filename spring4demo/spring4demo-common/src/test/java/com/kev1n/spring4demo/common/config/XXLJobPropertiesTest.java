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
 * XXL-Job 配置属性测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("XXL-Job 配置属性测试")
class XXLJobPropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        XXLJobProperties properties = new XXLJobProperties();

        // Then - 验证默认值
        assertThat(properties.getAdmin().getAddresses()).isNull();
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("default_token");
        assertThat(properties.getExecutor().getAppname()).isNull();
        assertThat(properties.getExecutor().getAddress()).isNull();
        assertThat(properties.getExecutor().getIp()).isNull();
        assertThat(properties.getExecutor().getPort()).isEqualTo(9999);
        assertThat(properties.getExecutor().getLogpath()).isEqualTo("/data/applogs/xxl-job/jobhandler");
        assertThat(properties.getExecutor().getLogretentiondays()).isEqualTo(30);
    }

    @Test
    @DisplayName("配置绑定测试 - 正常配置")
    void testBindConfiguration() {
        // Given - 配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.addresses", "http://xxl-job-admin.example.com");
        config.put("xxl.job.admin.access-token", "custom_token");
        config.put("xxl.job.executor.appname", "test-executor");
        config.put("xxl.job.executor.address", "http://executor.example.com");
        config.put("xxl.job.executor.ip", "192.168.1.100");
        config.put("xxl.job.executor.port", 8888);
        config.put("xxl.job.executor.logpath", "/custom/logs/xxl-job");
        config.put("xxl.job.executor.logretentiondays", 7);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证绑定结果
        assertThat(properties.getAdmin().getAddresses()).isEqualTo("http://xxl-job-admin.example.com");
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("custom_token");
        assertThat(properties.getExecutor().getAppname()).isEqualTo("test-executor");
        assertThat(properties.getExecutor().getAddress()).isEqualTo("http://executor.example.com");
        assertThat(properties.getExecutor().getIp()).isEqualTo("192.168.1.100");
        assertThat(properties.getExecutor().getPort()).isEqualTo(8888);
        assertThat(properties.getExecutor().getLogpath()).isEqualTo("/custom/logs/xxl-job");
        assertThat(properties.getExecutor().getLogretentiondays()).isEqualTo(7);
    }

    @Test
    @DisplayName("配置绑定测试 - 部分配置")
    void testBindPartialConfiguration() {
        // Given - 部分配置数据
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.addresses", "http://custom-xxl-job-admin.example.com");
        config.put("xxl.job.executor.appname", "custom-executor");
        config.put("xxl.job.executor.port", 7777);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证绑定结果（配置的值使用配置值，未配置的值使用默认值）
        assertThat(properties.getAdmin().getAddresses()).isEqualTo("http://custom-xxl-job-admin.example.com");
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("default_token"); // 默认值
        assertThat(properties.getExecutor().getAppname()).isEqualTo("custom-executor");
        assertThat(properties.getExecutor().getAddress()).isNull(); // 默认值
        assertThat(properties.getExecutor().getIp()).isNull(); // 默认值
        assertThat(properties.getExecutor().getPort()).isEqualTo(7777);
        assertThat(properties.getExecutor().getLogpath()).isEqualTo("/data/applogs/xxl-job/jobhandler"); // 默认值
        assertThat(properties.getExecutor().getLogretentiondays()).isEqualTo(30); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 空配置")
    void testBindEmptyConfiguration() {
        // Given - 空配置数据
        Map<String, Object> config = new HashMap<>();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证所有值都使用默认值
        assertThat(properties.getAdmin().getAddresses()).isNull();
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("default_token");
        assertThat(properties.getExecutor().getAppname()).isNull();
        assertThat(properties.getExecutor().getAddress()).isNull();
        assertThat(properties.getExecutor().getIp()).isNull();
        assertThat(properties.getExecutor().getPort()).isEqualTo(9999);
        assertThat(properties.getExecutor().getLogpath()).isEqualTo("/data/applogs/xxl-job/jobhandler");
        assertThat(properties.getExecutor().getLogretentiondays()).isEqualTo(30);
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.addresses", "http://test.example.com");
        config.put("xxl.job.admin.access-token", "test-token");
        config.put("xxl.job.executor.appname", "test-executor");
        config.put("xxl.job.executor.port", "6666"); // 字符串转 int
        config.put("xxl.job.executor.logretentiondays", "14"); // 字符串转 int

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证类型转换正确
        assertThat(properties.getAdmin().getAddresses()).isInstanceOf(String.class);
        assertThat(properties.getAdmin().getAccessToken()).isInstanceOf(String.class);
        assertThat(properties.getExecutor().getAppname()).isInstanceOf(String.class);
        assertThat(properties.getExecutor().getPort()).isInstanceOf(Integer.class);
        assertThat(properties.getExecutor().getLogretentiondays()).isInstanceOf(Integer.class);

        assertThat(properties.getExecutor().getPort()).isEqualTo(6666);
        assertThat(properties.getExecutor().getLogretentiondays()).isEqualTo(14);
    }

    @Test
    @DisplayName("配置绑定测试 - 调度中心多地址")
    void testBindAdminMultipleAddresses() {
        // Given - 调度中心多地址配置
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.addresses", "http://xxl-job-admin1.example.com,http://xxl-job-admin2.example.com");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证多地址配置
        assertThat(properties.getAdmin().getAddresses())
                .isEqualTo("http://xxl-job-admin1.example.com,http://xxl-job-admin2.example.com");
    }

    @Test
    @DisplayName("配置绑定测试 - 执行器端口边界值")
    void testBindExecutorPortBoundaryValues() {
        // Given - 执行器端口边界值
        Map<String, Object> config1 = new HashMap<>();
        config1.put("xxl.job.executor.port", -1); // 小于等于0，自动获取
        Map<String, Object> config2 = new HashMap<>();
        config2.put("xxl.job.executor.port", 65535); // 最大端口
        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);

        // When - 绑定配置
        XXLJobProperties properties1 = binder1.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties2 = binder2.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证端口边界值
        assertThat(properties1.getExecutor().getPort()).isEqualTo(-1);
        assertThat(properties2.getExecutor().getPort()).isEqualTo(65535);
    }

    @Test
    @DisplayName("配置绑定测试 - 日志保留天数边界值")
    void testBindLogRetentionDaysBoundaryValues() {
        // Given - 日志保留天数边界值
        Map<String, Object> config1 = new HashMap<>();
        config1.put("xxl.job.executor.logretentiondays", -1); // 关闭自动清理

        Map<String, Object> config2 = new HashMap<>();
        config2.put("xxl.job.executor.logretentiondays", 3); // 最小生效值
        Map<String, Object> config3 = new HashMap<>();
        config3.put("xxl.job.executor.logretentiondays", 365); // 一年
        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        XXLJobProperties properties1 = binder1.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties2 = binder2.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties3 = binder3.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证日志保留天数边界值
        assertThat(properties1.getExecutor().getLogretentiondays()).isEqualTo(-1);
        assertThat(properties2.getExecutor().getLogretentiondays()).isEqualTo(3);
        assertThat(properties3.getExecutor().getLogretentiondays()).isEqualTo(365);
    }

    @Test
    @DisplayName("配置绑定测试 - IP 地址格式")
    void testBindIpAddressFormat() {
        // Given - 不同格式的IP 地址
        Map<String, Object> config1 = new HashMap<>();
        config1.put("xxl.job.executor.ip", "192.168.1.100");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("xxl.job.executor.ip", "10.0.0.1");

        Map<String, Object> config3 = new HashMap<>();
        config3.put("xxl.job.executor.ip", "127.0.0.1");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        XXLJobProperties properties1 = binder1.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties2 = binder2.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties3 = binder3.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证 IP 地址格式
        assertThat(properties1.getExecutor().getIp()).isEqualTo("192.168.1.100");
        assertThat(properties2.getExecutor().getIp()).isEqualTo("10.0.0.1");
        assertThat(properties3.getExecutor().getIp()).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("配置绑定测试 - 日志路径格式")
    void testBindLogPathFormat() {
        // Given - 不同格式的日志路径
        Map<String, Object> config1 = new HashMap<>();
        config1.put("xxl.job.executor.logpath", "/var/log/xxl-job");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("xxl.job.executor.logpath", "C:\\logs\\xxl-job");

        Map<String, Object> config3 = new HashMap<>();
        config3.put("xxl.job.executor.logpath", "./logs/xxl-job");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        XXLJobProperties properties1 = binder1.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties2 = binder2.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());
        XXLJobProperties properties3 = binder3.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证日志路径格式
        assertThat(properties1.getExecutor().getLogpath()).isEqualTo("/var/log/xxl-job");
        assertThat(properties2.getExecutor().getLogpath()).isEqualTo("C:\\logs\\xxl-job");
        assertThat(properties3.getExecutor().getLogpath()).isEqualTo("./logs/xxl-job");
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.addresses", null);
        config.put("xxl.job.executor.appname", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证 null 值处理（使用默认值）
        assertThat(properties.getAdmin().getAddresses()).isNull();
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("default_token"); // 默认值
        assertThat(properties.getExecutor().getAppname()).isNull();
        assertThat(properties.getExecutor().getPort()).isEqualTo(9999); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 特殊字符")
    void testBindSpecialCharacters() {
        // Given - 包含特殊字符的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("xxl.job.admin.access-token", "token-with-special-chars-!@#$%");
        config.put("xxl.job.executor.appname", "executor-name_123");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        XXLJobProperties properties = binder.bind("xxl.job", Bindable.of(XXLJobProperties.class))
                .orElse(new XXLJobProperties());

        // Then - 验证特殊字符正确处理
        assertThat(properties.getAdmin().getAccessToken()).isEqualTo("token-with-special-chars-!@#$%");
        assertThat(properties.getExecutor().getAppname()).isEqualTo("executor-name_123");
    }
}
