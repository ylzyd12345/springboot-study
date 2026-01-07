package com.kev1n.spring4demo.core.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户统计任务
 * <p>
 * 定期统计用户数量和活跃度
 * </p>
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>统计用户数量：总用户数、活跃用户数、新增用户数等</li>
 *   <li>统计用户活跃度：日活跃用户数、周活跃用户数、月活跃用户数等</li>
 *   <li>统计用户状态：正常用户数、禁用用户数、删除用户数等</li>
 * </ul>
 *
 * <p>执行时间：</p>
 * <ul>
 *   <li>每天凌晨1点执行</li>
 * </ul>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 2026-01-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class UserStatsJob implements Job {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;

    /**
     * 任务执行入口
     *
     * @param context 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[UserStatsJob] 用户统计任务开始执行 - 时间: {}",
                LocalDateTime.now().format(FORMATTER));

        try {
            // 获取Job参数
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String statsType = dataMap.getString("statsType");

            log.info("[UserStatsJob] 统计类型: {}", statsType);

            // 1. 统计用户数量
            Map<String, Object> userCountStats = countUserStats();
            log.info("[UserStatsJob] 用户数量统计: {}", userCountStats);

            // 2. 统计用户活跃度
            Map<String, Object> userActivityStats = countUserActivity();
            log.info("[UserStatsJob] 用户活跃度统计: {}", userActivityStats);

            // 3. 统计用户状态
            Map<String, Object> userStatusStats = countUserStatus();
            log.info("[UserStatsJob] 用户状态统计: {}", userStatusStats);

            // 4. 生成统计报告
            generateStatsReport(userCountStats, userActivityStats, userStatusStats);

            log.info("[UserStatsJob] 用户统计任务执行完成 - 时间: {}",
                    LocalDateTime.now().format(FORMATTER));

        } catch (Exception e) {
            log.error("[UserStatsJob] 用户统计任务执行失败", e);
            throw new JobExecutionException(e);
        }
    }

    /**
     * 统计用户数量
     *
     * @return 用户数量统计结果
     */
    private Map<String, Object> countUserStats() {
        log.info("[UserStatsJob] 开始统计用户数量");

        Map<String, Object> stats = new HashMap<>();

        try {
            // 统计总用户数
            Long totalCount = userMapper.selectCount(null);
            stats.put("totalCount", totalCount);

            // 统计今日新增用户数
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LambdaQueryWrapper<User> todayQuery = new LambdaQueryWrapper<>();
            todayQuery.ge(User::getCreateTime, todayStart);
            Long todayCount = userMapper.selectCount(todayQuery);
            stats.put("todayCount", todayCount);

            // 统计本周新增用户数
            LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
            LambdaQueryWrapper<User> weekQuery = new LambdaQueryWrapper<>();
            weekQuery.ge(User::getCreateTime, weekStart);
            Long weekCount = userMapper.selectCount(weekQuery);
            stats.put("weekCount", weekCount);

            // 统计本月新增用户数
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LambdaQueryWrapper<User> monthQuery = new LambdaQueryWrapper<>();
            monthQuery.ge(User::getCreateTime, monthStart);
            Long monthCount = userMapper.selectCount(monthQuery);
            stats.put("monthCount", monthCount);

            log.info("[UserStatsJob] 用户数量统计完成: 总数={}, 今日新增={}, 本周新增={}, 本月新增={}",
                    totalCount, todayCount, weekCount, monthCount);

        } catch (Exception e) {
            log.error("[UserStatsJob] 统计用户数量失败", e);
        }

        return stats;
    }

    /**
     * 统计用户活跃度
     *
     * @return 用户活跃度统计结果
     */
    private Map<String, Object> countUserActivity() {
        log.info("[UserStatsJob] 开始统计用户活跃度");

        Map<String, Object> stats = new HashMap<>();

        try {
            // TODO: 需要User实体增加lastLoginTime字段
            // 当前使用updateTime作为参考

            // 统计活跃用户数（最近7天有登录记录）
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            LambdaQueryWrapper<User> activeQuery = new LambdaQueryWrapper<>();
            activeQuery.ge(User::getUpdateTime, sevenDaysAgo);
            activeQuery.eq(User::getStatus, 1); // 1表示正常状态
            Long activeCount = userMapper.selectCount(activeQuery);
            stats.put("activeCount", activeCount);

            // 统计日活跃用户数（最近1天有登录记录）
            LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
            LambdaQueryWrapper<User> dailyActiveQuery = new LambdaQueryWrapper<>();
            dailyActiveQuery.ge(User::getUpdateTime, oneDayAgo);
            dailyActiveQuery.eq(User::getStatus, 1);
            Long dailyActiveCount = userMapper.selectCount(dailyActiveQuery);
            stats.put("dailyActiveCount", dailyActiveCount);

            // 统计周活跃用户数（最近7天有登录记录）
            stats.put("weeklyActiveCount", activeCount);

            // 统计月活跃用户数（最近30天有登录记录）
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            LambdaQueryWrapper<User> monthlyActiveQuery = new LambdaQueryWrapper<>();
            monthlyActiveQuery.ge(User::getUpdateTime, thirtyDaysAgo);
            monthlyActiveQuery.eq(User::getStatus, 1);
            Long monthlyActiveCount = userMapper.selectCount(monthlyActiveQuery);
            stats.put("monthlyActiveCount", monthlyActiveCount);

            log.info("[UserStatsJob] 用户活跃度统计完成: 日活跃={}, 周活跃={}, 月活跃={}",
                    dailyActiveCount, activeCount, monthlyActiveCount);

        } catch (Exception e) {
            log.error("[UserStatsJob] 统计用户活跃度失败", e);
        }

        return stats;
    }

    /**
     * 统计用户状态
     *
     * @return 用户状态统计结果
     */
    private Map<String, Object> countUserStatus() {
        log.info("[UserStatsJob] 开始统计用户状态");

        Map<String, Object> stats = new HashMap<>();

        try {
            // 统计正常用户数
            LambdaQueryWrapper<User> normalQuery = new LambdaQueryWrapper<>();
            normalQuery.eq(User::getStatus, 1);
            Long normalCount = userMapper.selectCount(normalQuery);
            stats.put("normalCount", normalCount);

            // 统计禁用用户数
            LambdaQueryWrapper<User> disabledQuery = new LambdaQueryWrapper<>();
            disabledQuery.eq(User::getStatus, 0);
            Long disabledCount = userMapper.selectCount(disabledQuery);
            stats.put("disabledCount", disabledCount);

            // 统计删除用户数
            LambdaQueryWrapper<User> deletedQuery = new LambdaQueryWrapper<>();
            deletedQuery.eq(User::getDeleted, 1);
            Long deletedCount = userMapper.selectCount(deletedQuery);
            stats.put("deletedCount", deletedCount);

            log.info("[UserStatsJob] 用户状态统计完成: 正常={}, 禁用={}, 删除={}",
                    normalCount, disabledCount, deletedCount);

        } catch (Exception e) {
            log.error("[UserStatsJob] 统计用户状态失败", e);
        }

        return stats;
    }

    /**
     * 生成统计报告
     *
     * @param userCountStats 用户数量统计
     * @param userActivityStats 用户活跃度统计
     * @param userStatusStats 用户状态统计
     */
    private void generateStatsReport(Map<String, Object> userCountStats,
                                     Map<String, Object> userActivityStats,
                                     Map<String, Object> userStatusStats) {
        log.info("[UserStatsJob] ========== 用户统计报告 ==========");
        log.info("[UserStatsJob] 一、用户数量统计");
        log.info("[UserStatsJob]   - 总用户数: {}", userCountStats.get("totalCount"));
        log.info("[UserStatsJob]   - 今日新增: {}", userCountStats.get("todayCount"));
        log.info("[UserStatsJob]   - 本周新增: {}", userCountStats.get("weekCount"));
        log.info("[UserStatsJob]   - 本月新增: {}", userCountStats.get("monthCount"));
        log.info("[UserStatsJob] 二、用户活跃度统计");
        log.info("[UserStatsJob]   - 日活跃用户: {}", userActivityStats.get("dailyActiveCount"));
        log.info("[UserStatsJob]   - 周活跃用户: {}", userActivityStats.get("weeklyActiveCount"));
        log.info("[UserStatsJob]   - 月活跃用户: {}", userActivityStats.get("monthlyActiveCount"));
        log.info("[UserStatsJob] 三、用户状态统计");
        log.info("[UserStatsJob]   - 正常用户: {}", userStatusStats.get("normalCount"));
        log.info("[UserStatsJob]   - 禁用用户: {}", userStatusStats.get("disabledCount"));
        log.info("[UserStatsJob]   - 删除用户: {}", userStatusStats.get("deletedCount"));
        log.info("[UserStatsJob] ======================================");

        // TODO: 可以将统计报告保存到数据库或发送到消息队列
        // saveStatsReportToDatabase(userCountStats, userActivityStats, userStatusStats);
        // sendStatsReportToMessageQueue(userCountStats, userActivityStats, userStatusStats);
    }

    /**
     * TODO: 保存统计报告到数据库（待实现统计模块后启用）
     *
     * @param userCountStats 用户数量统计
     * @param userActivityStats 用户活跃度统计
     * @param userStatusStats 用户状态统计
     */
    private void saveStatsReportToDatabase(Map<String, Object> userCountStats,
                                           Map<String, Object> userActivityStats,
                                           Map<String, Object> userStatusStats) {
        log.warn("[UserStatsJob] 保存统计报告到数据库功能待实现，需要统计模块支持");
        // TODO: 待实现统计模块后启用
        // 1. 创建UserStatsReport实体
        // 2. 保存统计报告到数据库
    }

    /**
     * TODO: 发送统计报告到消息队列（待实现消息队列模块后启用）
     *
     * @param userCountStats 用户数量统计
     * @param userActivityStats 用户活跃度统计
     * @param userStatusStats 用户状态统计
     */
    private void sendStatsReportToMessageQueue(Map<String, Object> userCountStats,
                                                Map<String, Object> userActivityStats,
                                                Map<String, Object> userStatusStats) {
        log.warn("[UserStatsJob] 发送统计报告到消息队列功能待实现，需要消息队列模块支持");
        // TODO: 待实现消息队列模块后启用
        // 1. 创建UserStatsMessage消息
        // 2. 发送消息到RabbitMQ或Kafka
    }

    /**
     * TODO: 统计订单数据（待实现订单模块后启用）
     *
     * @return 订单数据统计结果
     */
    private Map<String, Object> countOrderStats() {
        log.warn("[UserStatsJob] 订单数据统计功能待实现，需要订单模块支持");
        Map<String, Object> stats = new HashMap<>();
        // TODO: 待实现订单模块后启用
        // 1. 统计订单数量
        // 2. 统计订单金额
        // 3. 统计订单状态
        return stats;
    }
}