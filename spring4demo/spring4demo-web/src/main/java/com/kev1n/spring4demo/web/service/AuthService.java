package com.kev1n.spring4demo.web.service;

import com.kev1n.spring4demo.common.security.UserPrincipal;
import com.kev1n.spring4demo.common.util.JwtUtils;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @Transactional
    public AuthResult login(String username, String password) {
        log.info("用户登录认证: {}", username);

        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // 更新最后登录时间
            updateLastLoginTime(userPrincipal.getId());

            // 生成JWT Token
            String token = jwtUtils.generateToken(userPrincipal);

            return AuthResult.builder()
                .success(true)
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .user(buildUserDTO(userPrincipal))
                .message("登录成功")
                .build();

        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return AuthResult.builder()
                .success(false)
                .message("用户名或密码错误")
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
        if (userRepository.existsByUsername(username)) {
            return AuthResult.builder()
                .success(false)
                .message("用户名已存在")
                .build();
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
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

        userRepository.save(user);

        return AuthResult.builder()
            .success(true)
            .message("注册成功")
            .build();
    }

    /**
     * 刷新Token
     */
    public AuthResult refreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // 生成新的JWT Token
        String newToken = jwtUtils.generateToken(userPrincipal);

        return AuthResult.builder()
            .success(true)
            .token(newToken)
            .tokenType("Bearer")
            .expiresIn(jwtUtils.getExpirationTime())
            .user(buildUserDTO(userPrincipal))
            .message("Token刷新成功")
            .build();
    }

    /**
     * 获取当前用户信息
     */
    public UserDTO getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return buildUserDTO(userPrincipal);
    }

    /**
     * 修改密码
     */
    @Transactional
    public AuthResult changePassword(String username, String oldPassword, String newPassword) {
        log.info("用户修改密码: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);
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
        userRepository.save(user);

        return AuthResult.builder()
            .success(true)
            .message("密码修改成功")
            .build();
    }

    /**
     * 更新最后登录时间
     */
    private void updateLastLoginTime(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    /**
     * 构建用户DTO
     */
    private UserDTO buildUserDTO(UserPrincipal userPrincipal) {
        return UserDTO.builder()
            .id(userPrincipal.getId())
            .username(userPrincipal.getUsername())
            .email(userPrincipal.getEmail())
            .realName(userPrincipal.getRealName())
            .avatar(userPrincipal.getAvatar())
            .deptId(userPrincipal.getDeptId())
            .authorities(userPrincipal.getAuthorities())
            .build();
    }

    /**
     * 认证结果
     */
    @lombok.Builder
    @lombok.Data
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
    @lombok.Builder
    @lombok.Data
    public static class UserDTO {
        private String id;
        private String username;
        private String email;
        private String realName;
        private String avatar;
        private String deptId;
        private java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities;
    }
}