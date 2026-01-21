package com.junmo.platform.core.service;

import com.junmo.platform.model.document.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户日志服务接口
 *
 * 提供用户日志的记录和查询功能
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface UserLogService {

    /**
     * 记录用户日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param action 操作类型
     * @param details 操作详情（JSON格式）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void logUserAction(Long userId, String username, String action, String details,
                       String ipAddress, String userAgent);

    /**
     * 记录用户日志（使用UserLog对象）
     *
     * @param userLog 用户日志对象
     */
    void logUserAction(UserLog userLog);

    /**
     * 查询用户日志
     *
     * @param userId 用户ID
     * @return 用户日志列表
     */
    List<UserLog> getUserLogs(Long userId);

    /**
     * 分页查询用户日志
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UserLog> getUserLogs(Long userId, Pageable pageable);

    /**
     * 根据用户ID和操作类型查询日志
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @return 用户日志列表
     */
    List<UserLog> getUserLogsByAction(Long userId, String action);

    /**
     * 根据时间范围查询日志
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    List<UserLog> getUserLogsByTimeRange(LocalDateTime start, LocalDateTime end);

    /**
     * 根据用户ID和时间范围查询日志
     *
     * @param userId 用户ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    List<UserLog> getUserLogsByTimeRange(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 根据操作类型查询日志
     *
     * @param action 操作类型
     * @return 用户日志列表
     */
    List<UserLog> getUserLogsByAction(String action);

    /**
     * 统计用户日志数量
     *
     * @param userId 用户ID
     * @return 日志数量
     */
    long countUserLogs(Long userId);

    /**
     * 统计指定操作类型的日志数量
     *
     * @param action 操作类型
     * @return 日志数量
     */
    long countUserLogsByAction(String action);

    // TODO: 待实现其他对象的日志记录功能
    // TODO: 文档日志（DocumentLog）
    // TODO: 系统日志（SystemLog）
}