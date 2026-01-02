package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import com.kev1n.spring4demo.web.dto.UserCreateRequest;
import com.kev1n.spring4demo.web.dto.UserUpdateRequest;
import com.kev1n.spring4demo.web.dto.UserQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户控制器
 * 
 * <p>提供用户管理的REST API，支持用户的增删改查、分页查询、条件搜索等功能。
 * 所有接口都经过权限验证，确保数据安全。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>用户创建、更新、删除</li>
 *   <li>用户信息查询（单个、批量、分页）</li>
 *   <li>用户条件搜索</li>
 *   <li>用户状态管理</li>
 * </ul>
 * 
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户管理", description = "用户管理相关接口，提供用户CRUD和查询功能")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     * 
     * <p>创建新用户，需要管理员权限。创建时会验证用户名和邮箱的唯一性，
     * 自动加密密码，设置默认状态。</p>
     * 
     * @param request 用户创建请求
     * @return 创建结果
     */
    @PostMapping
    @SaCheckRole("ADMIN")
    @Operation(summary = "创建用户",
               description = "创建新用户，需要管理员权限。会验证用户名和邮箱唯一性")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Parameter(description = "用户创建信息")
            @Valid @RequestBody UserCreateRequest request) {

        log.info("创建用户请求: username={}, email={}", request.getUsername(), request.getEmail());

        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_ALREADY_EXISTS.getCode(), "用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (StringUtils.hasText(request.getEmail()) && userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_ALREADY_EXISTS.getCode(), "邮箱已存在"));
            }

            // 创建用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRealName(request.getRealName());
            user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

            boolean result = userService.save(user);

            if (result) {
                log.info("用户创建成功: id={}, username={}", user.getId(), user.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("用户创建成功", user));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户创建失败"));
            }

        } catch (Exception e) {
            log.error("创建用户失败: username={}", request.getUsername(), e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，用户创建失败"));
        }
    }

    /**
     * 分页获取用户列表
     * 
     * @param request 查询请求参数
     * @return 分页用户列表
     */
    @GetMapping
    @SaCheckLogin
    @Operation(summary = "获取用户列表", 
               description = "分页获取用户列表，支持按条件筛选和排序")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @Parameter(description = "查询参数") @ModelAttribute UserQueryRequest request) {
        
        log.debug("获取用户列表: current={}, size={}, keyword={}", 
                request.getCurrent(), request.getSize(), request.getKeyword());
        
        try {
            // 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            
            // 关键字搜索（用户名、邮箱、真实姓名）
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = request.getKeyword().trim();
                queryWrapper.and(wrapper -> wrapper
                    .like("username", keyword)
                    .or()
                    .like("email", keyword)
                    .or()
                    .like("real_name", keyword)
                );
            }
            
            // 状态筛选
            if (request.getStatus() != null) {
                queryWrapper.eq("status", request.getStatus());
            }
            
            // 排序
            if (StringUtils.hasText(request.getSortBy())) {
                queryWrapper.orderBy(true, request.getAsc() != null && request.getAsc(), 
                                   request.getSortBy());
            } else {
                queryWrapper.orderByDesc("create_time");
            }
            
            // 分页查询
            Page<User> page = new Page<>(request.getCurrent(), request.getSize());
            Page<User> result = userService.page(page, queryWrapper);
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，查询失败"));
        }
    }

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    @Operation(summary = "获取用户详情", 
               description = "根据用户ID获取详细信息")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        
        log.debug("获取用户详情: id={}", id);
        
        try {
            Optional<User> userOpt = userService.getOptById(id);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(userOpt.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }
        } catch (Exception e) {
            log.error("获取用户详情失败: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，查询失败"));
        }
    }

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @SaCheckPermission("user:update")
    @Operation(summary = "更新用户信息", 
               description = "更新指定用户的信息，需要用户更新权限")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户更新信息") 
            @Valid @RequestBody UserUpdateRequest request) {
        
        log.info("更新用户信息: id={}, request={}", id, request);
        
        try {
            Optional<User> userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }
            
            User user = userOpt.get();
            
            // 检查邮箱唯一性（如果邮箱发生变化）
            if (StringUtils.hasText(request.getEmail()) && 
                !Objects.equals(user.getEmail(), request.getEmail())) {
                if (userService.existsByEmail(request.getEmail())) {
                    return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_ALREADY_EXISTS.getCode(), "邮箱已存在"));
                }
                user.setEmail(request.getEmail());
            }
            
            // 更新其他字段
            if (StringUtils.hasText(request.getPhone())) {
                user.setPhone(request.getPhone());
            }
            if (StringUtils.hasText(request.getRealName())) {
                user.setRealName(request.getRealName());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            
            boolean result = userService.updateById(user);
            
            if (result) {
                log.info("用户更新成功: id={}", id);
                return ResponseEntity.ok(ApiResponse.success("用户更新成功", user));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户更新失败"));
            }
            
        } catch (Exception e) {
            log.error("更新用户失败: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，更新失败"));
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @SaCheckRole("ADMIN")
    @Operation(summary = "删除用户", 
               description = "删除指定用户，需要管理员权限")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        
        log.info("删除用户: id={}", id);
        
        try {
            Optional<User> userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }
            
            // 软删除
            boolean result = userService.removeById(id);
            
            if (result) {
                log.info("用户删除成功: id={}", id);
                return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户删除失败"));
            }
            
        } catch (Exception e) {
            log.error("删除用户失败: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，删除失败"));
        }
    }

    /**
     * 批量删除用户
     * 
     * @param ids 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @SaCheckRole("ADMIN")
    @Operation(summary = "批量删除用户", 
               description = "批量删除多个用户，需要管理员权限")
    public ResponseEntity<ApiResponse<Void>> batchDeleteUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        
        log.info("批量删除用户: ids={}", ids);
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "用户ID列表不能为空"));
        }
        
        try {
            boolean result = userService.removeByIds(ids);
            
            if (result) {
                log.info("批量删除用户成功: count={}", ids.size());
                return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("批量删除失败"));
            }
            
        } catch (Exception e) {
            log.error("批量删除用户失败: ids={}", ids, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，删除失败"));
        }
    }

    /**
     * 启用/禁用用户
     * 
     * @param id 用户ID
     * @param status 状态（1-启用，0-禁用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("user:status")
    @Operation(summary = "更新用户状态", 
               description = "启用或禁用用户")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户状态：1-启用，0-禁用") 
            @RequestParam Integer status) {
        
        log.info("更新用户状态: id={}, status={}", id, status);
        
        try {
            Optional<User> userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }
            
            User user = userOpt.get();
            user.setStatus(status);
            
            boolean result = userService.updateById(user);
            
            if (result) {
                String action = status == 1 ? "启用" : "禁用";
                log.info("用户{}成功: id={}", action, id);
                return ResponseEntity.ok(ApiResponse.success("用户状态更新成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户状态更新失败"));
            }
            
        } catch (Exception e) {
            log.error("更新用户状态失败: id={}, status={}", id, status, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，操作失败"));
        }
    }
}