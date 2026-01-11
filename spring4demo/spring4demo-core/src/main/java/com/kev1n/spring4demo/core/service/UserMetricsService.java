package com.kev1n.spring4demo.core.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.kev1n.spring4demo.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 用户指标时序数据服务
 *
 * <p>提供用户相关指标的时序数据写入和查询功能，支持用户登录、操作等行为的数据收集和分析。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMetricsService {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.org:spring4demo}")
    private String org;

    @Value("${influxdb.bucket:spring4demo}")
    private String bucket;

    /**
     * 记录用户登录指标
     *
     * @param user 用户实体
     */
    public void recordUserLogin(User user) {
        log.info("记录用户登录指标: userId={}, username={}", user.getId(), user.getUsername());

        WriteApi writeApi = influxDBClient.getWriteApi();

        writeApi.writeMeasurement(
            WritePrecision.NS,
            new UserLoginMetrics(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                Instant.now()
            )
        );

        writeApi.close();
    }

    /**
     * 记录用户操作指标
     *
     * @param user 用户实体
     * @param operation 操作类型（login, logout, create, update, delete 等）
     * @param operationDetail 操作详情
     */
    public void recordUserOperation(User user, String operation, String operationDetail) {
        log.info("记录用户操作指标: userId={}, username={}, operation={}", user.getId(), user.getUsername(), operation);

        WriteApi writeApi = influxDBClient.getWriteApi();

        writeApi.writeMeasurement(
            WritePrecision.NS,
            new UserOperationMetrics(
                user.getId().toString(),
                user.getUsername(),
                operation,
                operationDetail,
                Instant.now()
            )
        );

        writeApi.close();
    }

    /**
     * 查询用户登录次数
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录次数
     */
    public long countUserLogins(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String flux = String.format(
            "from(bucket: \"%s\") " +
            "|> range(start: %s, stop: %s) " +
            "|> filter(fn: (r) => r._measurement == \"user_login\") " +
            "|> filter(fn: (r) => r.userId == \"%s\") " +
            "|> count()",
            bucket,
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString()
        );

        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);
        long count = 0;
        
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object value = record.getValueByKey("_value");
                if (value instanceof Number) {
                    count += ((Number) value).longValue();
                }
            }
        }

        return count;
    }

    /**
     * 查询用户操作次数
     *
     * @param userId 用户ID
     * @param operation 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    public long countUserOperations(Long userId, String operation, LocalDateTime startTime, LocalDateTime endTime) {
        String flux = String.format(
            "from(bucket: \"%s\") " +
            "|> range(start: %s, stop: %s) " +
            "|> filter(fn: (r) => r._measurement == \"user_operation\") " +
            "|> filter(fn: (r) => r.userId == \"%s\") " +
            "|> filter(fn: (r) => r.operation == \"%s\") " +
            "|> count()",
            bucket,
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString(),
            operation
        );

        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);
        long count = 0;
        
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object value = record.getValueByKey("_value");
                if (value instanceof Number) {
                    count += ((Number) value).longValue();
                }
            }
        }

        return count;
    }

    /**
     * 查询用户操作趋势
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param window 时间窗口（1h, 1d 等）
     * @return 操作趋势数据
     */
    public List<FluxRecord> getUserOperationTrend(Long userId, LocalDateTime startTime, LocalDateTime endTime, String window) {
        String flux = String.format(
            "from(bucket: \"%s\") " +
            "|> range(start: %s, stop: %s) " +
            "|> filter(fn: (r) => r._measurement == \"user_operation\") " +
            "|> filter(fn: (r) => r.userId == \"%s\") " +
            "|> aggregateWindow(every: %s, fn: count, createEmpty: false) " +
            "|> yield(name: \"count\")",
            bucket,
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString(),
            window
        );

        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);
        
        // 将所有记录合并到一个列表
        return tables.stream()
            .flatMap(table -> table.getRecords().stream())
            .toList();
    }

    /**
     * 查询活跃用户数（按时间窗口）
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param window 时间窗口
     * @return 活跃用户数趋势
     */
    public List<FluxRecord> getActiveUserCount(LocalDateTime startTime, LocalDateTime endTime, String window) {
        String flux = String.format(
            "from(bucket: \"%s\") " +
            "|> range(start: %s, stop: %s) " +
            "|> filter(fn: (r) => r._measurement == \"user_login\") " +
            "|> aggregateWindow(every: %s, fn: count, createEmpty: false) " +
            "|> group(columns: [\"_time\"]) " +
            "|> distinct(column: \"userId\") " +
            "|> count()",
            bucket,
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            window
        );

        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);
        
        return tables.stream()
            .flatMap(table -> table.getRecords().stream())
            .toList();
    }

    // ==================== 用户登录指标数据类 ====================

    /**
     * 用户登录指标
     */
    public static class UserLoginMetrics {
        private final String measurement = "user_login";
        private final Instant time;
        private final String userId;
        private final String username;
        private final String email;
        private final String phone;
        private final Integer status;

        public UserLoginMetrics(String userId, String username, String email, String phone, Integer status, Instant time) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.status = status;
            this.time = time;
        }

        public String getMeasurement() {
            return measurement;
        }

        public Instant getTime() {
            return time;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public Integer getStatus() {
            return status;
        }
    }

    // ==================== 用户操作指标数据类 ====================

    /**
     * 用户操作指标
     */
    public static class UserOperationMetrics {
        private final String measurement = "user_operation";
        private final Instant time;
        private final String userId;
        private final String username;
        private final String operation;
        private final String operationDetail;

        public UserOperationMetrics(String userId, String username, String operation, String operationDetail, Instant time) {
            this.userId = userId;
            this.username = username;
            this.operation = operation;
            this.operationDetail = operationDetail;
            this.time = time;
        }

        public String getMeasurement() {
            return measurement;
        }

        public Instant getTime() {
            return time;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getOperation() {
            return operation;
        }

        public String getOperationDetail() {
            return operationDetail;
        }
    }

    // ==================== TODO: 待添加的指标收集方法 ====================
    // TODO: 当实现更多业务功能时，添加以下指标收集方法：
    // - recordUserRegistration(user): 记录用户注册指标
    // - recordUserLogout(user): 记录用户登出指标
    // - recordUserUpdate(user): 记录用户更新指标
    // - recordUserDelete(userId): 记录用户删除指标
    // - recordUserPasswordChange(user): 记录用户密码修改指标
    // - recordUserStatusChange(user, oldStatus, newStatus): 记录用户状态变更指标
    // - recordApiRequest(userId, endpoint, method, duration): 记录API请求指标
    // - recordApiError(userId, endpoint, method, errorMessage): 记录API错误指标
    // - getUserActivitySummary(userId, startTime, endTime): 获取用户活动摘要
    // - getUserBehaviorAnalysis(userId): 用户行为分析
}