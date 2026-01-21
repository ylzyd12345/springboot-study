package com.junmo.platform.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法ID生成器
 * <p>
 * 雪花算法生成的ID是一个64位的long型数字，结构如下：
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <ul>
 *   <li>1位：符号位，始终为0</li>
 *   <li>41位：时间戳，可以使用69年</li>
 *   <li>10位：工作机器ID，支持1024个节点</li>
 *   <li>12位：序列号，每毫秒可生成4096个ID</li>
 * </ul>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
public class SnowflakeIdGenerator {

    /**
     * 起始时间戳（2024-01-01 00:00:00）
     */
    private static final long START_TIMESTAMP = 1704067200000L;

    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BIT = 12L;

    /**
     * 工作机器ID占用的位数
     */
    private static final long WORKER_ID_BIT = 10L;

    /**
     * 时间戳占用的位数
     */
    private static final long TIMESTAMP_BIT = 41L;

    /**
     * 工作机器ID最大值
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BIT);

    /**
     * 序列号最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 工作机器ID左移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BIT;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = WORKER_ID_BIT + SEQUENCE_BIT;

    /**
     * 工作机器ID
     */
    private final long workerId;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上一次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 单例实例
     */
    private static volatile SnowflakeIdGenerator instance;

    /**
     * 私有构造函数
     *
     * @param workerId 工作机器ID（0-1023）
     */
    private SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("工作机器ID范围: [0, %d]", MAX_WORKER_ID));
        }
        this.workerId = workerId;
        log.info("雪花算法ID生成器初始化完成，工作机器ID: {}", workerId);
    }

    /**
     * 获取单例实例
     *
     * @param workerId 工作机器ID（0-1023）
     * @return 雪花算法ID生成器实例
     */
    public static SnowflakeIdGenerator getInstance(long workerId) {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(workerId);
                }
            }
        }
        return instance;
    }

    /**
     * 获取默认实例（工作机器ID为0）
     *
     * @return 雪花算法ID生成器实例
     */
    public static SnowflakeIdGenerator getInstance() {
        return getInstance(0L);
    }

    /**
     * 生成下一个ID
     *
     * @return 生成的ID
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        // 时钟回拨检查
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(String.format("时钟回拨，拒绝生成ID。当前时间: %d, 上次时间: %d",
                    currentTimestamp, lastTimestamp));
        }

        // 同一毫秒内，序列号自增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        // 更新时间戳
        lastTimestamp = currentTimestamp;

        // 生成ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成下一个ID（字符串格式）
     *
     * @return 生成的ID（字符串）
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 生成批量ID
     *
     * @param count 批量数量
     * @return 生成的ID数组
     */
    public long[] nextIds(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("批量数量必须大于0");
        }
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = nextId();
        }
        return ids;
    }

    /**
     * 等待下一毫秒
     *
     * @param currentTimestamp 当前时间戳
     * @return 下一毫秒时间戳
     */
    private long waitNextMillis(long currentTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= currentTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳（毫秒）
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 解析ID的时间戳
     *
     * @param id 雪花算法ID
     * @return 生成ID的时间戳
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
    }

    /**
     * 解析ID的工作机器ID
     *
     * @param id 雪花算法ID
     * @return 工作机器ID
     */
    public static long parseWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 解析ID的序列号
     *
     * @param id 雪花算法ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 验证ID是否有效
     *
     * @param id 待验证的ID
     * @return true-有效，false-无效
     */
    public static boolean isValid(long id) {
        long timestamp = parseTimestamp(id);
        long workerId = parseWorkerId(id);
        long sequence = parseSequence(id);

        return timestamp >= START_TIMESTAMP
                && workerId >= 0 && workerId <= MAX_WORKER_ID
                && sequence >= 0 && sequence <= MAX_SEQUENCE;
    }
}