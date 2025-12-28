package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户控制器
 * 提供用户管理的REST API
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    @PostMapping
    @SaCheckRole("ADMIN")
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<Boolean> createUser(@Valid @RequestBody User user) {
        log.info("创建用户: {}", user.getUsername());
        boolean result = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public ResponseEntity<Page<User>> getUsers(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "create_time") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<User> page = new Page<>(current, size);
        Page<User> users = userService.page(page, null);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        User user = userService.getById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "根据ID更新用户信息")
    public ResponseEntity<Boolean> updateUser(
            @Parameter(description = "用户ID") @PathVariable String id,
            @Valid @RequestBody User user) {
        
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        user.setId(id);
        boolean result = userService.updateById(user);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("ADMIN")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResponseEntity<Boolean> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean result = userService.removeById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据用户名搜索用户")
    public ResponseEntity<Page<User>> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam String username,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<User> page = new Page<>(current, size);
        Page<User> users = userService.lambdaQuery()
                .like(User::getUsername, username)
                .page(page);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "根据状态获取用户", description = "根据状态获取用户列表")
    public ResponseEntity<List<User>> getUsersByStatus(
            @Parameter(description = "用户状态") @PathVariable Integer status) {
        
        List<User> users = userService.findByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已存在")
    public ResponseEntity<Boolean> checkUsernameExists(
            @Parameter(description = "用户名") @RequestParam String username) {
        
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已存在")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "邮箱") @RequestParam String email) {
        
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "统计用户数量", description = "根据状态统计用户数量")
    public ResponseEntity<Long> countUsersByStatus(
            @Parameter(description = "用户状态") @PathVariable Integer status) {
        
        long count = userService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最近活跃用户", description = "获取最近创建的活跃用户")
    public ResponseEntity<List<User>> getRecentActiveUsers() {
        List<User> users = userService.findRecentActiveUsers();
        return ResponseEntity.ok(users);
    }
}