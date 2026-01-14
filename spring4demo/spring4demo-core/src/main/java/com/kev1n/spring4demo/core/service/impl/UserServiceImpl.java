package com.kev1n.spring4demo.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.api.enums.UserStatus;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.repository.UserReactiveRepository;
import com.kev1n.spring4demo.core.service.UserAsyncService;
import com.kev1n.spring4demo.core.service.UserCacheService;
import com.kev1n.spring4demo.core.service.UserDistributedService;
import com.kev1n.spring4demo.core.service.UserLogService;
import com.kev1n.spring4demo.core.service.UserSearchService;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserLogService userLogService;
    private final UserReactiveRepository userReactiveRepository;
    private final UserSearchService userSearchService;
    private final UserCacheService userCacheService;
    private final UserAsyncService userAsyncService;
    private final UserDistributedService userDistributedService;

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 根据ID查询用户（使用缓存）
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Override
    public User getById(Long id) {
        // 先从缓存中获取
        User user = userCacheService.getUserFromCache(id);
        if (user != null) {
            return user;
        }

        // 缓存未命中，从数据库查询
        user = super.getById(id);

        // 将用户放入缓存
        if (user != null) {
            userCacheService.putUserToCache(user);
        }

        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Override
    public List<User> findByStatus(Integer status) {
        return userMapper.findByStatus(status);
    }

    @Override
    public long countByStatus(Integer status) {
        return userMapper.countByStatus(status);
    }

    @Override
    public List<User> findRecentActiveUsers() {
        return userMapper.findRecentActiveUsers();
    }

    /**
     * 记录用户操作日志
     *
     * @param user 用户对象
     * @param action 操作类型（CREATE, UPDATE, DELETE等）
     * @param details 操作详情（JSON格式）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    @Override
    public void logUserAction(User user, String action, String details, String ipAddress, String userAgent) {
        if (user != null) {
            userLogService.logUserAction(
                    user.getId(),
                    user.getUsername(),
                    action,
                    details,
                    ipAddress,
                    userAgent
            );
        }
    }

    /**
     * 创建用户（重写父类方法，添加日志记录和异步处理）
     *
     * @param user 用户对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(User user) {
        boolean result = super.save(user);
        if (result) {
            // 将用户放入缓存
            userCacheService.putUserToCache(user);
            log.info("用户已放入缓存: userId={}", user.getId());

            // 同步记录用户操作日志
            logUserAction(user, "CREATE", null, null, null);

            // 异步发送欢迎邮件
            try {
                userAsyncService.sendWelcomeEmailAsync(user.getId());
                log.info("已触发异步发送欢迎邮件: userId={}", user.getId());
            } catch (BusinessException e) {
                log.error("触发异步发送欢迎邮件失败(业务异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (RuntimeException e) {
                log.error("触发异步发送欢迎邮件失败(运行时异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (Exception e) {
                log.error("触发异步发送欢迎邮件失败(未知异常): userId={}", user.getId(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            }

            // 索引用户到Elasticsearch
            try {
                userSearchService.indexUser(user.getId());
                log.info("用户已索引到Elasticsearch: userId={}", user.getId());
            } catch (BusinessException e) {
                log.error("索引用户到Elasticsearch失败(业务异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (RuntimeException e) {
                log.error("索引用户到Elasticsearch失败(运行时异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (Exception e) {
                log.error("索引用户到Elasticsearch失败(未知异常): userId={}", user.getId(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            }
        }
        return result;
    }

    /**
     * 更新用户（重写父类方法，添加日志记录和异步处理）
     *
     * @param user 用户对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(User user) {
        boolean result = super.updateById(user);
        if (result) {
            // 更新缓存
            userCacheService.updateUserInCache(user);
            log.info("用户缓存已更新: userId={}", user.getId());

            // 同步记录用户操作日志
            logUserAction(user, "UPDATE", null, null, null);

            // 异步记录用户操作日志（额外记录详细信息）
            try {
                userAsyncService.logUserActionAsync(
                        user.getId(),
                        user.getUsername(),
                        "UPDATE_ASYNC",
                        String.format("用户信息更新: status=%s, email=%s", user.getStatus(), user.getEmail()),
                        null,
                        null
                );
                log.info("已触发异步记录用户操作日志: userId={}", user.getId());
            } catch (BusinessException e) {
                log.error("触发异步记录用户操作日志失败(业务异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (RuntimeException e) {
                log.error("触发异步记录用户操作日志失败(运行时异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (Exception e) {
                log.error("触发异步记录用户操作日志失败(未知异常): userId={}", user.getId(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            }

            // 更新Elasticsearch索引
            try {
                userSearchService.syncUserToEs(user.getId());
                log.info("用户索引已更新到Elasticsearch: userId={}", user.getId());
            } catch (BusinessException e) {
                log.error("更新用户Elasticsearch索引失败(业务异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (RuntimeException e) {
                log.error("更新用户Elasticsearch索引失败(运行时异常): userId={}, error={}", user.getId(), e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (Exception e) {
                log.error("更新用户Elasticsearch索引失败(未知异常): userId={}", user.getId(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            }
        }
        return result;
    }

    /**
     * 删除用户（重写父类方法，添加日志记录）
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        User user = this.getById(id);
        boolean result = super.removeById(id);
        if (result && user != null) {
            // 删除缓存
            userCacheService.deleteUserFromCache(id);
            log.info("用户缓存已删除: userId={}", id);

            logUserAction(user, "DELETE", null, null, null);
            // 删除Elasticsearch索引
            try {
                userSearchService.deleteUserIndex(id);
                log.info("用户索引已从Elasticsearch删除: userId={}", id);
            } catch (BusinessException e) {
                log.error("删除用户Elasticsearch索引失败(业务异常): userId={}, error={}", id, e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (RuntimeException e) {
                log.error("删除用户Elasticsearch索引失败(运行时异常): userId={}, error={}", id, e.getMessage(), e);
                // 异步操作失败不影响主业务流程，记录日志即可
            } catch (Exception e) {
                log.error("删除用户Elasticsearch索引失败(未知异常): userId={}", id, e);
                // 异步操作失败不影响主业务流程，记录日志即可
            }
        }
        return result;
    }

    // ==================== 响应式编程方法实现 ====================

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

        // 先检查用户名和邮箱是否已存在
        return Mono.zip(
                        Mono.fromCallable(() -> userMapper.existsByUsername(dto.getUsername())),
                        Mono.fromCallable(() -> userMapper.existsByEmail(dto.getEmail()))
                )
                .flatMap(tuple -> {
                    boolean usernameExists = tuple.getT1();
                    boolean emailExists = tuple.getT2();

                    if (usernameExists) {
                        return Mono.error(new IllegalArgumentException("用户名已存在: " + dto.getUsername()));
                    }

                    if (emailExists) {
                        return Mono.error(new IllegalArgumentException("邮箱已存在: " + dto.getEmail()));
                    }

                    // 创建用户实体
                    User user = new User();
                    BeanUtils.copyProperties(dto, user);
                    // 设置默认状态
                    if (user.getStatus() == null) {
                        user.setStatus(UserStatus.ACTIVE.getValue());
                    }

                    return Mono.fromCallable(() -> {
                        userMapper.insert(user);
                        return user;
                    });
                })
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(savedUser -> {
                    if (savedUser != null) {
                        log.info("响应式创建用户成功: {}", savedUser.getId());
                    }else {
                        log.error("响应式创建用户失败");
                    }
                })
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

    // ==================== 分布式事务方法实现 ====================

    /**
     * 用户注册（分布式事务）
     *
     * 委托给UserDistributedService处理
     */
    @Override
    public User registerUserDistributed(UserCreateDTO dto) {
        log.info("调用用户注册分布式事务: username={}", dto.getUsername());
        return userDistributedService.registerUser(dto);
    }
}