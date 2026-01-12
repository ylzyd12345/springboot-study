package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.api.dto.ApiResponse;
import com.kev1n.spring4demo.api.dto.PageResponse;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kev1n.spring4demo.web.annotation.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理API v2控制器
 * 提供用户管理的v2版本API接口
 * 相比v1版本，增加了分页查询、状态过滤等功能
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "用户管理API v2", description = "用户管理的v2版本API接口")
@ApiVersion(value = 2, type = ApiVersion.ApiVersionType.URL_PATH)
public class UserV2Controller {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 用户状态（可选）
     * @return 分页用户列表
     */
    @Operation(summary = "分页查询用户", description = "v2版本：分页查询用户信息，支持状态过滤")
    @GetMapping
    public ApiResponse<PageResponse<User>> getUsers(
            @Parameter(description = "页码，从0开始", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户状态（可选）", example = "1")
            @RequestParam(required = false) Integer status) {
        log.info("v2版本分页查询用户: page={}, size={}, status={}", page, size, status);
        
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        Page<User> result = userService.page(pageParam, queryWrapper);
        PageResponse<User> pageResponse = PageResponse.of(result);
        
        return ApiResponse.success(pageResponse);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID查询用户", description = "v2版本：根据ID查询用户信息")
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("v2版本查询用户: id={}", id);
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(user);
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建的用户信息
     */
    @Operation(summary = "创建用户", description = "v2版本：创建新用户，增加数据验证")
    @PostMapping
    public ApiResponse<User> createUser(
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        log.info("v2版本创建用户: username={}", user.getUsername());
        
        // 数据验证
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        
        userService.save(user);
        return ApiResponse.success(user, "用户创建成功");
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户", description = "v2版本：更新用户信息，增加数据验证")
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        log.info("v2版本更新用户: id={}", id);
        
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        user.setId(id);
        userService.updateById(user);
        return ApiResponse.success(user, "用户更新成功");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Operation(summary = "删除用户", description = "v2版本：删除用户，增加逻辑删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("v2版本删除用户: id={}", id);
        
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        boolean result = userService.removeById(id);
        if (result) {
            return ApiResponse.success(true, "用户删除成功");
        } else {
            return ApiResponse.error(500, "用户删除失败");
        }
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    @Operation(summary = "批量删除用户", description = "v2版本：批量删除用户")
    @DeleteMapping("/batch")
    public ApiResponse<Boolean> deleteUsers(
            @Parameter(description = "用户ID列表", required = true)
            @RequestBody List<Long> ids) {
        log.info("v2版本批量删除用户: count={}", ids.size());
        boolean result = userService.removeByIds(ids);
        if (result) {
            return ApiResponse.success(true, "批量删除成功");
        } else {
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    /**
     * 根据状态查询用户
     *
     * @param status 用户状态
     * @return 用户列表
     */
    @Operation(summary = "根据状态查询用户", description = "v2版本：根据状态查询用户")
    @GetMapping("/status/{status}")
    public ApiResponse<List<User>> getUsersByStatus(
            @Parameter(description = "用户状态", required = true)
            @PathVariable Integer status) {
        log.info("v2版本根据状态查询用户: status={}", status);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStatus, status);
        List<User> users = userService.list(queryWrapper);
        
        return ApiResponse.success(users);
    }
}