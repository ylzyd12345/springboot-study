package com.kev1n.spring4demo.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * 动态数据源配置属性
 *
 * <p>支持多数据源配置，包括主从数据源、读写分离等场景。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "spring.datasource.dynamic")
public class DynamicDataSourceProperties {

    /**
     * 是否启用动态数据源
     */
    private boolean enabled = true;

    /**
     * 严格模式，严格匹配数据源名称
     */
    private boolean strict = false;

    /**
     * 数据源配置
     */
    private Map<String, DataSourceConfig> datasource;

    /**
     * 数据源配置
     */
    @Data
    public static class DataSourceConfig {
        /**
         * 数据源名称
         */
        private String name;

        /**
         * 驱动类名称
         */
        private String driverClassName;

        /**
         * JDBC URL
         */
        private String url;

        /**
         * 数据库用户名
         */
        private String username;

        /**
         * 数据库密码
         */
        private String password;

        /**
         * 数据源类型
         */
        private String type = "com.alibaba.druid.pool.DruidDataSource";

        /**
         * Druid 连接池配置
         */
        private DruidConfig druid = new DruidConfig();

        /**
         * HikariCP 连接池配置
         */
        private HikariConfig hikari = new HikariConfig();

        /**
         * 是否作为主数据源
         */
        private boolean primary = false;

        /**
         * 是否作为只读数据源
         */
        private boolean readonly = false;
    }

    /**
     * Druid 连接池配置
     */
    @Data
    public static class DruidConfig {
        /**
         * 初始化连接数
         */
        private int initialSize = 5;

        /**
         * 最小空闲连接数
         */
        private int minIdle = 5;

        /**
         * 最大活跃连接数
         */
        private int maxActive = 20;

        /**
         * 获取连接超时时间（毫秒）
         */
        private int maxWait = 60000;

        /**
         * 配置间隔多久进行一次检测，检测需要关闭的空闲连接（毫秒）
         */
        private long timeBetweenEvictionRunsMillis = 60000;

        /**
         * 配置连接在池中最小生存的时间（毫秒）
         */
        private long minEvictableIdleTimeMillis = 300000;

        /**
         * 配置连接在池中最大生存的时间（毫秒）
         */
        private long maxEvictableIdleTimeMillis = 900000;

        /**
         * 测试连接是否有效的 SQL
         */
        private String validationQuery = "SELECT 1";

        /**
         * 申请连接时执行 validationQuery 检测连接是否有效
         */
        private boolean testWhileIdle = true;

        /**
         * 申请连接时执行 validationQuery 检测连接是否有效
         */
        private boolean testOnBorrow = false;

        /**
         * 归还连接时执行 validationQuery 检测连接是否有效
         */
        private boolean testOnReturn = false;

        /**
         * 是否缓存 preparedStatement
         */
        private boolean poolPreparedStatements = true;

        /**
         * 缓存 preparedStatement 的最大数量
         */
        private int maxPoolPreparedStatementPerConnectionSize = 20;

        /**
         * 配置监控统计拦截的 filters
         */
        private String filters = "stat,wall";

        /**
         * 通过 connectProperties 属性来打开 mergeSql 功能
         */
        private String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";

        /**
         * 是否启用监控统计
         */
        private boolean statViewServletEnabled = true;

        /**
         * 监控统计访问路径
         */
        private String statViewServletUrlPattern = "/druid/*";

        /**
         * 监控统计登录用户名
         */
        private String statViewServletLoginUsername = "admin";

        /**
         * 监控统计登录密码
         */
        private String statViewServletLoginPassword = "admin";

        /**
         * 是否启用 Web-JDBC 关联监控
         */
        private boolean webStatFilterEnabled = true;

        /**
         * Web-JDBC 关联监控拦截路径
         */
        private String webStatFilterUrlPattern = "/*";

        /**
         * Web-JDBC 关联监控排除路径
         */
        private String webStatFilterExclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";
    }

    /**
     * HikariCP 连接池配置
     */
    @Data
    public static class HikariConfig {
        /**
         * 最小空闲连接数
         */
        private int minimumIdle = 5;

        /**
         * 最大连接数
         */
        private int maximumPoolSize = 20;

        /**
         * 连接超时时间（毫秒）
         */
        private long connectionTimeout = 30000;

        /**
         * 空闲连接超时时间（毫秒）
         */
        private long idleTimeout = 600000;

        /**
         * 连接最大生命周期（毫秒）
         */
        private long maxLifetime = 1800000;

        /**
         * 连接测试查询
         */
        private String connectionTestQuery = "SELECT 1";

        /**
         * 连接池名称
         */
        private String poolName = "Spring4DemoHikariCP";
    }
}