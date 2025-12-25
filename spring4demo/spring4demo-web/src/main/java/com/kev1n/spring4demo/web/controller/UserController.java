package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("创建用户: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public ResponseEntity<Page<User>> getUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "根据ID更新用户信息")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable String id,
            @Valid @RequestBody User user) {
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据用户名搜索用户")
    public ResponseEntity<Page<User>> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam String username,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "根据状态获取用户", description = "根据状态获取用户列表")
    public ResponseEntity<List<User>> getUsersByStatus(
            @Parameter(description = "用户状态") @PathVariable Integer status) {
        
        List<User> users = userRepository.findByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已存在")
    public ResponseEntity<Boolean> checkUsernameExists(
            @Parameter(description = "用户名") @RequestParam String username) {
        
        boolean exists = userRepository.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已存在")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "邮箱") @RequestParam String email) {
        
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}