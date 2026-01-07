package com.kev1n.spring4demo.core.repository.mongo;

import com.kev1n.spring4demo.core.document.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户日志Repository
 *
 * 提供用户日志的数据库操作接口
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Repository
public interface UserLogRepository extends MongoRepository<UserLog, String> {

    /**
     * 根据用户ID查询日志（按创建时间倒序）
     *
     * @param userId 用户ID
     * @return 用户日志列表
     */
    List<UserLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID分页查询日志（按创建时间倒序）
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UserLog> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和操作类型查询日志（按创建时间倒序）
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @return 用户日志列表
     */
    List<UserLog> findByUserIdAndActionOrderByCreatedAtDesc(Long userId, String action);

    /**
     * 根据时间范围查询日志
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    List<UserLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据用户ID和时间范围查询日志
     *
     * @param userId 用户ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 用户日志列表
     */
    List<UserLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 根据操作类型查询日志
     *
     * @param action 操作类型
     * @return 用户日志列表
     */
    List<UserLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * 根据IP地址查询日志
     *
     * @param ipAddress IP地址
     * @return 用户日志列表
     */
    List<UserLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress);

    /**
     * 统计指定用户的日志数量
     *
     * @param userId 用户ID
     * @return 日志数量
     */
    long countByUserId(Long userId);

    /**
     * 统计指定操作类型的日志数量
     *
     * @param action 操作类型
     * @return 日志数量
     */
    long countByAction(String action);
}