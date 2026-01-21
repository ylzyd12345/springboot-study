package com.junmo.platform.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Quartz 任务调度配置属性
 *
 * 功能说明：
 * 1. 配置Quartz调度器的基本参数
 * 2. 配置任务持久化相关参数
 * 3. 配置线程池参数
 *
 * 配置说明：
 * - enabled: 是否启用Quartz调度器
 * - instanceName: 调度器实例名称
 * - instanceId: 调度器实例ID（AUTO表示自动生成）
 * - jobStore: 任务存储配置
 *   - class: 任务存储类（JobStoreTX用于事务环境）
 *   - driverDelegateClass: 数据库驱动代理类
 *   - tablePrefix: 数据库表前缀
 *   - isClustered: 是否启用集群模式
 * - threadPool: 线程池配置
 *   - class: 线程池实现类
 *   - threadCount: 线程数量
 *
 * @author junmo
 * @version 1.0.0
 * @since 2025-12-24
 */
@Component
@ConfigurationProperties(prefix = "quartz")
public class QuartzProperties {

    /**
     * 是否启用Quartz调度器
     */
    private boolean enabled = true;

    /**
     * 调度器实例名称
     */
    private String instanceName = "QuartzScheduler";

    /**
     * 调度器实例ID
     * 可选值：AUTO（自动生成）、SYS_PROP（系统属性）、HOSTNAME（主机名）
     */
    private String instanceId = "AUTO";

    /**
     * 任务存储配置
     */
    private JobStore jobStore = new JobStore();

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public JobStore getJobStore() {
        return jobStore;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 任务存储配置
     */
    public static class JobStore {
        /**
         * 任务存储类
         * JobStoreTX: 用于事务环境
         * JobStoreCMT: 用于容器管理事务
         */
        private String jobStoreClass = "org.quartz.impl.jdbcjobstore.JobStoreTX";

        /**
         * 数据库驱动代理类
         * StdJDBCDelegate: 标准JDBC代理
         */
        private String driverDelegateClass = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";

        /**
         * 数据库表前缀
         */
        private String tablePrefix = "QRTZ_";

        /**
         * 是否启用集群模式
         */
        private boolean isClustered = false;

        /**
         * 集群检查间隔（毫秒）
         */
        private long clusterCheckinInterval = 20000L;

        /**
         * 是否使用属性
         */
        private boolean useProperties = false;

        public String getJobStoreClass() {
            return jobStoreClass;
        }

        public void setJobStoreClass(String jobStoreClass) {
            this.jobStoreClass = jobStoreClass;
        }

        public String getDriverDelegateClass() {
            return driverDelegateClass;
        }

        public void setDriverDelegateClass(String driverDelegateClass) {
            this.driverDelegateClass = driverDelegateClass;
        }

        public String getTablePrefix() {
            return tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public boolean isClustered() {
            return isClustered;
        }

        public void setClustered(boolean clustered) {
            isClustered = clustered;
        }

        public long getClusterCheckinInterval() {
            return clusterCheckinInterval;
        }

        public void setClusterCheckinInterval(long clusterCheckinInterval) {
            this.clusterCheckinInterval = clusterCheckinInterval;
        }

        public boolean isUseProperties() {
            return useProperties;
        }

        public void setUseProperties(boolean useProperties) {
            this.useProperties = useProperties;
        }
    }

    /**
     * 线程池配置
     */
    public static class ThreadPool {
        /**
         * 线程池实现类
         * SimpleThreadPool: 简单线程池
         */
        private String threadPoolClass = "org.quartz.simpl.SimpleThreadPool";

        /**
         * 线程数量
         */
        private int threadCount = 5;

        /**
         * 线程优先级
         * 可选值：1-10（1最低，10最高）
         */
        private int threadPriority = 5;

        /**
         * 是否继承加载上下文类加载器
         */
        private boolean inheritContextClassLoaderOfInitializingThread = true;

        public String getThreadPoolClass() {
            return threadPoolClass;
        }

        public void setThreadPoolClass(String threadPoolClass) {
            this.threadPoolClass = threadPoolClass;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public int getThreadPriority() {
            return threadPriority;
        }

        public void setThreadPriority(int threadPriority) {
            this.threadPriority = threadPriority;
        }

        public boolean isInheritContextClassLoaderOfInitializingThread() {
            return inheritContextClassLoaderOfInitializingThread;
        }

        public void setInheritContextClassLoaderOfInitializingThread(boolean inheritContextClassLoaderOfInitializingThread) {
            this.inheritContextClassLoaderOfInitializingThread = inheritContextClassLoaderOfInitializingThread;
        }
    }
}