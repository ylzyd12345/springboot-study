package com.kev1n.spring4demo.web.controller.v1;

import com.kev1n.spring4demo.common.annotation.ApiVersion;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * 用户控制器 v1.0
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理 v1.0", description = "用户管理相关接口 v1.0")
@ApiVersion(value = 1, description = "用户管理API v1.0")
public class UserControllerV1 {

    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("创建用户 v1.0: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
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
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String id) {
        
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}