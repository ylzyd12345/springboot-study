package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.kev1n.spring4demo.api.dto.AuthResult;
import com.kev1n.spring4demo.api.dto.UserDTO;
import com.kev1n.spring4demo.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码登录")
    public ResponseEntity<AuthResult> login(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "密码") @RequestParam String password) {
        
        log.info("用户登录请求: {}", username);
        AuthResult result = authService.login(username, password);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<AuthResult> register(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "密码") @RequestParam String password,
            @Parameter(description = "邮箱") @RequestParam String email,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName) {
        
        log.info("用户注册请求: {}", username);
        AuthResult result = authService.register(username, password, email, realName);
        
        if (result.isSuccess()) {
            return ResponseEntity.status(201).body(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/refresh")
    @SaCheckLogin
    @Operation(summary = "刷新Token", description = "刷新当前用户的访问令牌")
    public ResponseEntity<AuthResult> refreshToken() {
        log.info("刷新Token请求: {}", StpUtil.getLoginId());
        AuthResult result = authService.refreshToken();
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/me")
    @SaCheckLogin
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String userId = StpUtil.getLoginId().toString();
        log.info("获取当前用户信息: {}", userId);
        
        UserDTO user = authService.getCurrentUser();
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/change-password")
    @SaCheckLogin
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public ResponseEntity<AuthResult> changePassword(
            @Parameter(description = "原密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        
        String userId = StpUtil.getLoginId().toString();
        log.info("用户修改密码: {}", userId);
        
        AuthResult result = authService.changePassword(oldPassword, newPassword);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/logout")
    @SaCheckLogin
    @Operation(summary = "用户退出", description = "用户退出登录")
    public ResponseEntity<String> logout() {
        String userId = StpUtil.getLoginId().toString();
        log.info("用户退出登录: {}", userId);
        
        authService.logout();
        return ResponseEntity.ok("退出成功");
    }

    @GetMapping("/is-login")
    @Operation(summary = "检查登录状态", description = "检查当前用户是否已登录")
    public ResponseEntity<Boolean> isLogin() {
        boolean isLogin = StpUtil.isLogin();
        return ResponseEntity.ok(isLogin);
    }
}