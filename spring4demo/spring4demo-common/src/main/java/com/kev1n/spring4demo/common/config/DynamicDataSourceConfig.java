package com.kev1n.spring4demo.common.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 动态数据源配置类
 *
 * <p>支持多数据源配置，包括主从数据源、读写分离等场景。</p>
 * <p>Dynamic Datasource 框架已提供自动配置，此类主要用于日志记录和自定义扩展。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig {

    private final DataSource dataSource;

    public DynamicDataSourceConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 初始化动态数据源
     *
     * <p>在应用启动后记录动态数据源的配置信息。</p>
     */
    @PostConstruct
    public void init() {
        if (dataSource instanceof DynamicRoutingDataSource) {
            DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
            log.info("动态数据源初始化成功，数据源：{}", dynamicRoutingDataSource.getDataSources());
        } else {
            log.info("使用默认数据源，未启用动态数据源");
        }
    }
}