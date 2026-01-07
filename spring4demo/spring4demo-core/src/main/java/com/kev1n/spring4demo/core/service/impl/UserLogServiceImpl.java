package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.core.document.UserLog;
import com.kev1n.spring4demo.core.repository.mongo.UserLogRepository;
import com.kev1n.spring4demo.core.service.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户日志服务实现类
 *
 * 提供用户日志的记录和查询功能
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogServiceImpl implements UserLogService {

    private final UserLogRepository userLogRepository;

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
    @Override
    public void logUserAction(Long userId, String username, String action, String details,
                              String ipAddress, String userAgent) {
        UserLog userLog = UserLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        userLogRepository.save(userLog);
        log.info("记录用户日志: userId={}, username={}, action={}", userId, username, action);
    }

    /**
     * 记录用户日志（使用UserLog对象）
     *
     * @param userLog 用户日志对象
     */
    @Override
    public void logUserAction(UserLog userLog) {
        if (userLog.getCreatedAt() == null) {
            userLog.setCreatedAt(LocalDateTime.now());
        }
        userLogRepository.save(userLog);
        log.info("记录用户日志: userId={}, username={}, action={}",
                userLog.getUserId(), userLog.getUsername(), userLog.getAction());
    }

    /**
     * 查询用户日志
     *
     * @param userId 用户ID
     * @return 用户日志列表
     */
    @Override
    public List<UserLog> getUserLogs(Long userId) {
        log.info("查询用户日志: userId={}", userId);
        return userLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 分页查询用户日志
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Override
    public Page<UserLog> getUserLogs(Long userId, Pageable pageable) {
        log.info("分页查询用户日志: userId={}, page={}, size={}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return userLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 根据用户ID和操作类型查询日志
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @return 用户日志列表
     */
    @Override
    public List<UserLog> getUserLogsByAction(Long userId, String action) {
        log.info("根据操作类型查询用户日志: userId={}, action={}", userId, action);
        return userLogRepository.findByUserIdAndActionOrderByCreatedAtDesc(userId, action);
    }

    /**
     * 根据时间范围查询日志
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    @Override
    public List<UserLog> getUserLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        log.info("根据时间范围查询日志: start={}, end={}", start, end);
        return userLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * 根据用户ID和时间范围查询日志
     *
     * @param userId 用户ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    @Override
    public List<UserLog> getUserLogsByTimeRange(Long userId, LocalDateTime start, LocalDateTime end) {
        log.info("根据用户ID和时间范围查询日志: userId={}, start={}, end={}", userId, start, end);
        return userLogRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, start, end);
    }

    /**
     * 根据操作类型查询日志
     *
     * @param action 操作类型
     * @return 用户日志列表
     */
    @Override
    public List<UserLog> getUserLogsByAction(String action) {
        log.info("根据操作类型查询日志: action={}", action);
        return userLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * 统计用户日志数量
     *
     * @param userId 用户ID
     * @return 日志数量
     */
    @Override
    public long countUserLogs(Long userId) {
        log.info("统计用户日志数量: userId={}", userId);
        return userLogRepository.countByUserId(userId);
    }

    /**
     * 统计指定操作类型的日志数量
     *
     * @param action 操作类型
     * @return 日志数量
     */
    @Override
    public long countUserLogsByAction(String action) {
        log.info("统计操作类型日志数量: action={}", action);
        return userLogRepository.countByAction(action);
    }
}