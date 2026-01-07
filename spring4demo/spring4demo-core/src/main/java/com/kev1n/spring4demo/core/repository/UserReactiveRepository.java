package com.kev1n.spring4demo.core.repository;

import com.kev1n.spring4demo.core.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户响应式数据访问接口
 * 基于MyBatis-Plus实现响应式数据访问
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserReactiveRepository {

    /**
     * 响应式查询所有用户
     *
     * @return 用户列表Flux
     */
    Flux<User> findAll();

    /**
     * 响应式根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    Mono<User> findById(Long id);

    /**
     * 响应式根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    Mono<User> findByUsername(String username);

    /**
     * 响应式根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    Mono<User> findByEmail(String email);

    /**
     * 响应式根据状态查询用户
     *
     * @param status 用户状态
     * @return 用户列表Flux
     */
    Flux<User> findByStatus(Integer status);

    /**
     * 响应式保存用户
     *
     * @param user 用户实体
     * @return 保存后的用户Mono
     */
    Mono<User> save(User user);

    /**
     * 响应式更新用户
     *
     * @param user 用户实体
     * @return 更新后的用户Mono
     */
    Mono<User> update(User user);

    /**
     * 响应式删除用户
     *
     * @param id 用户ID
     * @return 删除结果Mono
     */
    Mono<Boolean> deleteById(Long id);

    /**
     * 响应式检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在Mono
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * 响应式检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在Mono
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * 响应式统计指定状态的用户数量
     *
     * @param status 用户状态
     * @return 用户数量Mono
     */
    Mono<Long> countByStatus(Integer status);

    /**
     * 响应式流式查询用户数据
     *
     * @return 用户流Flux
     */
    Flux<User> streamUsers();

    /**
     * 响应式批量查询用户
     *
     * @param ids 用户ID列表
     * @return 用户列表Flux
     */
    Flux<User> findAllById(Iterable<Long> ids);
}