package com.junmo.platform.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junmo.platform.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据Id查找用户
     */
    User getById(Long id);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(Integer status);

    /**
     * 统计指定状态的用户数量
     */
    long countByStatus(Integer status);

    /**
     * 查找最近创建的用户
     */
    List<User> findRecentActiveUsers();

    /**
     * 记录用户操作日志
     *
     * @param user 用户对象
     * @param action 操作类型（CREATE, UPDATE, DELETE等）
     * @param details 操作详情（JSON格式）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void logUserAction(User user, String action, String details, String ipAddress, String userAgent);

    // ==================== 响应式编程方法 ====================

    /**
     * 响应式查询所有用户
     *
     * @return 用户列表Flux
     */
    reactor.core.publisher.Flux<User> listUsersReactive();

    /**
     * 响应式根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    reactor.core.publisher.Mono<User> getUserByIdReactive(Long id);

    /**
     * 响应式根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    reactor.core.publisher.Mono<User> getUserByUsernameReactive(String username);

    /**
     * 响应式根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    reactor.core.publisher.Mono<User> getUserByEmailReactive(String email);

    /**
     * 响应式根据状态查询用户列表
     *
     * @param status 用户状态
     * @return 用户列表Flux
     */
    reactor.core.publisher.Flux<User> getUsersByStatusReactive(Integer status);

    /**
     * 响应式创建用户
     *
     * @param dto 用户创建DTO
     * @return 创建后的用户Mono
     */
    reactor.core.publisher.Mono<User> createUserReactive(com.junmo.platform.api.dto.UserCreateDTO dto);

    /**
     * 响应式统计指定状态的用户数量
     *
     * @param status 用户状态
     * @return 用户数量Mono
     */
    reactor.core.publisher.Mono<Long> countByStatusReactive(Integer status);

    /**
     * 响应式流式查询用户数据
     *
     * @return 用户流Flux
     */
    reactor.core.publisher.Flux<User> streamUsersReactive();

    // ==================== 分布式事务方法 ====================

    /**
     * 用户注册（分布式事务）
     *
     * 使用Seata AT模式保证分布式事务一致性
     * 涉及：用户表 + 用户日志表
     *
     * @param dto 用户创建DTO
     * @return 创建的用户对象
     */
    User registerUserDistributed(com.junmo.platform.api.dto.UserCreateDTO dto);

    }