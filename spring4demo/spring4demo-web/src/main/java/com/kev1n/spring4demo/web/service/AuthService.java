package com.kev1n.spring4demo.web.service;

import cn.dev33.satoken.stp.StpUtil;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 认证服务
 * 
 * @author spring4demo
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
            updateLastLoginTime(user.getId());

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

        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return AuthResult.builder()
                .success(false)
                .message("登录失败")
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
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateBy("system");
        user.setUpdateTime(LocalDateTime.now());
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

        String userId = StpUtil.getLoginId().toString();
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

        String userId = StpUtil.getLoginId().toString();
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

        String userId = StpUtil.getLoginId().toString();
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
        user.setUpdateTime(LocalDateTime.now());
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
            user.setUpdateTime(LocalDateTime.now());
            userService.updateById(user);
        }
    }

    /**
     * 构建用户DTO
     */
    private UserDTO buildUserDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .realName(user.getRealName())
            .avatar(user.getAvatar())
            .deptId(user.getDeptId())
            .status(user.getStatus())
            .build();
    }

    /**
     * 认证结果
     */
    @Builder
    @Data
    public static class AuthResult {
        private boolean success;
        private String token;
        private String tokenType;
        private Long expiresIn;
        private UserDTO user;
        private String message;
    }

    /**
     * 用户DTO
     */
    @Builder
    @Data
    public static class UserDTO {
        private String id;
        private String username;
        private String email;
        private String realName;
        private String avatar;
        private String deptId;
        private Integer status;
    }
}