package com.kev1n.spring4demo.core.service;

import com.influxdb.v3.client.InfluxDBApiException;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.kev1n.spring4demo.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 用户指标时序数据服务
 *
 * <p>提供用户相关指标的时序数据写入和查询功能，支持用户登录、操作等行为的数据收集和分析。</p>
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMetricsService {

    private final InfluxDBClient influxDBClient;

    /**
     * 记录用户登录指标
     *
     * @param user 用户实体
     */
    public void recordUserLogin(User user) {
        log.info("记录用户登录指标: userId={}, username={}", user.getId(), user.getUsername());

        Point point = Point.measurement("user_login")
                .setTag("userId", user.getId().toString())
                .setTag("username", user.getUsername())
                .setField("email", user.getEmail() != null ? user.getEmail() : "")
                .setField("phone", user.getPhone() != null ? user.getPhone() : "")
                .setField("status", user.getStatus() != null ? user.getStatus() : 0)
                .setTimestamp(Instant.now());

        try {
            influxDBClient.writePoint(point);
            log.info("用户登录指标记录成功: userId={}", user.getId());
        } catch (InfluxDBApiException e) {
            log.error("记录用户登录指标失败(InfluxDB异常): userId={}, error={}", user.getId(), e.getMessage(), e);
            // 指标记录失败不影响主业务流程，记录日志即可
        } catch (Exception e) {
            log.error("记录用户登录指标失败(未知异常): userId={}", user.getId(), e);
        }
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

        Point point = Point.measurement("user_operation")
                .setTag("userId", user.getId().toString())
                .setTag("username", user.getUsername())
                .setTag("operation", operation)
                .setField("operationDetail", operationDetail != null ? operationDetail : "")
                .setTimestamp(Instant.now());

        try {
            influxDBClient.writePoint(point);
            log.info("用户操作指标记录成功: userId={}, operation={}", user.getId(), operation);
        } catch (InfluxDBApiException e) {
            log.error("记录用户操作指标失败(InfluxDB异常): userId={}, operation={}, error={}", user.getId(), operation, e.getMessage(), e);
            // 指标记录失败不影响主业务流程，记录日志即可
        } catch (Exception e) {
            log.error("记录用户操作指标失败(未知异常): userId={}, operation={}", user.getId(), operation, e);
        }
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
        String sql = String.format(
            "SELECT COUNT(*) AS count FROM user_login " +
            "WHERE time >= '%s' AND time <= '%s' AND userId = '%s'",
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString()
        );

        try (Stream<Object[]> stream = influxDBClient.query(sql)) {
            return stream
                    .filter(row -> row.length > 0 && row[0] instanceof Number)
                    .map(row -> ((Number) row[0]).longValue())
                    .findFirst()
                    .orElse(0L);
        } catch (InfluxDBApiException e) {
            log.error("查询用户登录次数失败(InfluxDB异常): userId={}, error={}", userId, e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("查询用户登录次数失败(未知异常): userId={}", userId, e);
            return 0;
        }
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
        String sql = String.format(
            "SELECT COUNT(*) AS count FROM user_operation " +
            "WHERE time >= '%s' AND time <= '%s' AND userId = '%s' AND operation = '%s'",
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString(),
            operation
        );

        try (Stream<Object[]> stream = influxDBClient.query(sql)) {
            return stream
                    .filter(row -> row.length > 0 && row[0] instanceof Number)
                    .map(row -> ((Number) row[0]).longValue())
                    .findFirst()
                    .orElse(0L);
        } catch (InfluxDBApiException e) {
            log.error("查询用户操作次数失败(InfluxDB异常): userId={}, operation={}, error={}", userId, operation, e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("查询用户操作次数失败(未知异常): userId={}, operation={}", userId, operation, e);
            return 0;
        }
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
    public List<OperationTrendResult> getUserOperationTrend(Long userId, LocalDateTime startTime, LocalDateTime endTime, String window) {
        String sql = String.format(
            "SELECT time, COUNT(*) AS count FROM user_operation " +
            "WHERE time >= '%s' AND time <= '%s' AND userId = '%s' " +
            "GROUP BY time(%s)",
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            userId.toString(),
            window
        );

        List<OperationTrendResult> results = new ArrayList<>();
        try (Stream<Object[]> stream = influxDBClient.query(sql)) {
            stream.forEach(row -> {
                if (row.length >= 2) {
                    OperationTrendResult result = new OperationTrendResult();
                    result.time = row[0] instanceof Instant ? (Instant) row[0] : null;
                    result.count = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0;
                    results.add(result);
                }
            });
        } catch (InfluxDBApiException e) {
            log.error("查询用户操作趋势失败(InfluxDB异常): userId={}, error={}", userId, e.getMessage(), e);
        } catch (Exception e) {
            log.error("查询用户操作趋势失败(未知异常): userId={}", userId, e);
        }
        return results;
    }

    /**
     * 查询活跃用户数（按时间窗口）
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param window 时间窗口
     * @return 活跃用户数趋势
     */
    public List<ActiveUserResult> getActiveUserCount(LocalDateTime startTime, LocalDateTime endTime, String window) {
        String sql = String.format(
            "SELECT time, COUNT(DISTINCT userId) AS count FROM user_login " +
            "WHERE time >= '%s' AND time <= '%s' " +
            "GROUP BY time(%s)",
            startTime.atZone(ZoneId.systemDefault()).toInstant(),
            endTime.atZone(ZoneId.systemDefault()).toInstant(),
            window
        );

        List<ActiveUserResult> results = new ArrayList<>();
        try (Stream<Object[]> stream = influxDBClient.query(sql)) {
            stream.forEach(row -> {
                if (row.length >= 2) {
                    ActiveUserResult result = new ActiveUserResult();
                    result.time = row[0] instanceof Instant ? (Instant) row[0] : null;
                    result.count = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0;
                    results.add(result);
                }
            });
        } catch (InfluxDBApiException e) {
            log.error("查询活跃用户数失败(InfluxDB异常): error={}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("查询活跃用户数失败(未知异常)", e);
        }
        return results;
    }

    // ==================== 查询结果类 ====================

    /**
     * 操作趋势查询结果
     */
    public static class OperationTrendResult {
        public Instant time;
        public long count;
    }

    /**
     * 活跃用户数查询结果
     */
    public static class ActiveUserResult {
        public Instant time;
        public long count;
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