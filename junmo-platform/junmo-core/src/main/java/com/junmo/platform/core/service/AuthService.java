package com.junmo.platform.core.service;

import cn.dev33.satoken.stp.StpUtil;
import com.junmo.platform.api.dto.AuthResult;
import com.junmo.platform.api.dto.UserDTO;
import com.junmo.platform.common.exception.BusinessException;
import com.junmo.platform.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 认证服务
 * 
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Transactional
    public AuthResult login(String username, String password) {
        log.info("用户登录认证: {}", username);

        try {
            // 查找用户
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return AuthResult.builder()
                    .success(false)
                    .message("用户名或密码错误")
                    .build();
            }

            User user = userOpt.get();

            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return AuthResult.builder()
                    .success(false)
                    .message("用户名或密码错误")
                    .build();
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                return AuthResult.builder()
                    .success(false)
                    .message("用户已被禁用")
                    .build();
            }

            // 更新最后登录时间
            updateLastLoginTime(user.getId().toString());

            // 使用Sa-Token登录
            StpUtil.login(user.getId());

            // 获取token
            String token = StpUtil.getTokenValue();

            return AuthResult.builder()
                .success(true)
                .token(token)
                .tokenType("Bearer")
                .expiresIn(StpUtil.getTokenTimeout())
                .user(buildUserDTO(user))
                .message("登录成功")
                .build();

        } catch (BusinessException e) {
            log.error("用户登录失败(业务异常): username={}, error={}", username, e.getMessage(), e);
            return AuthResult.builder()
                .success(false)
                .message("登录失败: " + e.getMessage())
                .build();
        } catch (IllegalArgumentException e) {
            log.error("用户登录失败(参数异常): username={}, error={}", username, e.getMessage(), e);
            return AuthResult.builder()
                .success(false)
                .message("登录失败: 参数错误 - " + e.getMessage())
                .build();
        } catch (RuntimeException e) {
            log.error("用户登录失败(运行时异常): username={}, error={}", username, e.getMessage(), e);
            return AuthResult.builder()
                .success(false)
                .message("登录失败: " + e.getMessage())
                .build();
        } catch (Exception e) {
            log.error("用户登录失败(未知异常): username={}", username, e);
            return AuthResult.builder()
                .success(false)
                .message("登录失败: 系统错误")
                .build();
        }
    }

    /**
     * 用户注册
     */
    @Transactional
    public AuthResult register(String username, String password, String email, String realName) {
        log.info("用户注册: {}", username);

        // 检查用户名是否已存在
        if (userService.existsByUsername(username)) {
            return AuthResult.builder()
                .success(false)
                .message("用户名已存在")
                .build();
        }

        // 检查邮箱是否已存在
        if (userService.existsByEmail(email)) {
            return AuthResult.builder()
                .success(false)
                .message("邮箱已存在")
                .build();
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRealName(realName);
        user.setStatus(1);
        user.setCreateBy("system");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdateBy("system");
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(0);
        user.setVersion(1);

        userService.save(user);

        return AuthResult.builder()
            .success(true)
            .message("注册成功")
            .build();
    }

    /**
     * 刷新Token
     */
    public AuthResult refreshToken() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            return AuthResult.builder()
                .success(false)
                .message("用户未登录")
                .build();
        }

        Object loginId = StpUtil.getLoginId();
        String userId = loginId instanceof Long ? loginId.toString() : loginId.toString();
        Optional<User> userOpt = userService.getOptById(userId);
        if (userOpt.isEmpty()) {
            return AuthResult.builder()
                .success(false)
                .message("用户不存在")
                .build();
        }

        // 刷新token
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());
        String token = StpUtil.getTokenValue();

        return AuthResult.builder()
            .success(true)
            .token(token)
            .tokenType("Bearer")
            .expiresIn(StpUtil.getTokenTimeout())
            .user(buildUserDTO(userOpt.get()))
            .message("Token刷新成功")
            .build();
    }

    /**
     * 获取当前用户信息
     */
    public UserDTO getCurrentUser() {
        if (!StpUtil.isLogin()) {
            return null;
        }

        Object loginId = StpUtil.getLoginId();
        String userId = loginId instanceof Long ? loginId.toString() : loginId.toString();
        Optional<User> userOpt = userService.getOptById(userId);
        return userOpt.map(this::buildUserDTO).orElse(null);
    }

    /**
     * 修改密码
     */
    @Transactional
    public AuthResult changePassword(String oldPassword, String newPassword) {
        if (!StpUtil.isLogin()) {
            return AuthResult.builder()
                .success(false)
                .message("用户未登录")
                .build();
        }

        Object loginId = StpUtil.getLoginId();
        String userId = loginId instanceof Long ? loginId.toString() : loginId.toString();
        Optional<User> userOpt = userService.getOptById(userId);
        if (userOpt.isEmpty()) {
            return AuthResult.builder()
                .success(false)
                .message("用户不存在")
                .build();
        }

        User user = userOpt.get();

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return AuthResult.builder()
                .success(false)
                .message("原密码错误")
                .build();
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userService.updateById(user);

        return AuthResult.builder()
            .success(true)
            .message("密码修改成功")
            .build();
    }

    /**
     * 退出登录
     */
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 更新最后登录时间
     */
    private void updateLastLoginTime(String userId) {
        Optional<User> userOpt = userService.getOptById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUpdatedAt(LocalDateTime.now());
            userService.updateById(user);
        }
    }

    /**
     * 构建用户DTO
     */
    private UserDTO buildUserDTO(User user) {
        return UserDTO.builder()
            .id(user.getId() != null ? user.getId().toString() : null)
            .username(user.getUsername())
            .email(user.getEmail())
            .realName(user.getRealName())
            .avatar(user.getAvatar())
            .deptId(user.getDeptId() != null ? user.getDeptId().toString() : null)
            .status(user.getStatus())
            .build();
    }
}