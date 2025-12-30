package com.kev1n.spring4demo.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Seata 分布式事务配置属性
 *
 * <p>用于配置 Seata 分布式事务的连接参数，包括 AT、TCC、SAGA、XA 等事务模式。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "seata")
public class CustomSeataProperties {

    /**
     * 是否启用 Seata
     */
    private boolean enabled = true;

    /**
     * 应用名称
     */
    @NotBlank(message = "Seata 应用名称不能为空")
    private String applicationName = "spring4demo";

    /**
     * 事务组名称
     */
    private String txServiceGroup = "spring4demo-tx-group";

    /**
     * 服务配置
     */
    private Service service = new Service();

    /**
     * 客户端配置
     */
    private Client client = new Client();

    /**
     * 传输配置
     */
    private Transport transport = new Transport();

    /**
     * 配置中心配置
     */
    private Config config = new Config();

    /**
     * 注册中心配置
     */
    private Registry registry = new Registry();

    /**
     * 日志配置
     */
    private Log log = new Log();

    /**
     * 服务配置
     */
    @Data
    public static class Service {
        /**
         * 虚拟组和分组的映射
         */
        private VgroupMapping vgroupMapping = new VgroupMapping();

        /**
         * 失败重试间隔（毫秒）
         */
        private int grouplistRetryInterval = 2000;

        /**
         * 是否启用降级
         */
        private boolean enableDegrade = false;

        /**
         * 禁用全局事务
         */
        private boolean disableGlobalTransaction = false;

        /**
         * 虚拟组映射
         */
        @Data
        public static class VgroupMapping {
            /**
             * 默认事务组
             */
            private String defaultTxGroup = "default";
        }
    }

    /**
     * 客户端配置
     */
    @Data
    public static class Client {
        /**
         * RM 配置
         */
        private RM rm = new RM();

        /**
         * TM 配置
         */
        private TM tm = new TM();

        /**
         * undo 配置
         */
        private Undo undo = new Undo();

        /**
         * RM 配置
         */
        @Data
        public static class RM {
            /**
             * 异步提交缓存队列
             */
            private int asyncCommitBufferLimit = 10000;

            /**
             * 检查报告间隔（毫秒）
             */
            private int lockRetryInterval = 10;

            /**
             * 检查报告次数
             */
            private int lockRetryTimes = 30;

            /**
             * 锁重试策略
             */
            private String lockRetryPolicy = "backoff";

            /**
             * 查询超时时间（毫秒）
             */
            private int queryTimeout = 1000;

            /**
             * 提交重试次数
             */
            private int commitRetryCount = 5;

            /**
             * 回滚重试次数
             */
            private int rollbackRetryCount = 5;

            /**
             * 表元数据检查
             */
            private boolean tableMetaCheckEnable = true;
        }

        /**
         * TM 配置
         */
        @Data
        public static class TM {
            /**
             * 提交重试次数
             */
            private int commitRetryCount = 5;

            /**
             * 回滚重试次数
             */
            private int rollbackRetryCount = 5;

            /**
             * 全局锁定超时时间（毫秒）
             */
            private int globalLockRetryInterval = 10;

            /**
             * 全局锁定重试次数
             */
            private int globalLockRetryTimes = 30;
        }

        /**
         * undo 配置
         */
        @Data
        public static class Undo {
            /**
             * 是否启用 undo 日志
             */
            private boolean enable = true;

            /**
             * 数据校验
             */
            private boolean dataValidation = true;

            /**
             * 日志序列化
             */
            private String logSerialization = "jackson";

            /**
             * 只保存更新
             */
            private boolean onlyCareUpdateColumns = true;
        }
    }

    /**
     * 传输配置
     */
    @Data
    public static class Transport {
        /**
         * TCP 传输
         */
        private Tcp tcp = new Tcp();

        /**
         * TCC 传输
         */
        private Tcc tcc = new Tcc();

        /**
         * TCP 配置
         */
        @Data
        public static class Tcp {
            /**
             * 启用
             */
            private boolean enabled = true;

            /**
             * 名称
             */
            private String name = "TCP";

            /**
             * 线程工厂
             */
            private String threadFactory = "NettyBossSelector";

            /**
             * Boss 线程数
             */
            private int bossThreadSize = 1;

            /**
             * Worker 线程数
             */
            private int workerThreadSize = 1;

            /**
             * 关闭等待时间（秒）
             */
            private int shutdownWait = 3;
        }

        /**
         * TCC 配置
         */
        @Data
        public static class Tcc {
            /**
             * 线程工厂
             */
            private String threadFactory = "NettyBossSelector";

            /**
             * Boss 线程数
             */
            private int bossThreadSize = 1;

            /**
             * Worker 线程数
             */
            private int workerThreadSize = 1;

            /**
             * 关闭等待时间（秒）
             */
            private int shutdownWait = 3;
        }
    }

    /**
     * 配置中心配置
     */
    @Data
    public static class Config {
        /**
         * 类型（file、nacos、apollo、zk、consul、etcd3、springCloudConfig）
         */
        private String type = "file";

        /**
         * 文件配置
         */
        private File file = new File();

        /**
         * Nacos 配置
         */
        private Nacos nacos = new Nacos();

        /**
         * 文件配置
         */
        @Data
        public static class File {
            /**
             * 名称
             */
            private String name = "file.conf";
        }

        /**
         * Nacos 配置
         */
        @Data
        public static class Nacos {
            /**
             * 服务器地址
             */
            private String serverAddr = "localhost:8848";

            /**
             * 命名空间
             */
            private String namespace;

            /**
             * 组
             */
            private String group = "SEATA_GROUP";

            /**
             * 用户名
             */
            private String username;

            /**
             * 密码
             */
            private String password;
        }
    }

    /**
     * 注册中心配置
     */
    @Data
    public static class Registry {
        /**
         * 类型（file、nacos、eureka、redis、zk、consul、etcd3、sofa）
         */
        private String type = "file";

        /**
         * 文件配置
         */
        private File file = new File();

        /**
         * Nacos 配置
         */
        private Nacos nacos = new Nacos();

        /**
         * 文件配置
         */
        @Data
        public static class File {
            /**
             * 名称
             */
            private String name = "file.conf";
        }

        /**
         * Nacos 配置
         */
        @Data
        public static class Nacos {
            /**
             * 服务器地址
             */
            private String serverAddr = "localhost:8848";

            /**
             * 命名空间
             */
            private String namespace;

            /**
             * 组
             */
            private String group = "SEATA_GROUP";

            /**
             * 用户名
             */
            private String username;

            /**
             * 密码
             */
            private String password;

            /**
             * 应用
             */
            private String application = "seata-server";
        }
    }

    /**
     * 日志配置
     */
    @Data
    public static class Log {
        /**
         * 异常率
         */
        private int exceptionRate = 100;
    }
}