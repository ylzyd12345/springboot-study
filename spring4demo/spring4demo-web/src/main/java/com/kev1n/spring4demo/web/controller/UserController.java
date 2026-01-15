package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器（路由委托）
 *
 * <p>负责路由用户相关的请求到对应的专用控制器。
 * 本控制器不包含业务逻辑，仅作为路由入口。</p>
 *
 * @author spring4demo
 * @version 2.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户管理", description = "用户管理相关接口，提供用户CRUD和查询功能")
public class UserController {

    private final UserQueryController userQueryController;
    private final UserCommandController userCommandController;
    private final UserStatusController userStatusController;

    /**
     * 创建用户（委托给UserCommandController）
     *
     * @param request 用户创建请求
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建用户",
               description = "创建新用户，需要管理员权限。会验证用户名和邮箱唯一性")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Parameter(description = "用户创建信息")
            @Valid @RequestBody UserCreateRequest request) {
        return userCommandController.createUser(request);
    }

    /**
     * 分页获取用户列表（委托给UserQueryController）
     *
     * @param request 查询请求参数
     * @return 分页用户列表
     */
    @GetMapping
    @Operation(summary = "获取用户列表",
               description = "分页获取用户列表，支持按条件筛选和排序")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @Parameter(description = "查询参数") @ModelAttribute UserQueryRequest request) {
        return userQueryController.getUsers(request);
    }

    /**
     * 根据ID获取用户信息（委托给UserQueryController）
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情",
               description = "根据用户ID获取详细信息")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userQueryController.getUserById(id);
    }

    /**
     * 更新用户信息（委托给UserCommandController）
     *
     * @param id 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息",
               description = "更新指定用户的信息，需要用户更新权限")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户更新信息")
            @Valid @RequestBody UserUpdateRequest request) {
        return userCommandController.updateUser(id, request);
    }

    /**
     * 删除用户（委托给UserCommandController）
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户",
               description = "删除指定用户，需要管理员权限")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return userCommandController.deleteUser(id);
    }

    /**
     * 批量删除用户（委托给UserCommandController）
     *
     * @param ids 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户",
               description = "批量删除多个用户，需要管理员权限")
    public ResponseEntity<ApiResponse<Void>> batchDeleteUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        return userCommandController.batchDeleteUsers(ids);
    }

    /**
     * 启用/禁用用户（委托给UserStatusController）
     *
     * @param id 用户ID
     * @param status 状态（1-启用，0-禁用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态",
               description = "启用或禁用用户")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户状态：1-启用，0-禁用")
            @RequestParam Integer status) {
        return userStatusController.updateUserStatus(id, status);
    }
}