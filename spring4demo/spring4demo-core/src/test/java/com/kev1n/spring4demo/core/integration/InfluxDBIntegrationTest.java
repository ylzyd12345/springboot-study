package com.kev1n.spring4demo.core.integration;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.query.QueryOptions;
import com.kev1n.spring4demo.test.config.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InfluxDB 集成测试
 * 测试 InfluxDB 3.0 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("InfluxDB 集成测试")
class InfluxDBIntegrationTest {

    private static final String DATABASE = "test";
    private static final String TOKEN = "admin:password"; // InfluxDB 3.0 使用 username:password 格式

    private InfluxDBClient influxDBClient;

    @BeforeEach
    void setUp() {
        log.info("初始化 InfluxDB 连接...");
        log.info("InfluxDB URL: {}", TestContainersConfig.getInfluxDbUrl());
        log.info("InfluxDB 主机: {}", TestContainersConfig.getInfluxDbHost());
        log.info("InfluxDB 端口: {}", TestContainersConfig.getInfluxDbPort());

        // 创建 InfluxDB 3.0 客户端 - 使用 getInstance API
        influxDBClient = InfluxDBClient.getInstance(
                TestContainersConfig.getInfluxDbUrl(),
                TOKEN.toCharArray(),
                DATABASE
        );

        log.info("InfluxDB 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        if (influxDBClient != null) {
            influxDBClient.close();
            log.info("InfluxDB 连接已关闭");
        }
    }

    @Test
    @DisplayName("应该成功连接到 InfluxDB 容器")
    void shouldConnectToInfluxDBContainer_whenContainerIsRunning() {
        // Given & When
        Boolean ping = influxDBClient.ping();

        // Then
        assertThat(ping).isTrue();
        log.info("InfluxDB 连接测试通过: {}", ping);
    }

    @Test
    @DisplayName("应该成功写入单点数据")
    void shouldWriteSinglePoint_whenPointIsProvided() {
        // Given
        Point point = Point.measurement("temperature")
                .addTag("location", "room1")
                .addField("value", 23.5)
                .setTimestamp(Instant.now());

        // When
        influxDBClient.writePoint(point);
        log.info("写入单点数据: location = room1, value = 23.5");

        // Then - 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证数据
        String sqlQuery = String.format(
                "SELECT * FROM temperature WHERE time > now() - 2m AND location = 'room1'",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);
        assertThat(results).isNotEmpty();
        log.info("写入单点数据测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功写入多个点数据")
    void shouldWriteMultiplePoints_whenMultiplePointsAreProvided() {
        // Given
        Instant now = Instant.now();
        Point point1 = Point.measurement("temperature")
                .addTag("location", "room1")
                .addField("value", 23.5)
                .setTimestamp(now);

        Point point2 = Point.measurement("temperature")
                .addTag("location", "room2")
                .addField("value", 24.0)
                .setTimestamp(now.plusSeconds(1));

        Point point3 = Point.measurement("temperature")
                .addTag("location", "room3")
                .addField("value", 22.8)
                .setTimestamp(now.plusSeconds(2));

        // When
        influxDBClient.writePoints(List.of(point1, point2, point3));
        log.info("写入多个点数据: 3 个温度数据点");

        // Then - 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证数据
        String sqlQuery = String.format(
                "SELECT * FROM temperature WHERE time > now() - 2m",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);
        assertThat(results).isNotEmpty();
        log.info("写入多个点数据测试通过: 写入 {} 个数据点", results.size());
    }

    @Test
    @DisplayName("应该成功查询时序数据")
    void shouldQueryTimeSeriesData_whenQueryIsExecuted() {
        // Given
        Instant now = Instant.now();
        for (int i = 0; i < 5; i++) {
            Point point = Point.measurement("temperature")
                    .addTag("location", "room1")
                    .addField("value", 20.0 + i)
                    .setTimestamp(now.plusSeconds(i * 10));
            influxDBClient.writePoint(point);
        }

        // 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        String sqlQuery = String.format(
                "SELECT * FROM temperature WHERE time > now() - 2m AND location = 'room1' ORDER BY time",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);

        // Then
        assertThat(results).isNotEmpty();
        log.info("查询时序数据测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功执行聚合查询")
    void shouldExecuteAggregationQuery_whenAggregationFunctionsAreUsed() {
        // Given
        Instant now = Instant.now();
        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("temperature")
                    .addTag("location", "room1")
                    .addField("value", 20.0 + i)
                    .setTimestamp(now.plusSeconds(i * 10));
            influxDBClient.writePoint(point);
        }

        // 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        String sqlQuery = String.format(
                "SELECT AVG(value) as avg_value FROM temperature WHERE time > now() - 2m AND location = 'room1'",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);

        // Then
        assertThat(results).isNotEmpty();
        log.info("聚合查询测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功执行时间范围查询")
    void shouldExecuteTimeRangeQuery_whenTimeRangeIsSpecified() {
        // Given
        Instant now = Instant.now();
        // 写入过去5分钟的数据
        for (int i = 0; i < 5; i++) {
            Point point = Point.measurement("temperature")
                    .addTag("location", "room1")
                    .addField("value", 20.0 + i)
                    .setTimestamp(now.minus(5, ChronoUnit.MINUTES).plusSeconds(i * 60));
            influxDBClient.writePoint(point);
        }

        // 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - 查询过去3分钟的数据
        String sqlQuery = String.format(
                "SELECT * FROM temperature WHERE time > now() - 3m",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);

        // Then
        log.info("时间范围查询测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功执行分组查询")
    void shouldExecuteGroupByQuery_whenGroupByTagIsSpecified() {
        // Given
        Instant now = Instant.now();
        // 写入不同位置的数据
        for (int i = 0; i < 3; i++) {
            Point point1 = Point.measurement("temperature")
                    .addTag("location", "room1")
                    .addField("value", 20.0 + i)
                    .setTimestamp(now.plusSeconds(i * 10));
            influxDBClient.writePoint(point1);

            Point point2 = Point.measurement("temperature")
                    .addTag("location", "room2")
                    .addField("value", 22.0 + i)
                    .setTimestamp(now.plusSeconds(i * 10));
            influxDBClient.writePoint(point2);
        }

        // 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        String sqlQuery = String.format(
                "SELECT location, AVG(value) as avg_value FROM temperature WHERE time > now() - 2m GROUP BY location",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);

        // Then
        assertThat(results).isNotEmpty();
        log.info("分组查询测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功写入不同类型的字段")
    void shouldWriteDifferentFieldTypes_whenVariousFieldTypesAreProvided() {
        // Given
        Point point = Point.measurement("sensor_data")
                .addTag("sensor", "sensor1")
                .addField("temperature", 23.5)
                .addField("humidity", 65)
                .addField("pressure", 1013.25)
                .addField("active", true)
                .setTimestamp(Instant.now());

        // When
        influxDBClient.writePoint(point);
        log.info("写入不同类型的字段: temperature=23.5, humidity=65, pressure=1013.25, active=true");

        // Then - 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证数据
        String sqlQuery = String.format(
                "SELECT * FROM sensor_data WHERE time > now() - 2m",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);
        assertThat(results).isNotEmpty();
        log.info("写入不同类型的字段测试通过: 查询返回 {} 条记录", results.size());
    }

    @Test
    @DisplayName("应该成功执行批量写入")
    void shouldWriteBatch_whenMultiplePointsAreBatched() {
        // Given
        Instant now = Instant.now();
        List<Point> points = List.of(
                Point.measurement("temperature").addTag("location", "room1").addField("value", 23.5).setTimestamp(now),
                Point.measurement("temperature").addTag("location", "room2").addField("value", 24.0).setTimestamp(now.plusSeconds(1)),
                Point.measurement("temperature").addTag("location", "room3").addField("value", 22.8).setTimestamp(now.plusSeconds(2)),
                Point.measurement("temperature").addTag("location", "room4").addField("value", 23.2).setTimestamp(now.plusSeconds(3)),
                Point.measurement("temperature").addTag("location", "room5").addField("value", 24.5).setTimestamp(now.plusSeconds(4))
        );

        // When
        influxDBClient.writePoints(points);
        log.info("批量写入: {} 个数据点", points.size());

        // Then - 等待写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证数据
        String sqlQuery = String.format(
                "SELECT * FROM temperature WHERE time > now() - 2m",
                DATABASE
        );

        List<Object> results = influxDBClient.query(sqlQuery);
        assertThat(results).isNotEmpty();
        log.info("批量写入测试通过: 查询返回 {} 条记录", results.size());
    }
}
