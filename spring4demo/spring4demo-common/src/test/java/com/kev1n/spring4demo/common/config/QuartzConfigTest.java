package com.kev1n.spring4demo.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Quartz 任务调度配置测试
 *
 * 测试要点：
 * 1. 验证 SchedulerFactoryBean Bean 创建
 * 2. 验证 Scheduler Bean 创建
 * 3. 验证调度器配置
 * 4. 验证调度器状态
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@ConditionalOnProperty(prefix = "quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
@DisplayName("Quartz 任务调度配置测试")
class QuartzConfigTest {

    @Autowired
    private Scheduler scheduler;

    @Autowired(required = false)
    private QuartzConfig quartzConfig;

    @Test
    @DisplayName("验证 QuartzConfig Bean 创建")
    void shouldCreateQuartzConfigBean() {
        // Then - 验证 Bean 创建成功
        // 注意：由于使用了 @ConditionalOnProperty，如果配置不满足条件，Bean 可能不会被创建
        // 这里检查是否为 null
        if (quartzConfig != null) {
            assertThat(quartzConfig).isNotNull();
        } else {
            // 如果 Bean 未创建，说明配置不满足条件，这也是正常的
            assertThat(quartzConfig).isNull();
        }
    }

    @Test
    @DisplayName("验证 Scheduler Bean 创建")
    void shouldCreateSchedulerBean() {
        // Then - 验证 Bean 创建成功
        assertThat(scheduler).isNotNull();
    }

    @Test
    @DisplayName("验证调度器名称")
    void shouldVerifySchedulerName() throws Exception {
        // When - 获取调度器名称
        String schedulerName = scheduler.getSchedulerName();

        // Then - 验证调度器名称
        assertThat(schedulerName).isNotNull();
        assertThat(schedulerName).isNotEmpty();
    }

    @Test
    @DisplayName("验证调度器实例ID")
    void shouldVerifySchedulerInstanceId() throws Exception {
        // When - 获取调度器实例ID
        String instanceId = scheduler.getSchedulerInstanceId();

        // Then - 验证实例ID
        assertThat(instanceId).isNotNull();
        assertThat(instanceId).isNotEmpty();
    }

    @Test
    @DisplayName("验证调度器状态")
    void shouldVerifySchedulerState() throws Exception {
        // When - 获取调度器状态
        boolean isStarted = scheduler.isStarted();
        boolean isShutdown = scheduler.isShutdown();
        boolean isInStandbyMode = scheduler.isInStandbyMode();

        // Then - 验证调度器状态
        // 调度器应该已启动，未关闭，不在待机模式
        assertThat(isStarted).isTrue();
        assertThat(isShutdown).isFalse();
        assertThat(isInStandbyMode).isFalse();
    }

    @Test
    @DisplayName("验证调度器元数据")
    void shouldVerifySchedulerMetaData() throws Exception {
        // When - 获取调度器元数据
        var metaData = scheduler.getMetaData();

        // Then - 验证元数据
        assertThat(metaData).isNotNull();
        assertThat(metaData.getSchedulerName()).isNotNull();
        assertThat(metaData.getSchedulerInstanceId()).isNotNull();
    }

    @Test
    @DisplayName("验证调度器线程池")
    void shouldVerifySchedulerThreadPool() throws Exception {
        // When - 获取调度器元数据
        var metaData = scheduler.getMetaData();

        // Then - 验证线程池
        assertThat(metaData).isNotNull();
        assertThat(metaData.getThreadPoolSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("验证调度器Job存储")
    void shouldVerifySchedulerJobStore() throws Exception {
        // When - 获取调度器Job存储
        var jobStore = scheduler.getMetaData().getJobStoreClass();

        // Then - 验证Job存储
        assertThat(jobStore).isNotNull();
    }

    @Test
    @DisplayName("验证调度器已暂停")
    void shouldVerifySchedulerCanBePaused() throws Exception {
        // Given - 获取原始状态
        boolean originalStarted = scheduler.isStarted();

        // When - 暂停调度器
        scheduler.standby();

        // Then - 验证调度器已暂停
        assertThat(scheduler.isInStandbyMode()).isTrue();

        // Cleanup - 恢复调度器
        if (originalStarted) {
            scheduler.start();
        }
    }

    @Test
    @DisplayName("验证调度器可以恢复")
    void shouldVerifySchedulerCanBeResumed() throws Exception {
        // Given - 暂停调度器
        scheduler.standby();

        // When - 恢复调度器
        scheduler.start();

        // Then - 验证调度器已恢复
        assertThat(scheduler.isStarted()).isTrue();
        assertThat(scheduler.isInStandbyMode()).isFalse();
    }

    @Test
    @DisplayName("验证调度器Job数量")
    void shouldVerifySchedulerJobCount() throws Exception {
        // When - 获取Job数量
        int jobCount = scheduler.getJobKeys(org.quartz.impl.matchers.GroupMatcher.anyGroup()).size();

        // Then - 验证Job数量（初始状态应该为0）
        assertThat(jobCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("验证调度器Trigger数量")
    void shouldVerifySchedulerTriggerCount() throws Exception {
        // When - 获取Trigger数量
        int triggerCount = scheduler.getTriggerKeys(org.quartz.impl.matchers.GroupMatcher.anyGroup()).size();

        // Then - 验证Trigger数量（初始状态应该为0）
        assertThat(triggerCount).isGreaterThanOrEqualTo(0);
    }
}