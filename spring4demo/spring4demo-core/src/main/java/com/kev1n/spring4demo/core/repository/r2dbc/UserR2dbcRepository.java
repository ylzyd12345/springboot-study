package com.kev1n.spring4demo.core.repository.r2dbc;

import com.kev1n.spring4demo.core.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户R2DBC响应式Repository
 * 使用R2DBC实现真正的响应式数据访问
 * 
 * 注意：此Repository与MyBatis-Plus的UserMapper同时存在
 * - 传统Controller使用UserMapper（同步）
 * - 响应式Controller使用UserR2dbcRepository（异步）
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Repository
public interface UserR2dbcRepository extends R2dbcRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    Mono<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    Mono<User> findByEmail(String email);

    /**
     * 根据状态查找用户列表
     *
     * @param status 状态
     * @return 用户Flux
     */
    Flux<User> findByStatus(Integer status);

    /**
     * 根据状态和删除标记查找用户
     *
     * @param status 状态
     * @param deleted 删除标记
     * @return 用户Flux
     */
    Flux<User> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 根据部门ID查找用户
     *
     * @param deptId 部门ID
     * @return 用户Flux
     */
    Flux<User> findByDeptId(String deptId);
}