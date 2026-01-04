package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Quartz 配置属性测试
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
@EnableConfigurationProperties(QuartzProperties.class)
@TestPropertySource(properties = {
    "quartz.enabled=false",
    "quartz.instance-name=CustomScheduler",
    "quartz.instance-id=SYS_PROP",
    "quartz.job-store.job-store-class=org.quartz.impl.jdbcjobstore.JobStoreTX",
    "quartz.job-store.driver-delegate-class=org.quartz.impl.jdbcjobstore.StdJDBCDelegate",
    "quartz.job-store.table-prefix=QRTZ2_",
    "quartz.job-store.is-clustered=true",
    "quartz.job-store.cluster-checkin-interval=30000",
    "quartz.job-store.use-properties=true",
    "quartz.thread-pool.thread-pool-class=org.quartz.simpl.SimpleThreadPool",
    "quartz.thread-pool.thread-count=10",
    "quartz.thread-pool.thread-priority=8",
    "quartz.thread-pool.inherit-context-class-loader-of-initializing-thread=false"
})
@DisplayName("Quartz 配置属性测试")
class QuartzPropertiesTest {

    @Autowired
    private QuartzProperties properties;

    @Test
    @DisplayName("应该正确注入QuartzProperties Bean")
    void shouldInjectQuartzProperties() {
        assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该正确配置enabled")
    void shouldConfigureEnabled() {
        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("应该正确配置instanceName")
    void shouldConfigureInstanceName() {
        assertThat(properties.getInstanceName()).isEqualTo("CustomScheduler");
    }

    @Test
    @DisplayName("应该正确配置instanceId")
    void shouldConfigureInstanceId() {
        assertThat(properties.getInstanceId()).isEqualTo("SYS_PROP");
    }

    @Test
    @DisplayName("应该正确配置jobStore")
    void shouldConfigureJobStore() {
        QuartzProperties.JobStore jobStore = properties.getJobStore();
        
        assertThat(jobStore).isNotNull();
        assertThat(jobStore.getJobStoreClass())
                .isEqualTo("org.quartz.impl.jdbcjobstore.JobStoreTX");
        assertThat(jobStore.getDriverDelegateClass())
                .isEqualTo("org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        assertThat(jobStore.getTablePrefix())
                .isEqualTo("QRTZ2_");
        assertThat(jobStore.isClustered())
                .isTrue();
        assertThat(jobStore.getClusterCheckinInterval())
                .isEqualTo(30000L);
        assertThat(jobStore.isUseProperties())
                .isTrue();
    }

    @Test
    @DisplayName("应该正确配置threadPool")
    void shouldConfigureThreadPool() {
        QuartzProperties.ThreadPool threadPool = properties.getThreadPool();
        
        assertThat(threadPool).isNotNull();
        assertThat(threadPool.getThreadPoolClass())
                .isEqualTo("org.quartz.simpl.SimpleThreadPool");
        assertThat(threadPool.getThreadCount())
                .isEqualTo(10);
        assertThat(threadPool.getThreadPriority())
                .isEqualTo(8);
        assertThat(threadPool.isInheritContextClassLoaderOfInitializingThread())
                .isFalse();
    }

    @DisplayName("应该使用默认值当配置未设置")
    @EnableConfigurationProperties(QuartzProperties.class)
    class DefaultValuesTest {

        @Autowired
        private QuartzProperties properties;

        @Test
        @DisplayName("enabled应该使用默认值")
        void enabledShouldUseDefaultValue() {
            assertThat(properties.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("instanceName应该使用默认值")
        void instanceNameShouldUseDefaultValue() {
            assertThat(properties.getInstanceName()).isEqualTo("QuartzScheduler");
        }

        @Test
        @DisplayName("instanceId应该使用默认值")
        void instanceIdShouldUseDefaultValue() {
            assertThat(properties.getInstanceId()).isEqualTo("AUTO");
        }

        @Test
        @DisplayName("jobStore应该使用默认值")
        void jobStoreShouldUseDefaultValue() {
            QuartzProperties.JobStore jobStore = properties.getJobStore();
            
            assertThat(jobStore.getJobStoreClass())
                    .isEqualTo("org.quartz.simpl.RAMJobStore");
            assertThat(jobStore.getDriverDelegateClass())
                    .isEqualTo("org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            assertThat(jobStore.getTablePrefix())
                    .isEqualTo("QRTZ_");
            assertThat(jobStore.isClustered())
                    .isFalse();
            assertThat(jobStore.getClusterCheckinInterval())
                    .isEqualTo(20000L);
            assertThat(jobStore.isUseProperties())
                    .isFalse();
        }

        @Test
        @DisplayName("threadPool应该使用默认值")
        void threadPoolShouldUseDefaultValue() {
            QuartzProperties.ThreadPool threadPool = properties.getThreadPool();
            
            assertThat(threadPool.getThreadPoolClass())
                    .isEqualTo("org.quartz.simpl.SimpleThreadPool");
            assertThat(threadPool.getThreadCount())
                    .isEqualTo(5);
            assertThat(threadPool.getThreadPriority())
                    .isEqualTo(5);
            assertThat(threadPool.isInheritContextClassLoaderOfInitializingThread())
                    .isTrue();
        }
    }
}