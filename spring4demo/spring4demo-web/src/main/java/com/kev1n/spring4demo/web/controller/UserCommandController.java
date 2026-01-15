package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import com.kev1n.spring4demo.web.dto.UserCreateRequest;
import com.kev1n.spring4demo.web.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 用户命令控制器
 *
 * <p>负责用户相关的命令操作，包括创建、更新、删除等。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户命令", description = "用户命令相关接口")
public class UserCommandController {

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
                return ResponseEntity.ok(ApiResponse.error(
                        ErrorCode.USER_ALREADY_EXISTS.getCode(), "用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (StringUtils.hasText(request.getEmail()) && userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.ok(ApiResponse.error(
                        ErrorCode.USER_ALREADY_EXISTS.getCode(), "邮箱已存在"));
            }

            // 创建用户
            User user = buildUserFromRequest(request);
            boolean result = userService.save(user);

            if (result) {
                log.info("用户创建成功: id={}, username={}", user.getId(), user.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("用户创建成功", user));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户创建失败"));
            }

        } catch (DuplicateKeyException e) {
            return handleDuplicateKeyError("用户已存在: username=" + request.getUsername(), e);
        } catch (DataAccessException e) {
            return handleDataAccessError("数据库操作失败: username=" + request.getUsername(), e);
        } catch (BusinessException e) {
            return handleBusinessError("业务异常: username=" + request.getUsername(), e);
        } catch (Exception e) {
            return handleGenericError("创建用户失败: username=" + request.getUsername(), e);
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
            var userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }

            User user = userOpt.get();

            // 检查邮箱唯一性（如果邮箱发生变化）
            if (StringUtils.hasText(request.getEmail()) &&
                    !Objects.equals(user.getEmail(), request.getEmail())) {
                if (userService.existsByEmail(request.getEmail())) {
                    return ResponseEntity.ok(ApiResponse.error(
                            ErrorCode.USER_ALREADY_EXISTS.getCode(), "邮箱已存在"));
                }
                user.setEmail(request.getEmail());
            }

            // 更新其他字段
            updateUserFields(user, request);

            boolean result = userService.updateById(user);

            if (result) {
                log.info("用户更新成功: id={}", id);
                return ResponseEntity.ok(ApiResponse.success("用户更新成功", user));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户更新失败"));
            }

        } catch (DuplicateKeyException e) {
            return handleDuplicateKeyError("邮箱已存在: id=" + id, e);
        } catch (DataAccessException e) {
            return handleDataAccessError("数据库操作失败: id=" + id, e);
        } catch (BusinessException e) {
            return handleBusinessError("业务异常: id=" + id, e);
        } catch (Exception e) {
            return handleGenericError("更新用户失败: id=" + id, e);
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
            var userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
            }

            // 软删除
            boolean result = userService.removeById(id);

            if (result) {
                log.info("用户删除成功: id={}", id);
                return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户删除失败"));
            }

        } catch (DataAccessException e) {
            return handleDataAccessError("数据库操作失败: id=" + id, e);
        } catch (BusinessException e) {
            return handleBusinessError("业务异常: id=" + id, e);
        } catch (Exception e) {
            return handleGenericError("删除用户失败: id=" + id, e);
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
            return ResponseEntity.ok(ApiResponse.error(
                    ErrorCode.BAD_REQUEST.getCode(), "用户ID列表不能为空"));
        }

        try {
            boolean result = userService.removeByIds(ids);

            if (result) {
                log.info("批量删除用户成功: count={}", ids.size());
                return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.error("批量删除失败"));
            }

        } catch (IllegalArgumentException e) {
            log.error("批量删除用户失败: 参数错误, ids={}", ids, e);
            return ResponseEntity.ok(ApiResponse.error("批量删除失败，参数错误"));
        } catch (DataAccessException e) {
            return handleDataAccessError("数据库操作失败: ids=" + ids, e);
        } catch (BusinessException e) {
            return handleBusinessError("业务异常: ids=" + ids, e);
        } catch (Exception e) {
            return handleGenericError("批量删除用户失败: ids=" + ids, e);
        }
    }

    /**
     * 从请求构建用户对象
     *
     * @param request 用户创建请求
     * @return 用户对象
     */
    private User buildUserFromRequest(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        return user;
    }

    /**
     * 更新用户字段
     *
     * @param user 用户对象
     * @param request 更新请求
     */
    private void updateUserFields(User user, UserUpdateRequest request) {
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
    }

    /**
     * 处理重复键异常
     */
    private <T> ResponseEntity<ApiResponse<T>> handleDuplicateKeyError(String message, Exception e) {
        log.error(message, e);
        return ResponseEntity.ok(ApiResponse.error(
                ErrorCode.USER_ALREADY_EXISTS.getCode(), "用户已存在"));
    }

    /**
     * 处理数据访问异常
     */
    private <T> ResponseEntity<ApiResponse<T>> handleDataAccessError(String message, Exception e) {
        log.error(message, e);
        return ResponseEntity.ok(ApiResponse.error("系统繁忙，请稍后重试"));
    }

    /**
     * 处理业务异常
     */
    private <T> ResponseEntity<ApiResponse<T>> handleBusinessError(String message, BusinessException e) {
        log.error(message, e);
        return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
    }

    /**
     * 处理通用异常
     */
    private <T> ResponseEntity<ApiResponse<T>> handleGenericError(String message, Exception e) {
        log.error(message, e);
        return ResponseEntity.ok(ApiResponse.error("系统异常，操作失败"));
    }
}