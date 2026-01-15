package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.core.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户响应式服务接口（基于MyBatis-Plus）
 *
 * <p>提供基于MyBatis-Plus的响应式用户操作接口，包括：
 * <ul>
 *   <li>响应式查询操作</li>
 *   <li>响应式创建操作</li>
 *   <li>响应式流式操作</li>
 * </ul>
 * </p>
 *
 * <p>注意：此接口与UserReactiveService（基于R2DBC）不同，本接口使用MyBatis-Plus进行数据访问</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserMapperReactiveService {

    /**
     * 响应式查询所有用户
     *
     * @return 用户列表Flux
     */
    Flux<User> listUsersReactive();

    /**
     * 响应式根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    Mono<User> getUserByIdReactive(Long id);

    /**
     * 响应式根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    Mono<User> getUserByUsernameReactive(String username);

    /**
     * 响应式根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    Mono<User> getUserByEmailReactive(String email);

    /**
     * 响应式根据状态查询用户列表
     *
     * @param status 用户状态
     * @return 用户列表Flux
     */
    Flux<User> getUsersByStatusReactive(Integer status);

    /**
     * 响应式创建用户
     *
     * @param dto 用户创建DTO
     * @return 创建后的用户Mono
     */
    Mono<User> createUserReactive(UserCreateDTO dto);

    /**
     * 响应式统计指定状态的用户数量
     *
     * @param status 用户状态
     * @return 用户数量Mono
     */
    Mono<Long> countByStatusReactive(Integer status);

    /**
     * 响应式流式查询用户数据
     *
     * @return 用户流Flux
     */
    Flux<User> streamUsersReactive();
}