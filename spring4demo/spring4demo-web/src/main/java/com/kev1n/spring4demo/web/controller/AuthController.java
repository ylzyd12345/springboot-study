package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.common.constant.ApiConstants;
import com.kev1n.spring4demo.core.repository.UserRepository;
import com.kev1n.spring4demo.core.security.UserPrincipal;
import com.kev1n.spring4demo.core.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回JWT Token")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());

        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // 生成JWT Token
            String token = jwtUtils.generateToken(userPrincipal);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("expiresIn", jwtUtils.getExpirationTime());
            response.put("user", buildUserInfo(userPrincipal));
            response.put("timestamp", LocalDateTime.now());
            response.put("code", ApiConstants.CODE_SUCCESS);
            response.put("message", "登录成功");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", ApiConstants.CODE_ERROR);
            errorResponse.put("message", "用户名或密码错误");
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用当前Token刷新获取新的Token")
    public ResponseEntity<Map<String, Object>> refreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // 生成新的JWT Token
        String newToken = jwtUtils.generateToken(userPrincipal);

        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("type", "Bearer");
        response.put("expiresIn", jwtUtils.getExpirationTime());
        response.put("timestamp", LocalDateTime.now());
        response.put("code", ApiConstants.CODE_SUCCESS);
        response.put("message", "Token刷新成功");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("user", buildUserInfo(userPrincipal));
        response.put("timestamp", LocalDateTime.now());
        response.put("code", ApiConstants.CODE_SUCCESS);
        response.put("message", "获取用户信息成功");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出（客户端清除Token）")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("code", ApiConstants.CODE_SUCCESS);
        response.put("message", "登出成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(UserPrincipal userPrincipal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getId());
        userInfo.put("username", userPrincipal.getUsername());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("realName", userPrincipal.getRealName());
        userInfo.put("avatar", userPrincipal.getAvatar());
        userInfo.put("deptId", userPrincipal.getDeptId());
        userInfo.put("authorities", userPrincipal.getAuthorities());
        return userInfo;
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}