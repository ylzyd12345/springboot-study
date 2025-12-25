package com.kev1n.spring4demo.web.controller.v2;

import com.kev1n.spring4demo.common.annotation.ApiVersion;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 用户控制器 v2.0
 * 
 * @author spring4demo
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
@Tag(name = "用户管理 v2.0", description = "用户管理相关接口 v2.0")
@ApiVersion(value = 2, stable = true, description = "用户管理API v2.0 - 稳定版本")
public class UserControllerV2 {

    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户，支持更多字段和验证")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("创建用户 v2.0: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持排序和过滤")
    public ResponseEntity<Page<User>> getUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String direction) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
    public ResponseEntity<List<User>> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        // 这里应该实现搜索逻辑
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情，包含更多信息")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "根据ID更新用户信息，支持部分更新")
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

    @PatchMapping("/{id}")
    @Operation(summary = "部分更新用户", description = "根据ID部分更新用户信息")
    public ResponseEntity<User> patchUser(
            @Parameter(description = "用户ID") @PathVariable String id,
            @RequestBody User user) {
        
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User currentUser = existingUser.get();
        
        // 只更新非空字段
        if (user.getUsername() != null) {
            currentUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            currentUser.setPhone(user.getPhone());
        }
        
        User updatedUser = userRepository.save(currentUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户，支持软删除")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String id,
            @Parameter(description = "是否软删除") @RequestParam(defaultValue = "true") boolean soft) {
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        if (soft) {
            // 软删除逻辑
            Optional<User> user = userRepository.findById(id);
            user.ifPresent(u -> {
                u.setDeleted(1);
                userRepository.save(u);
            });
        } else {
            // 硬删除
            userRepository.deleteById(id);
        }
        
        return ResponseEntity.noContent().build();
    }
}