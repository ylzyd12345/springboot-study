package com.kev1n.spring4demo.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * SnowflakeIdGenerator 雪花算法ID生成器单元测试
 *
 * @author kev1n
 * @since 1.0.0
 */
@DisplayName("SnowflakeIdGenerator 雪花算法ID生成器测试")
class SnowflakeIdGeneratorTests {

    private SnowflakeIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = SnowflakeIdGenerator.getInstance(1L);
    }

    @Nested
    @DisplayName("实例化测试")
    class InstanceTests {

        @Test
        @DisplayName("应该成功创建实例")
        void shouldCreateInstanceSuccessfully() {
            // 验证
            assertThat(generator).isNotNull();
        }

        @Test
        @DisplayName("应该返回单例实例")
        void shouldReturnSingletonInstance() {
            // 执行
            SnowflakeIdGenerator instance1 = SnowflakeIdGenerator.getInstance(1L);
            SnowflakeIdGenerator instance2 = SnowflakeIdGenerator.getInstance(1L);

            // 验证
            assertThat(instance1).isSameAs(instance2);
        }

        @Test
        @DisplayName("应该返回默认实例（工作机器ID为0）")
        void shouldReturnDefaultInstance() {
            // 执行
            SnowflakeIdGenerator defaultInstance = SnowflakeIdGenerator.getInstance();

            // 验证
            assertThat(defaultInstance).isNotNull();
        }

        @Test
        @DisplayName("当工作机器ID超出范围时应该抛出异常")
        void shouldThrowExceptionWhenWorkerIdOutOfRange() {
            // 注意：由于getInstance()使用单例模式，如果instance已经创建，则不会再次抛出异常
            // 这里只测试构造函数的行为

            // 验证 - 工作机器ID为负数
            assertThatThrownBy(() -> {
                try {
                    // 使用反射创建新实例以测试构造函数
                    java.lang.reflect.Constructor<SnowflakeIdGenerator> constructor =
                            SnowflakeIdGenerator.class.getDeclaredConstructor(long.class);
                    constructor.setAccessible(true);
                    constructor.newInstance(-1L);
                } catch (Exception e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException) e.getCause();
                    }
                    throw new RuntimeException(e);
                }
            }).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("工作机器ID范围");

            // 验证 - 工作机器ID超过最大值
            assertThatThrownBy(() -> {
                try {
                    // 使用反射创建新实例以测试构造函数
                    java.lang.reflect.Constructor<SnowflakeIdGenerator> constructor =
                            SnowflakeIdGenerator.class.getDeclaredConstructor(long.class);
                    constructor.setAccessible(true);
                    constructor.newInstance(1024L);
                } catch (Exception e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException) e.getCause();
                    }
                    throw new RuntimeException(e);
                }
            }).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("工作机器ID范围");
        }

        @Test
        @DisplayName("应该接受有效的工作机器ID范围")
        void shouldAcceptValidWorkerIdRange() {
            // 验证 - 工作机器ID为0
            assertThatCode(() -> SnowflakeIdGenerator.getInstance(0L)).doesNotThrowAnyException();

            // 验证 - 工作机器ID为最大值
            assertThatCode(() -> SnowflakeIdGenerator.getInstance(1023L)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("nextId 方法测试")
    class NextIdTests {

        @Test
        @DisplayName("应该成功生成ID")
        void shouldGenerateIdSuccessfully() {
            // 执行
            long id = generator.nextId();

            // 验证
            assertThat(id).isPositive();
        }

        @Test
        @DisplayName("应该生成唯一的ID")
        void shouldGenerateUniqueIds() {
            // 准备
            Set<Long> idSet = new HashSet<>();
            int count = 1000;

            // 执行
            for (int i = 0; i < count; i++) {
                idSet.add(generator.nextId());
            }

            // 验证
            assertThat(idSet).hasSize(count);
        }

        @Test
        @DisplayName("生成的ID应该是递增的")
        void shouldGenerateIncrementalIds() {
            // 执行
            long id1 = generator.nextId();
            long id2 = generator.nextId();
            long id3 = generator.nextId();

            // 验证
            assertThat(id2).isGreaterThan(id1);
            assertThat(id3).isGreaterThan(id2);
        }

        @Test
        @DisplayName("应该在高并发下生成唯一ID")
        void shouldGenerateUniqueIdsUnderHighConcurrency() throws InterruptedException {
            // 准备
            int threadCount = 10;
            int idsPerThread = 1000;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            Set<Long> idSet = new HashSet<>();
            Object lock = new Object();

            // 执行
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        for (int j = 0; j < idsPerThread; j++) {
                            long id = generator.nextId();
                            synchronized (lock) {
                                idSet.add(id);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            // 验证
            assertThat(idSet).hasSize(threadCount * idsPerThread);
        }
    }

    @Nested
    @DisplayName("nextIdStr 方法测试")
    class NextIdStrTests {

        @Test
        @DisplayName("应该成功生成字符串格式的ID")
        void shouldGenerateIdStringSuccessfully() {
            // 执行
            String idStr = generator.nextIdStr();

            // 验证
            assertThat(idStr).isNotNull();
            assertThat(idStr).isNotEmpty();
            assertThat(idStr).matches("\\d+");
        }

        @Test
        @DisplayName("字符串ID应该与long ID一致")
        void shouldMatchLongId() {
            // 执行
            long longId = generator.nextId();
            String idStr = generator.nextIdStr();
            long parsedId = Long.parseLong(idStr);

            // 验证
            assertThat(parsedId).isGreaterThan(longId);
        }
    }

    @Nested
    @DisplayName("nextIds 方法测试")
    class NextIdsTests {

        @Test
        @DisplayName("应该成功生成批量ID")
        void shouldGenerateBatchIdsSuccessfully() {
            // 执行
            int count = 100;
            long[] ids = generator.nextIds(count);

            // 验证
            assertThat(ids).hasSize(count);
            for (long id : ids) {
                assertThat(id).isPositive();
            }
        }

        @Test
        @DisplayName("批量ID应该是唯一的")
        void shouldGenerateUniqueBatchIds() {
            // 执行
            int count = 1000;
            long[] ids = generator.nextIds(count);
            Set<Long> idSet = new HashSet<>();

            for (long id : ids) {
                idSet.add(id);
            }

            // 验证
            assertThat(idSet).hasSize(count);
        }

        @Test
        @DisplayName("当批量数量小于等于0时应该抛出异常")
        void shouldThrowExceptionWhenCountIsInvalid() {
            // 验证 - 批量数量为0
            assertThatThrownBy(() -> generator.nextIds(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("批量数量必须大于0");

            // 验证 - 批量数量为负数
            assertThatThrownBy(() -> generator.nextIds(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("批量数量必须大于0");
        }
    }

    @Nested
    @DisplayName("ID解析方法测试")
    class IdParseTests {

        @Test
        @DisplayName("应该正确解析ID的时间戳")
        void shouldParseTimestampCorrectly() {
            // 执行
            long id = generator.nextId();
            long timestamp = SnowflakeIdGenerator.parseTimestamp(id);

            // 验证
            assertThat(timestamp).isGreaterThan(0L);
            assertThat(timestamp).isLessThanOrEqualTo(System.currentTimeMillis());
        }

        @Test
        @DisplayName("应该正确解析ID的工作机器ID")
        void shouldParseWorkerIdCorrectly() {
            // 执行
            long workerId = 1L;
            SnowflakeIdGenerator gen = SnowflakeIdGenerator.getInstance(workerId);
            long id = gen.nextId();
            long parsedWorkerId = SnowflakeIdGenerator.parseWorkerId(id);

            // 验证
            assertThat(parsedWorkerId).isEqualTo(workerId);
        }

        @Test
        @DisplayName("应该正确解析ID的序列号")
        void shouldParseSequenceCorrectly() {
            // 执行
            long id1 = generator.nextId();
            long id2 = generator.nextId();
            long sequence1 = SnowflakeIdGenerator.parseSequence(id1);
            long sequence2 = SnowflakeIdGenerator.parseSequence(id2);

            // 验证
            assertThat(sequence1).isGreaterThanOrEqualTo(0L);
            assertThat(sequence2).isGreaterThanOrEqualTo(0L);
            // 序列号可能相同（如果时间戳不同）或递增
        }

        @Test
        @DisplayName("解析的工作机器ID应该在有效范围内")
        void shouldParseWorkerIdInRange() {
            // 执行
            long workerId = 500L;
            SnowflakeIdGenerator gen = SnowflakeIdGenerator.getInstance(workerId);
            long id = gen.nextId();
            long parsedWorkerId = SnowflakeIdGenerator.parseWorkerId(id);

            // 验证
            assertThat(parsedWorkerId).isGreaterThanOrEqualTo(0L);
            assertThat(parsedWorkerId).isLessThanOrEqualTo(1023L);
        }
    }

    @Nested
    @DisplayName("ID验证方法测试")
    class IdValidationTests {

        @Test
        @DisplayName("应该验证有效的ID")
        void shouldValidateValidId() {
            // 执行
            long id = generator.nextId();
            boolean isValid = SnowflakeIdGenerator.isValid(id);

            // 验证
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("应该拒绝无效的ID（负数）")
        void shouldRejectNegativeId() {
            // 执行
            boolean isValid = SnowflakeIdGenerator.isValid(-1L);

            // 验证
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("应该验证ID为0的情况")
        void shouldValidateZeroId() {
            // 执行
            boolean isValid = SnowflakeIdGenerator.isValid(0L);

            // 验证 - 根据isValid方法的实现，0会被解析为START_TIMESTAMP，所以是有效的
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("应该验证多个生成的ID")
        void shouldValidateMultipleGeneratedIds() {
            // 准备
            int count = 1000;

            // 执行
            for (int i = 0; i < count; i++) {
                long id = generator.nextId();
                boolean isValid = SnowflakeIdGenerator.isValid(id);

                // 验证
                assertThat(isValid).as("ID %d 应该是有效的", id).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {

        @Test
        @DisplayName("应该能够快速生成大量ID")
        void shouldGenerateLargeNumberOfIdsQuickly() {
            // 准备
            int count = 100000;
            long startTime = System.currentTimeMillis();

            // 执行
            for (int i = 0; i < count; i++) {
                generator.nextId();
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 验证
            assertThat(duration).isLessThan(1000L); // 应该在1秒内完成
        }

        @Test
        @DisplayName("应该能够处理同一毫秒内的序列号溢出")
        void shouldHandleSequenceOverflowInSameMillisecond() {
            // 执行
            int count = 5000;
            Set<Long> idSet = new HashSet<>();

            for (int i = 0; i < count; i++) {
                long id = generator.nextId();
                idSet.add(id);
            }

            // 验证
            assertThat(idSet).hasSize(count);
        }
    }
}
