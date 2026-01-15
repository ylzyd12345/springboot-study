package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.kev1n.spring4demo.api.dto.ApiResponse;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户状态控制器
 *
 * <p>负责用户状态管理操作，包括启用、禁用用户等。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户状态管理", description = "用户状态管理相关接口")
public class UserStatusController {

    private final UserService userService;

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
            var userOpt = userService.getOptById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ErrorCode.USER_NOT_FOUND.getCode(), "用户不存在"));
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

        } catch (IllegalArgumentException e) {
            log.error("用户状态参数错误: id={}, status={}", id, status, e);
            return ResponseEntity.ok(ApiResponse.error("用户状态参数错误"));
        } catch (DataAccessException e) {
            log.error("数据库操作失败: id={}, status={}", id, status, e);
            return ResponseEntity.ok(ApiResponse.error("系统繁忙，请稍后重试"));
        } catch (BusinessException e) {
            log.error("业务异常: id={}, status={}, error={}", id, status, e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("更新用户状态失败: id={}, status={}", id, status, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，操作失败"));
        }
    }
}