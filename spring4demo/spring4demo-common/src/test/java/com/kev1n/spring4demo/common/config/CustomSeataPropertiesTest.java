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
 * Seata 分布式事务配置属性测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@DisplayName("Seata 分布式事务配置属性测试")
class CustomSeataPropertiesTest {

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // Given
        CustomSeataProperties properties = new CustomSeataProperties();

        // Then - 验证默认值
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getApplicationName()).isEqualTo("spring4demo");
        assertThat(properties.getTxServiceGroup()).isEqualTo("spring4demo-tx-group");
        assertThat(properties.getService()).isNotNull();
        assertThat(properties.getClient()).isNotNull();
        assertThat(properties.getTransport()).isNotNull();
        assertThat(properties.getConfig()).isNotNull();
        assertThat(properties.getRegistry()).isNotNull();
    }

    @Test
    @DisplayName("配置绑定测试 - 基本配置")
    void testBindBasicConfiguration() {
        // Given - 基本配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.enabled", false);
        config.put("seata.application-name", "test-application");
        config.put("seata.tx-service-group", "test-tx-group");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证绑定结果
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getApplicationName()).isEqualTo("test-application");
        assertThat(properties.getTxServiceGroup()).isEqualTo("test-tx-group");
    }

    @Test
    @DisplayName("配置绑定测试 - 服务配置")
    void testBindServiceConfiguration() {
        // Given - 服务配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.service.grouplist-retry-interval", 5000);
        config.put("seata.service.enable-degrade", true);
        config.put("seata.service.disable-global-transaction", true);
        config.put("seata.service.vgroup-mapping.default-tx-group", "custom-tx-group");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证服务配置
        assertThat(properties.getService().getGrouplistRetryInterval()).isEqualTo(5000);
        assertThat(properties.getService().isEnableDegrade()).isTrue();
        assertThat(properties.getService().isDisableGlobalTransaction()).isTrue();
        assertThat(properties.getService().getVgroupMapping().getDefaultTxGroup()).isEqualTo("custom-tx-group");
    }

    @Test
    @DisplayName("配置绑定测试 - RM 配置")
    void testBindRmConfiguration() {
        // Given - RM 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.client.rm.async-commit-buffer-limit", 20000);
        config.put("seata.client.rm.lock-retry-interval", 20);
        config.put("seata.client.rm.lock-retry-times", 50);
        config.put("seata.client.rm.query-timeout", 2000);
        config.put("seata.client.rm.commit-retry-count", 10);
        config.put("seata.client.rm.rollback-retry-count", 10);
        config.put("seata.client.rm.table-meta-check-enable", false);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 RM 配置
        assertThat(properties.getClient().getRm().getAsyncCommitBufferLimit()).isEqualTo(20000);
        assertThat(properties.getClient().getRm().getLockRetryInterval()).isEqualTo(20);
        assertThat(properties.getClient().getRm().getLockRetryTimes()).isEqualTo(50);
        assertThat(properties.getClient().getRm().getQueryTimeout()).isEqualTo(2000);
        assertThat(properties.getClient().getRm().getCommitRetryCount()).isEqualTo(10);
        assertThat(properties.getClient().getRm().getRollbackRetryCount()).isEqualTo(10);
        assertThat(properties.getClient().getRm().isTableMetaCheckEnable()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - TM 配置")
    void testBindTmConfiguration() {
        // Given - TM 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.client.tm.commit-retry-count", 10);
        config.put("seata.client.tm.rollback-retry-count", 10);
        config.put("seata.client.tm.global-lock-retry-interval", 20);
        config.put("seata.client.tm.global-lock-retry-times", 50);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 TM 配置
        assertThat(properties.getClient().getTm().getCommitRetryCount()).isEqualTo(10);
        assertThat(properties.getClient().getTm().getRollbackRetryCount()).isEqualTo(10);
        assertThat(properties.getClient().getTm().getGlobalLockRetryInterval()).isEqualTo(20);
        assertThat(properties.getClient().getTm().getGlobalLockRetryTimes()).isEqualTo(50);
    }

    @Test
    @DisplayName("配置绑定测试 - Undo 配置")
    void testBindUndoConfiguration() {
        // Given - Undo 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.client.undo.enable", false);
        config.put("seata.client.undo.data-validation", false);
        config.put("seata.client.undo.log-serialization", "fastjson");
        config.put("seata.client.undo.only-care-update-columns", false);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Undo 配置
        assertThat(properties.getClient().getUndo().isEnable()).isFalse();
        assertThat(properties.getClient().getUndo().isDataValidation()).isFalse();
        assertThat(properties.getClient().getUndo().getLogSerialization()).isEqualTo("fastjson");
        assertThat(properties.getClient().getUndo().isOnlyCareUpdateColumns()).isFalse();
    }

    @Test
    @DisplayName("配置绑定测试 - Transport TCP 配置")
    void testBindTransportTcpConfiguration() {
        // Given - Transport TCP 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.transport.tcp.enabled", false);
        config.put("seata.transport.tcp.name", "CustomTCP");
        config.put("seata.transport.tcp.boss-thread-size", 2);
        config.put("seata.transport.tcp.worker-thread-size", 2);
        config.put("seata.transport.tcp.shutdown-wait", 5);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Transport TCP 配置
        assertThat(properties.getTransport().getTcp().isEnabled()).isFalse();
        assertThat(properties.getTransport().getTcp().getName()).isEqualTo("CustomTCP");
        assertThat(properties.getTransport().getTcp().getBossThreadSize()).isEqualTo(2);
        assertThat(properties.getTransport().getTcp().getWorkerThreadSize()).isEqualTo(2);
        assertThat(properties.getTransport().getTcp().getShutdownWait()).isEqualTo(5);
    }

    @Test
    @DisplayName("配置绑定测试 - Config File 配置")
    void testBindConfigFileConfiguration() {
        // Given - Config File 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.config.type", "file");
        config.put("seata.config.file.name", "custom-file.conf");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Config File 配置
        assertThat(properties.getConfig().getType()).isEqualTo("file");
        assertThat(properties.getConfig().getFile().getName()).isEqualTo("custom-file.conf");
    }

    @Test
    @DisplayName("配置绑定测试 - Config Nacos 配置")
    void testBindConfigNacosConfiguration() {
        // Given - Config Nacos 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.config.type", "nacos");
        config.put("seata.config.nacos.server-addr", "192.168.1.100:8848");
        config.put("seata.config.nacos.namespace", "test-namespace");
        config.put("seata.config.nacos.group", "TEST_GROUP");
        config.put("seata.config.nacos.username", "nacos-user");
        config.put("seata.config.nacos.password", "nacos-password");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Config Nacos 配置
        assertThat(properties.getConfig().getType()).isEqualTo("nacos");
        assertThat(properties.getConfig().getNacos().getServerAddr()).isEqualTo("192.168.1.100:8848");
        assertThat(properties.getConfig().getNacos().getNamespace()).isEqualTo("test-namespace");
        assertThat(properties.getConfig().getNacos().getGroup()).isEqualTo("TEST_GROUP");
        assertThat(properties.getConfig().getNacos().getUsername()).isEqualTo("nacos-user");
        assertThat(properties.getConfig().getNacos().getPassword()).isEqualTo("nacos-password");
    }

    @Test
    @DisplayName("配置绑定测试 - Registry File 配置")
    void testBindRegistryFileConfiguration() {
        // Given - Registry File 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.registry.type", "file");
        config.put("seata.registry.file.name", "custom-registry.conf");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Registry File 配置
        assertThat(properties.getRegistry().getType()).isEqualTo("file");
        assertThat(properties.getRegistry().getFile().getName()).isEqualTo("custom-registry.conf");
    }

    @Test
    @DisplayName("配置绑定测试 - Registry Nacos 配置")
    void testBindRegistryNacosConfiguration() {
        // Given - Registry Nacos 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.registry.type", "nacos");
        config.put("seata.registry.nacos.server-addr", "192.168.1.100:8848");
        config.put("seata.registry.nacos.namespace", "test-namespace");
        config.put("seata.registry.nacos.group", "TEST_GROUP");
        config.put("seata.registry.nacos.username", "nacos-user");
        config.put("seata.registry.nacos.password", "nacos-password");
        config.put("seata.registry.nacos.application", "seata-server-test");

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Registry Nacos 配置
        assertThat(properties.getRegistry().getType()).isEqualTo("nacos");
        assertThat(properties.getRegistry().getNacos().getServerAddr()).isEqualTo("192.168.1.100:8848");
        assertThat(properties.getRegistry().getNacos().getNamespace()).isEqualTo("test-namespace");
        assertThat(properties.getRegistry().getNacos().getGroup()).isEqualTo("TEST_GROUP");
        assertThat(properties.getRegistry().getNacos().getUsername()).isEqualTo("nacos-user");
        assertThat(properties.getRegistry().getNacos().getPassword()).isEqualTo("nacos-password");
        assertThat(properties.getRegistry().getNacos().getApplication()).isEqualTo("seata-server-test");
    }

    @Test
    @DisplayName("配置绑定测试 - Log 配置")
    void testBindLogConfiguration() {
        // Given - Log 配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.log.exception-rate", 50);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 Log 配置
        assertThat(properties.getLog().getExceptionRate()).isEqualTo(50);
    }

    @Test
    @DisplayName("配置绑定测试 - 类型转换")
    void testBindTypeConversion() {
        // Given - 字符串类型的配置值
        Map<String, Object> config = new HashMap<>();
        config.put("seata.enabled", "false"); // 字符串转 boolean
        config.put("seata.service.grouplist-retry-interval", "3000"); // 字符串转 int
        config.put("seata.client.rm.async-commit-buffer-limit", "15000"); // 字符串转 int
        config.put("seata.log.exception-rate", "80"); // 字符串转 int

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证类型转换正确
        assertThat(properties.isEnabled()).isInstanceOf(Boolean.class);
        assertThat(properties.getService().getGrouplistRetryInterval()).isInstanceOf(Integer.class);
        assertThat(properties.getClient().getRm().getAsyncCommitBufferLimit()).isInstanceOf(Integer.class);
        assertThat(properties.getLog().getExceptionRate()).isInstanceOf(Integer.class);

        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getService().getGrouplistRetryInterval()).isEqualTo(3000);
        assertThat(properties.getClient().getRm().getAsyncCommitBufferLimit()).isEqualTo(15000);
        assertThat(properties.getLog().getExceptionRate()).isEqualTo(80);
    }

    @Test
    @DisplayName("配置绑定测试 - 边界值")
    void testBindBoundaryValues() {
        // Given - 边界值配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.client.rm.async-commit-buffer-limit", 0); // 最小值
        config.put("seata.client.rm.lock-retry-times", Integer.MAX_VALUE); // 最大值
        config.put("seata.log.exception-rate", 0); // 最小值
        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证边界值正确处理
        assertThat(properties.getClient().getRm().getAsyncCommitBufferLimit()).isZero();
        assertThat(properties.getClient().getRm().getLockRetryTimes()).isEqualTo(Integer.MAX_VALUE);
        assertThat(properties.getLog().getExceptionRate()).isZero();
    }

    @Test
    @DisplayName("配置绑定测试 - null 值")
    void testBindNullValues() {
        // Given - 包含 null 值的配置
        Map<String, Object> config = new HashMap<>();
        config.put("seata.application-name", null);
        config.put("seata.tx-service-group", null);

        ConfigurationPropertySource source = new MapConfigurationPropertySource(config);
        Binder binder = new Binder(source);

        // When - 绑定配置
        CustomSeataProperties properties = binder.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证 null 值处理（使用默认值）
        assertThat(properties.getApplicationName()).isEqualTo("spring4demo"); // 默认值
        assertThat(properties.getTxServiceGroup()).isEqualTo("spring4demo-tx-group"); // 默认值
    }

    @Test
    @DisplayName("配置绑定测试 - 配置中心类型")
    void testBindConfigCenterTypes() {
        // Given - 不同配置中心类型
        Map<String, Object> config1 = new HashMap<>();
        config1.put("seata.config.type", "file");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("seata.config.type", "nacos");

        Map<String, Object> config3 = new HashMap<>();
        config3.put("seata.config.type", "apollo");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        CustomSeataProperties properties1 = binder1.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());
        CustomSeataProperties properties2 = binder2.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());
        CustomSeataProperties properties3 = binder3.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证配置中心类型
        assertThat(properties1.getConfig().getType()).isEqualTo("file");
        assertThat(properties2.getConfig().getType()).isEqualTo("nacos");
        assertThat(properties3.getConfig().getType()).isEqualTo("apollo");
    }

    @Test
    @DisplayName("配置绑定测试 - 注册中心类型")
    void testBindRegistryTypes() {
        // Given - 不同注册中心类型
        Map<String, Object> config1 = new HashMap<>();
        config1.put("seata.registry.type", "file");

        Map<String, Object> config2 = new HashMap<>();
        config2.put("seata.registry.type", "nacos");

        Map<String, Object> config3 = new HashMap<>();
        config3.put("seata.registry.type", "eureka");

        ConfigurationPropertySource source1 = new MapConfigurationPropertySource(config1);
        ConfigurationPropertySource source2 = new MapConfigurationPropertySource(config2);
        ConfigurationPropertySource source3 = new MapConfigurationPropertySource(config3);
        Binder binder1 = new Binder(source1);
        Binder binder2 = new Binder(source2);
        Binder binder3 = new Binder(source3);

        // When - 绑定配置
        CustomSeataProperties properties1 = binder1.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());
        CustomSeataProperties properties2 = binder2.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());
        CustomSeataProperties properties3 = binder3.bind("seata", Bindable.of(CustomSeataProperties.class))
                .orElse(new CustomSeataProperties());

        // Then - 验证注册中心类型
        assertThat(properties1.getRegistry().getType()).isEqualTo("file");
        assertThat(properties2.getRegistry().getType()).isEqualTo("nacos");
        assertThat(properties3.getRegistry().getType()).isEqualTo("eureka");
    }
}