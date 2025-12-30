package com.kev1n.spring4demo.core.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Seata 分布式事务配置类
 *
 * <p>配置 Seata 数据源代理，支持 AT、TCC、SAGA、XA 等事务模式。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "seata", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SeataConfig {

    /**
     * 配置 Seata 数据源代理
     *
     * <p>将动态数据源包装为 Seata 数据源代理，以支持分布式事务。</p>
     *
     * @param dataSource 原始数据源
     * @return Seata 数据源代理
     */
    @Bean
    public DataSource seataDataSource(DataSource dataSource) {
        if (dataSource instanceof DynamicRoutingDataSource) {
            log.info("Seata 数据源代理配置成功，使用动态数据源");
            // Dynamic Datasource 已经支持 Seata，不需要额外代理
            return dataSource;
        } else {
            DataSourceProxy dataSourceProxy = new DataSourceProxy(dataSource);
            log.info("Seata 数据源代理配置成功");
            return dataSourceProxy;
        }
    }
}