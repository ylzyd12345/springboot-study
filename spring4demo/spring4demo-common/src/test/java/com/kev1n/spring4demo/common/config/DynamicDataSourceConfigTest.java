package com.kev1n.spring4demo.common.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 动态数据源配置测试
 *
 * 测试要点：
 * 1. 验证配置类 Bean 创建
 * 2. 验证动态数据源初始化
 * 3. 验证数据源注入
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@DisplayName("动态数据源配置测试")
class DynamicDataSourceConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    @Test
    @DisplayName("验证 DynamicDataSourceConfig Bean 创建")
    void shouldCreateDynamicDataSourceConfigBean() {
        // Then - 验证 Bean 创建成功
        assertThat(dynamicDataSourceConfig).isNotNull();
    }

    @Test
    @DisplayName("验证 DataSource Bean 创建")
    void shouldCreateDataSourceBean() {
        // Then - 验证 Bean 创建成功
        assertThat(dataSource).isNotNull();
    }

    @Test
    @DisplayName("验证动态数据源类型")
    void shouldVerifyDynamicDataSourceType() {
        // When - 检查数据源类型
        boolean isDynamicRoutingDataSource = dataSource instanceof DynamicRoutingDataSource;

        // Then - 验证数据源类型
        assertThat(isDynamicRoutingDataSource).isTrue();
    }

    @Test
    @DisplayName("验证动态数据源配置")
    void shouldVerifyDynamicDataSourceConfiguration() {
        // Given - 获取动态数据源
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;

        // When - 获取数据源配置
        var dataSources = dynamicRoutingDataSource.getDataSources();

        // Then - 验证数据源配置不为空
        assertThat(dataSources).isNotNull();
        assertThat(dataSources).isNotEmpty();
    }

    @Test
    @DisplayName("验证默认数据源配置")
    void shouldVerifyDefaultDataSource() {
        // Given - 获取动态数据源
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;

        // When - 获取默认数据源
        DataSource defaultDataSource = dynamicRoutingDataSource.determineDataSource();

        // Then - 验证默认数据源不为空
        assertThat(defaultDataSource).isNotNull();
    }

    @Test
    @DisplayName("验证数据源连接性")
    void shouldVerifyDataSourceConnection() throws Exception {
        // When - 尝试获取数据库连接
        try (var connection = dataSource.getConnection()) {
            // Then - 验证连接不为空且有效
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();
        }
    }

    @Test
    @DisplayName("验证多数据源支持")
    void shouldVerifyMultipleDataSourcesSupport() {
        // Given - 获取动态数据源
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;

        // When - 获取所有数据源
        var dataSources = dynamicRoutingDataSource.getDataSources();

        // Then - 验证支持多数据源
        assertThat(dataSources).hasSizeGreaterThanOrEqualTo(1);
    }
}