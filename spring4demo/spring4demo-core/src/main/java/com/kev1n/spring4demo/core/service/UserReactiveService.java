package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.r2dbc.UserR2dbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户响应式服务
 * 使用R2DBC实现真正的响应式数据访问
 * 
 * 注意：此Service与UserService同时存在
 * - 传统Controller使用UserService（同步，基于MyBatis-Plus）
 * - 响应式Controller使用UserReactiveService（异步，基于R2DBC）
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
public class UserReactiveService {

    @Autowired
    private UserR2dbcRepository userR2dbcRepository;

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    public Mono<User> findById(Long id) {
        log.info("R2DBC响应式查询用户: id={}", id);
        return userR2dbcRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")));
    }

    /**
     * 查找所有用户
     *
     * @return 用户Flux
     */
    public Flux<User> findAll() {
        log.info("R2DBC响应式查询所有用户");
        return userR2dbcRepository.findAll();
    }

    /**
     * 保存用户
     *
     * @param user 用户实体
     * @return 保存后的用户Mono
     */
    public Mono<User> save(User user) {
        log.info("R2DBC响应式保存用户: username={}", user.getUsername());
        return userR2dbcRepository.save(user);
    }

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @return 更新后的用户Mono
     */
    public Mono<User> update(User user) {
        log.info("R2DBC响应式更新用户: id={}", user.getId());
        return userR2dbcRepository.findById(user.getId())
            .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")))
            .flatMap(existingUser -> {
                // 更新用户信息
                if (user.getUsername() != null) {
                    existingUser.setUsername(user.getUsername());
                }
                if (user.getEmail() != null) {
                    existingUser.setEmail(user.getEmail());
                }
                if (user.getPhone() != null) {
                    existingUser.setPhone(user.getPhone());
                }
                if (user.getRealName() != null) {
                    existingUser.setRealName(user.getRealName());
                }
                if (user.getAvatar() != null) {
                    existingUser.setAvatar(user.getAvatar());
                }
                if (user.getStatus() != null) {
                    existingUser.setStatus(user.getStatus());
                }
                if (user.getDeptId() != null) {
                    existingUser.setDeptId(user.getDeptId());
                }
                return userR2dbcRepository.save(existingUser);
            });
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功Mono
     */
    public Mono<Boolean> deleteById(Long id) {
        log.info("R2DBC响应式删除用户: id={}", id);
        return userR2dbcRepository.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.just(false);
                }
                return userR2dbcRepository.deleteById(id)
                    .thenReturn(true);
            });
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    public Mono<User> findByUsername(String username) {
        log.info("R2DBC响应式根据用户名查找: username={}", username);
        return userR2dbcRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    public Mono<User> findByEmail(String email) {
        log.info("R2DBC响应式根据邮箱查找: email={}", email);
        return userR2dbcRepository.findByEmail(email);
    }

    /**
     * 根据状态查找用户
     *
     * @param status 状态
     * @return 用户Flux
     */
    public Flux<User> findByStatus(Integer status) {
        log.info("R2DBC响应式根据状态查找用户: status={}", status);
        return userR2dbcRepository.findByStatus(status);
    }

    /**
     * 查找所有活跃用户
     *
     * @return 用户Flux
     */
    public Flux<User> findActiveUsers() {
        log.info("R2DBC响应式查找活跃用户");
        return userR2dbcRepository.findByStatusAndDeleted(1, 0);
    }

    /**
     * 根据部门ID查找用户
     *
     * @param deptId 部门ID
     * @return 用户Flux
     */
    public Flux<User> findByDeptId(String deptId) {
        log.info("R2DBC响应式根据部门ID查找用户: deptId={}", deptId);
        return userR2dbcRepository.findByDeptId(deptId);
    }

    /**
     * 批量保存用户
     *
     * @param users 用户列表
     * @return 保存后的用户Flux
     */
    public Flux<User> saveAll(Iterable<User> users) {
        log.info("R2DBC响应式批量保存用户: count={}", users.spliterator().estimateSize());
        return userR2dbcRepository.saveAll(users);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功Mono
     */
    public Mono<Boolean> deleteAllById(Iterable<Long> ids) {
        log.info("R2DBC响应式批量删除用户: count={}", ids.spliterator().estimateSize());
        return Flux.fromIterable(ids)
            .flatMap(userR2dbcRepository::deleteById)
            .then(Mono.just(true))
            .onErrorResume(e -> Mono.just(false));
    }

    /**
     * 统计用户数量
     *
     * @return 用户数量Mono
     */
    public Mono<Long> count() {
        log.info("R2DBC响应式统计用户数量");
        return userR2dbcRepository.count();
    }

    /**
     * 根据状态统计用户数量
     *
     * @param status 状态
     * @return 用户数量Mono
     */
    public Mono<Long> countByStatus(Integer status) {
        log.info("R2DBC响应式根据状态统计用户数量: status={}", status);
        return userR2dbcRepository.findByStatus(status)
            .count();
    }
}