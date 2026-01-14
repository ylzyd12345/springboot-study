package com.kev1n.spring4demo.core.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存刷新任务
 * <p>
 * 定期刷新用户缓存和配置缓存
 * </p>
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>刷新用户缓存：预热活跃用户缓存</li>
 *   <li>刷新配置缓存：更新系统配置缓存</li>
 *   <li>清理过期缓存：清理过期的缓存数据</li>
 * </ul>
 *
 * <p>执行时间：</p>
 * <ul>
 *   <li>每小时执行一次</li>
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
public class CacheRefreshJob implements Job {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String CACHE_KEY_PREFIX = "user:";
    private static final String CONFIG_CACHE_KEY_PREFIX = "config:";

    private final UserMapper userMapper;
    private final UserCacheService userCacheService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 任务执行入口
     *
     * @param context 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[CacheRefreshJob] 缓存刷新任务开始执行 - 时间: {}",
                LocalDateTime.now().format(FORMATTER));

        try {
            // 获取Job参数
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String refreshType = dataMap.getString("refreshType");

            log.info("[CacheRefreshJob] 刷新类型: {}", refreshType);

            // 1. 刷新用户缓存
            refreshUserCache();

            // 2. 刷新配置缓存
            refreshConfigCache();

            // 3. 清理过期缓存
            cleanExpiredCache();

            log.info("[CacheRefreshJob] 缓存刷新任务执行完成 - 时间: {}",
                    LocalDateTime.now().format(FORMATTER));

        } catch (DataAccessException e) {
            log.error("[CacheRefreshJob] 数据库访问失败，无法刷新缓存: {}", e.getMessage());
            // 发送告警通知
            sendAlert("缓存刷新任务失败", "数据库访问失败: " + e.getMessage());
            throw new JobExecutionException("数据库访问失败", e);
        } catch (RuntimeException e) {
            log.error("[CacheRefreshJob] 缓存刷新任务执行时发生运行时异常: {}", e.getMessage());
            // 发送告警通知
            sendAlert("缓存刷新任务失败", "运行时异常: " + e.getMessage());
            throw new JobExecutionException("运行时异常", e);
        } catch (Exception e) {
            log.error("[CacheRefreshJob] 缓存刷新任务执行时发生未知异常", e);
            // 发送告警通知
            sendAlert("缓存刷新任务失败", "未知异常: " + e.getMessage());
            throw new JobExecutionException("未知异常", e);
        }
    }

    /**
     * 刷新用户缓存
     * <p>
     * 预热活跃用户缓存，提高访问性能
     * </p>
     */
    private void refreshUserCache() {
        log.info("[CacheRefreshJob] 开始刷新用户缓存");

        try {
            // 查询最近活跃的用户（最近7天有更新记录的用户）
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            LambdaQueryWrapper<User> activeQuery = new LambdaQueryWrapper<>();
            activeQuery.ge(User::getUpdatedAt, sevenDaysAgo);
            activeQuery.eq(User::getStatus, 1); // 1表示正常状态
            activeQuery.last("LIMIT 100"); // 限制最多100个用户
            List<User> activeUsers = userMapper.selectList(activeQuery);

            log.info("[CacheRefreshJob] 查询到 {} 个活跃用户", activeUsers.size());

            // 预热用户缓存
            int refreshedCount = 0;
            for (User user : activeUsers) {
                try {
                    // 将用户放入缓存
                    userCacheService.putUserToCache(user);
                    refreshedCount++;
                } catch (RuntimeException e) {
                    log.error("[CacheRefreshJob] 预热用户缓存失败: userId={}, 错误: {}", user.getId(), e.getMessage());
                } catch (Exception e) {
                    log.error("[CacheRefreshJob] 预热用户缓存时发生未知异常: userId={}", user.getId(), e);
                }
            }

            log.info("[CacheRefreshJob] 用户缓存刷新完成: {} 个用户", refreshedCount);

        } catch (DataAccessException e) {
            log.error("[CacheRefreshJob] 数据库访问失败，无法刷新用户缓存: {}", e.getMessage());
            // 发送告警通知
            sendAlert("用户缓存刷新失败", "数据库访问失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("[CacheRefreshJob] 参数错误，无法刷新用户缓存: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[CacheRefreshJob] 刷新用户缓存时发生未知异常", e);
            // 发送告警通知
            sendAlert("用户缓存刷新失败", "未知异常: " + e.getMessage());
        }
    }

    /**
     * 刷新配置缓存
     * <p>
     * 更新系统配置缓存
     * </p>
     */
    private void refreshConfigCache() {
        log.info("[CacheRefreshJob] 开始刷新配置缓存");

        try {
            // TODO: 需要配置模块支持
            // 当前模拟刷新配置缓存

            // 查询所有配置缓存key
            Set<String> configKeys = redisTemplate.keys(CONFIG_CACHE_KEY_PREFIX + "*");

            if (configKeys != null && !configKeys.isEmpty()) {
                log.info("[CacheRefreshJob] 查询到 {} 个配置缓存", configKeys.size());

                // TODO: 从数据库重新加载配置并更新缓存
                // for (String configKey : configKeys) {
                //     String configName = configKey.replace(CONFIG_CACHE_KEY_PREFIX, "");
                //     Object configValue = configService.loadConfig(configName);
                //     redisTemplate.opsForValue().set(configKey, configValue);
                // }

                log.info("[CacheRefreshJob] 配置缓存刷新功能待实现，需要配置模块支持");
            } else {
                log.info("[CacheRefreshJob] 没有配置缓存需要刷新");
            }

        } catch (RuntimeException e) {
            log.error("[CacheRefreshJob] Redis操作失败，无法刷新配置缓存: {}", e.getMessage());
            // 发送告警通知
            sendAlert("配置缓存刷新失败", "Redis操作失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("[CacheRefreshJob] 刷新配置缓存时发生未知异常", e);
            // 发送告警通知
            sendAlert("配置缓存刷新失败", "未知异常: " + e.getMessage());
        }
    }

    /**
     * 清理过期缓存
     * <p>
     * 清理过期的缓存数据
     * </p>
     */
    private void cleanExpiredCache() {
        log.info("[CacheRefreshJob] 开始清理过期缓存");

        try {
            // Redis会自动清理过期的key，这里主要是清理一些特殊情况

            // 1. 清理空值缓存
            Set<String> nullKeys = redisTemplate.keys("user:null:*");
            if (nullKeys != null && !nullKeys.isEmpty()) {
                long deletedCount = redisTemplate.delete(nullKeys);
                log.info("[CacheRefreshJob] 清理空值缓存: {} 个", deletedCount);
            }

            // 2. 清理无效的用户缓存（用户已删除但缓存还存在）
            Set<String> userCacheKeys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (userCacheKeys != null && !userCacheKeys.isEmpty()) {
                log.info("[CacheRefreshJob] 查询到 {} 个用户缓存", userCacheKeys.size());

                // TODO: 验证缓存的有效性
                // for (String cacheKey : userCacheKeys) {
                //     String userIdStr = cacheKey.replace(CACHE_KEY_PREFIX, "");
                //     Long userId = Long.parseLong(userIdStr);
                //     User user = userMapper.selectById(userId);
                //     if (user == null || user.getDeleted() == 1) {
                //         redisTemplate.delete(cacheKey);
                //         log.info("[CacheRefreshJob] 清理无效用户缓存: userId={}", userId);
                //     }
                // }

                log.info("[CacheRefreshJob] 无效用户缓存清理功能待实现");
            }

            log.info("[CacheRefreshJob] 过期缓存清理完成");

        } catch (RuntimeException e) {
            log.error("[CacheRefreshJob] Redis操作失败，无法清理过期缓存: {}", e.getMessage());
            // 发送告警通知
            sendAlert("过期缓存清理失败", "Redis操作失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("[CacheRefreshJob] 清理过期缓存时发生未知异常", e);
            // 发送告警通知
            sendAlert("过期缓存清理失败", "未知异常: " + e.getMessage());
        }
    }

    /**
     * TODO: 刷新商品缓存（待实现商品模块后启用）
     */
    private void refreshProductCache() {
        log.warn("[CacheRefreshJob] 商品缓存刷新功能待实现，需要商品模块支持");
        // TODO: 待实现商品模块后启用
        // 1. 查询热门商品
        // 2. 预热商品缓存
    }

    /**
     * TODO: 刷新权限缓存（待实现权限模块后启用）
     */
    private void refreshPermissionCache() {
        log.warn("[CacheRefreshJob] 权限缓存刷新功能待实现，需要权限模块支持");
        // TODO: 待实现权限模块后启用
        // 1. 查询所有角色和权限
        // 2. 刷新权限缓存
    }

    /**
     * TODO: 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    private Map<String, Object> getCacheStats() {
        log.warn("[CacheRefreshJob] 缓存统计功能待实现");
        Map<String, Object> stats = new java.util.HashMap<>();
        // TODO: 获取缓存统计信息
        // 1. 用户缓存数量
        // 2. 配置缓存数量
        // 3. 缓存命中率
        // 4. 缓存大小
        return stats;
    }

    /**
     * 发送告警通知
     *
     * @param title 告警标题
     * @param message 告警消息
     */
    private void sendAlert(String title, String message) {
        log.warn("[CacheRefreshJob] 告警通知 - 标题: {}, 消息: {}", title, message);
        // TODO: 待实现告警模块后启用
        // 1. 创建告警消息
        // 2. 发送到告警系统（邮件、短信、钉钉等）
    }
}