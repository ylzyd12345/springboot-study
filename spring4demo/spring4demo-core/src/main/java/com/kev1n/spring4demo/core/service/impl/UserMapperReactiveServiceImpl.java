package com.kev1n.spring4demo.core.service.impl;

import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.api.enums.UserStatus;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.service.UserMapperReactiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * 用户响应式服务实现类（基于MyBatis-Plus）
 *
 * <p>专门处理响应式编程相关的用户操作，包括：
 * <ul>
 *   <li>响应式查询操作</li>
 *   <li>响应式创建操作</li>
 *   <li>响应式流式操作</li>
 * </ul>
 * </p>
 *
 * <p>注意：此实现类使用MyBatis-Plus进行数据访问，与UserReactiveService（基于R2DBC）不同</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMapperReactiveServiceImpl implements UserMapperReactiveService {

    private final UserMapper userMapper;

    @Override
    public Flux<User> listUsersReactive() {
        log.debug("响应式查询所有用户");
        return Flux.defer(() -> Flux.fromIterable(userMapper.selectList(null)))
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(throwable -> {
                    log.error("响应式查询所有用户失败", throwable);
                    return Flux.empty();
                });
    }

    @Override
    public Mono<User> getUserByIdReactive(Long id) {
        log.debug("响应式根据ID查询用户: {}", id);
        return Mono.fromCallable(() -> userMapper.selectById(id))
                .timeout(Duration.ofSeconds(10))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + id)))
                .onErrorResume(throwable -> {
                    log.error("响应式根据ID查询用户失败: {}", id, throwable);
                    return Mono.error(throwable);
                });
    }

    @Override
    public Mono<User> getUserByUsernameReactive(String username) {
        log.debug("响应式根据用户名查询用户: {}", username);
        return Mono.fromCallable(() -> userMapper.findByUsername(username).orElse(null))
                .timeout(Duration.ofSeconds(10))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + username)))
                .onErrorResume(throwable -> {
                    log.error("响应式根据用户名查询用户失败: {}", username, throwable);
                    return Mono.error(throwable);
                });
    }

    @Override
    public Mono<User> getUserByEmailReactive(String email) {
        log.debug("响应式根据邮箱查询用户: {}", email);
        return Mono.fromCallable(() -> userMapper.findByEmail(email).orElse(null))
                .timeout(Duration.ofSeconds(10))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + email)))
                .onErrorResume(throwable -> {
                    log.error("响应式根据邮箱查询用户失败: {}", email, throwable);
                    return Mono.error(throwable);
                });
    }

    @Override
    public Flux<User> getUsersByStatusReactive(Integer status) {
        log.debug("响应式根据状态查询用户列表: {}", status);
        return Flux.defer(() -> Flux.fromIterable(userMapper.findByStatus(status)))
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(throwable -> {
                    log.error("响应式根据状态查询用户列表失败: {}", status, throwable);
                    return Flux.empty();
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<User> createUserReactive(UserCreateDTO dto) {
        log.info("响应式创建用户: {}", dto.getUsername());

        return validateUserCreateDTO(dto)
                .then(Mono.defer(() -> insertUser(dto)))
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(savedUser -> handleCreateSuccess(savedUser))
                .doOnError(throwable -> log.error("响应式创建用户失败: {}", dto.getUsername(), throwable));
    }

    @Override
    public Mono<Long> countByStatusReactive(Integer status) {
        log.debug("响应式统计指定状态的用户数量: {}", status);
        return Mono.fromCallable(() -> userMapper.countByStatus(status))
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(throwable -> {
                    log.error("响应式统计用户数量失败: {}", status, throwable);
                    return Mono.just(0L);
                });
    }

    @Override
    public Flux<User> streamUsersReactive() {
        log.debug("响应式流式查询用户数据");
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> listUsersReactive())
                .timeout(Duration.ofMinutes(5))
                .onErrorResume(throwable -> {
                    log.error("响应式流式查询用户数据失败", throwable);
                    return Flux.empty();
                });
    }

    /**
     * 验证用户创建DTO
     *
     * @param dto 用户创建DTO
     * @return 验证通过的Mono
     */
    private Mono<Void> validateUserCreateDTO(UserCreateDTO dto) {
        return Mono.zip(
                Mono.fromCallable(() -> userMapper.existsByUsername(dto.getUsername())),
                Mono.fromCallable(() -> userMapper.existsByEmail(dto.getEmail()))
        ).flatMap(tuple -> {
            boolean usernameExists = tuple.getT1();
            boolean emailExists = tuple.getT2();

            if (usernameExists) {
                return Mono.error(new IllegalArgumentException("用户名已存在: " + dto.getUsername()));
            }

            if (emailExists) {
                return Mono.error(new IllegalArgumentException("邮箱已存在: " + dto.getEmail()));
            }

            return Mono.empty();
        });
    }

    /**
     * 插入用户到数据库
     *
     * @param dto 用户创建DTO
     * @return 插入的用户Mono
     */
    private Mono<User> insertUser(UserCreateDTO dto) {
        return Mono.fromCallable(() -> {
            User user = new User();
            BeanUtils.copyProperties(dto, user);
            if (user.getStatus() == null) {
                user.setStatus(UserStatus.ACTIVE.getValue());
            }
            userMapper.insert(user);
            return user;
        });
    }

    /**
     * 处理创建成功
     *
     * @param savedUser 保存的用户
     */
    private void handleCreateSuccess(User savedUser) {
        if (savedUser != null) {
            log.info("响应式创建用户成功: {}", savedUser.getId());
        } else {
            log.error("响应式创建用户失败");
        }
    }
}