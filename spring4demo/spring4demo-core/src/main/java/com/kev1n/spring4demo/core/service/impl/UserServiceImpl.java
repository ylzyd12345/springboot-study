package com.kev1n.spring4demo.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.common.helper.AsyncExecutorHelper;
import com.kev1n.spring4demo.common.util.SpringContextUtil;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.service.UserAsyncService;
import com.kev1n.spring4demo.core.service.UserCacheService;
import com.kev1n.spring4demo.core.service.UserDistributedService;
import com.kev1n.spring4demo.core.service.UserLogService;
import com.kev1n.spring4demo.core.service.UserSearchService;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.core.service.UserMapperReactiveService;
import com.kev1n.spring4demo.core.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 *
 * <p>负责处理同步用户操作，包括：
 * <ul>
 *   <li>用户查询操作</li>
 *   <li>用户创建、更新、删除操作</li>
 *   <li>用户缓存管理</li>
 *   <li>用户日志记录</li>
 * </ul>
 * </p>
 *
 * <p>响应式编程相关操作已迁移至 {@link UserReactiveServiceImpl}</p>
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
    private final UserSearchService userSearchService;
    private final UserCacheService userCacheService;
    private final UserAsyncService userAsyncService;
    private final UserDistributedService userDistributedService;
    private final UserValidator userValidator;
    private final AsyncExecutorHelper asyncExecutor;

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User getById(Long id) {
        User user = userCacheService.getUserFromCache(id);
        if (user != null) {
            return user;
        }

        user = super.getById(id);

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(User user) {
        boolean result = super.save(user);
        if (result) {
            handleSaveSuccess(user);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(User user) {
        boolean result = super.updateById(user);
        if (result) {
            handleUpdateSuccess(user);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        User user = this.getById(id);
        boolean result = super.removeById(id);
        if (result && user != null) {
            handleRemoveSuccess(user);
        }
        return result;
    }

    // ==================== 响应式编程方法实现（委托给UserReactiveService）====================

    @Override
    public Flux<User> listUsersReactive() {
        return getReactiveService().listUsersReactive();
    }

    @Override
    public Mono<User> getUserByIdReactive(Long id) {
        return getReactiveService().getUserByIdReactive(id);
    }

    @Override
    public Mono<User> getUserByUsernameReactive(String username) {
        return getReactiveService().getUserByUsernameReactive(username);
    }

    @Override
    public Mono<User> getUserByEmailReactive(String email) {
        return getReactiveService().getUserByEmailReactive(email);
    }

    @Override
    public Flux<User> getUsersByStatusReactive(Integer status) {
        return getReactiveService().getUsersByStatusReactive(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<User> createUserReactive(UserCreateDTO dto) {
        return getReactiveService().createUserReactive(dto);
    }

    @Override
    public Mono<Long> countByStatusReactive(Integer status) {
        return getReactiveService().countByStatusReactive(status);
    }

    @Override
    public Flux<User> streamUsersReactive() {
        return getReactiveService().streamUsersReactive();
    }

    // ==================== 分布式事务方法实现 ====================

    @Override
    public User registerUserDistributed(UserCreateDTO dto) {
        log.info("调用用户注册分布式事务: username={}", dto.getUsername());
        return userDistributedService.registerUser(dto);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取响应式服务实例
     *
     * @return UserMapperReactiveService实例
     */
    private UserMapperReactiveService getReactiveService() {
        return SpringContextUtil.getBean(UserMapperReactiveService.class);
    }

    /**
     * 处理用户保存成功后的操作
     *
     * @param user 保存的用户
     */
    private void handleSaveSuccess(User user) {
        userCacheService.putUserToCache(user);
        log.info("用户已放入缓存: userId={}", user.getId());

        logUserAction(user, "CREATE", null, null, null);

        executeAsyncTasks(user);
    }

    /**
     * 处理用户更新成功后的操作
     *
     * @param user 更新的用户
     */
    private void handleUpdateSuccess(User user) {
        userCacheService.updateUserInCache(user);
        log.info("用户缓存已更新: userId={}", user.getId());

        logUserAction(user, "UPDATE", null, null, null);

        asyncExecutor.executeAsync("记录用户操作日志",
                () -> userAsyncService.logUserActionAsync(
                        user.getId(),
                        user.getUsername(),
                        "UPDATE_ASYNC",
                        String.format("用户信息更新: status=%s, email=%s", user.getStatus(), user.getEmail()),
                        null,
                        null
                ));

        asyncExecutor.executeAsync("更新用户Elasticsearch索引",
                () -> userSearchService.syncUserToEs(user.getId()));
    }

    /**
     * 处理用户删除成功后的操作
     *
     * @param user 删除的用户
     */
    private void handleRemoveSuccess(User user) {
        userCacheService.deleteUserFromCache(user.getId());
        log.info("用户缓存已删除: userId={}", user.getId());

        logUserAction(user, "DELETE", null, null, null);

        asyncExecutor.executeAsync("删除用户Elasticsearch索引",
                () -> userSearchService.deleteUserIndex(user.getId()));
    }

    /**
     * 执行异步任务
     *
     * @param user 用户对象
     */
    private void executeAsyncTasks(User user) {
        asyncExecutor.executeAsync("发送欢迎邮件",
                () -> userAsyncService.sendWelcomeEmailAsync(user.getId()));

        asyncExecutor.executeAsync("索引用户到Elasticsearch",
                () -> userSearchService.indexUser(user.getId()));
    }
}