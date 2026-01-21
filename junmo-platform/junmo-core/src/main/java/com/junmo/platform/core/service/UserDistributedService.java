package com.junmo.platform.core.service;

import com.junmo.platform.api.dto.UserCreateDTO;
import com.junmo.platform.common.exception.BusinessException;
import com.junmo.platform.model.entity.User;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户分布式事务服务
 *
 * 提供涉及多个数据表的分布式事务方法，使用Seata AT模式保证数据一致性
 *
 * @author junmo-platform
 * @version 2.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDistributedService {

    private final UserService userService;
    private final UserLogService userLogService;

    /**
     * 用户注册分布式事务
     *
     * 涉及的原子操作：
     * 1. 创建用户信息（sys_user表）
     * 2. 记录用户操作日志（sys_user_log表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param dto 用户创建DTO
     @return 创建的用户对象
     */
    @GlobalTransactional(name = "register-user", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserCreateDTO dto) {
        log.info("开始用户注册分布式事务: username={}", dto.getUsername());

        try {
            // 1. 创建用户信息
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setRealName(dto.getRealName());
            user.setStatus(1); // 默认状态：正常

            userService.save(user);
            log.info("用户信息创建成功: userId={}", user.getId());

            // 2. 记录用户注册日志
            userLogService.logUserAction(
                    user.getId(),
                    user.getUsername(),
                    "USER_REGISTER",
                    String.format("用户注册成功: username=%s, email=%s, phone=%s",
                            user.getUsername(), user.getEmail(), user.getPhone()),
                    null,
                    null
            );
            log.info("用户注册日志记录成功: userId={}", user.getId());

            log.info("用户注册分布式事务提交成功: userId={}", user.getId());
            return user;

        } catch (BusinessException e) {
            log.error("用户注册分布式事务失败(业务异常)，Seata将自动回滚: username={}, error={}",
                    dto.getUsername(), e.getMessage());
            throw e; // 业务异常直接向上抛出，保留原始异常类型
        } catch (RuntimeException e) {
            log.error("用户注册分布式事务失败(运行时异常)，Seata将自动回滚: username={}, error={}",
                    dto.getUsername(), e.getMessage(), e);
            throw new RuntimeException("用户注册失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("用户注册分布式事务失败(未知异常)，Seata将自动回滚: username={}",
                    dto.getUsername(), e);
            throw new RuntimeException("用户注册失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户信息更新分布式事务
     *
     * 涉及的原子操作：
     * 1. 更新用户信息（sys_user表）
     * 2. 记录用户操作日志（sys_user_log表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param phone 手机号
     * @return 更新后的用户对象
     */
    @GlobalTransactional(name = "update-user", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public User updateUserProfile(Long userId, String realName, String phone) {
        log.info("开始用户信息更新分布式事务: userId={}", userId);

        try {
            // 1. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: userId=" + userId);
            }

            // 2. 更新用户信息
            if (realName != null) {
                user.setRealName(realName);
            }
            if (phone != null) {
                user.setPhone(phone);
            }
            userService.updateById(user);
            log.info("用户信息更新成功: userId={}", userId);

            // 3. 记录用户操作日志
            userLogService.logUserAction(
                    user.getId(),
                    user.getUsername(),
                    "USER_UPDATE",
                    String.format("用户信息更新: realName=%s, phone=%s", realName, phone),
                    null,
                    null
            );
            log.info("用户更新日志记录成功: userId={}", userId);

            log.info("用户信息更新分布式事务提交成功: userId={}", userId);
            return user;

        } catch (BusinessException e) {
            log.error("用户信息更新分布式事务失败(业务异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage());
            throw e; // 业务异常直接向上抛出，保留原始异常类型
        } catch (IllegalArgumentException e) {
            log.error("用户信息更新分布式事务失败(参数异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage());
            throw new IllegalArgumentException("用户信息更新参数错误: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("用户信息更新分布式事务失败(运行时异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage(), e);
            throw new RuntimeException("用户信息更新失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("用户信息更新分布式事务失败(未知异常)，Seata将自动回滚: userId={}", userId, e);
            throw new RuntimeException("用户信息更新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户状态变更分布式事务
     *
     * 涉及的原子操作：
     * 1. 更新用户状态（sys_user表）
     * 2. 记录用户操作日志（sys_user_log表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 更新后的用户对象
     */
    @GlobalTransactional(name = "update-user-status", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public User updateUserStatus(Long userId, Integer status) {
        log.info("开始用户状态变更分布式事务: userId={}, status={}", userId, status);

        try {
            // 1. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: userId=" + userId);
            }

            // 2. 更新用户状态
            user.setStatus(status);
            userService.updateById(user);
            log.info("用户状态更新成功: userId={}, status={}", userId, status);

            // 3. 记录用户操作日志
            String action = status == 1 ? "USER_ACTIVATE" : "USER_DEACTIVATE";
            userLogService.logUserAction(
                    user.getId(),
                    user.getUsername(),
                    action,
                    String.format("用户状态更新: status=%d", status),
                    null,
                    null
            );
            log.info("用户状态日志记录成功: userId={}", userId);

            log.info("用户状态变更分布式事务提交成功: userId={}, status={}", userId, status);
            return user;

        } catch (BusinessException e) {
            log.error("用户状态变更分布式事务失败(业务异常)，Seata将自动回滚: userId={}, status={}, error={}",
                    userId, status, e.getMessage());
            throw e; // 业务异常直接向上抛出，保留原始异常类型
        } catch (IllegalArgumentException e) {
            log.error("用户状态变更分布式事务失败(参数异常)，Seata将自动回滚: userId={}, status={}, error={}",
                    userId, status, e.getMessage());
            throw new IllegalArgumentException("用户状态变更参数错误: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("用户状态变更分布式事务失败(运行时异常)，Seata将自动回滚: userId={}, status={}, error={}",
                    userId, status, e.getMessage(), e);
            throw new RuntimeException("用户状态变更失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("用户状态变更分布式事务失败(未知异常)，Seata将自动回滚: userId={}, status={}", userId, status, e);
            throw new RuntimeException("用户状态变更失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户删除分布式事务
     *
     * 涉及的原子操作：
     * 1. 逻辑删除用户（sys_user表）
     * 2. 记录用户操作日志（sys_user_log表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @GlobalTransactional(name = "delete-user", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        log.info("开始用户删除分布式事务: userId={}", userId);

        try {
            // 1. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: userId=" + userId);
            }

            // 2. 逻辑删除用户
            userService.removeById(userId);
            log.info("用户删除成功: userId={}", userId);

            // 3. 记录用户操作日志
            userLogService.logUserAction(
                    user.getId(),
                    user.getUsername(),
                    "USER_DELETE",
                    "用户被删除",
                    null,
                    null
            );
            log.info("用户删除日志记录成功: userId={}", userId);

            log.info("用户删除分布式事务提交成功: userId={}", userId);
            return true;

        } catch (BusinessException e) {
            log.error("用户删除分布式事务失败(业务异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage());
            throw e; // 业务异常直接向上抛出，保留原始异常类型
        } catch (IllegalArgumentException e) {
            log.error("用户删除分布式事务失败(参数异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage());
            throw new IllegalArgumentException("用户删除参数错误: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("用户删除分布式事务失败(运行时异常)，Seata将自动回滚: userId={}, error={}",
                    userId, e.getMessage(), e);
            throw new RuntimeException("用户删除失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("用户删除分布式事务失败(未知异常)，Seata将自动回滚: userId={}", userId, e);
            throw new RuntimeException("用户删除失败: " + e.getMessage(), e);
        }
    }
}