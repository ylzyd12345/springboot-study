package com.kev1n.spring4demo.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kev1n.spring4demo.core.annotation.RateLimit;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import com.kev1n.spring4demo.web.dto.UserQueryRequest;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户查询控制器
 *
 * <p>负责用户相关的查询操作，包括单个用户查询、分页查询、条件搜索等。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户查询", description = "用户查询相关接口")
public class UserQueryController {

    private final UserService userService;
    private final MeterRegistry meterRegistry;

    /**
     * 分页获取用户列表
     *
     * @param request 查询请求参数
     * @return 分页用户列表
     */
    @GetMapping
    @SaCheckLogin
    @RateLimit(key = "user:list", permits = 100)
    @Operation(summary = "获取用户列表",
               description = "分页获取用户列表，支持按条件筛选和排序")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @Parameter(description = "查询参数") @ModelAttribute UserQueryRequest request) {

        log.debug("获取用户列表: current={}, size={}, keyword={}",
                request.getCurrent(), request.getSize(), request.getKeyword());

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            // 监控深度分页
            if (request.getCurrent() > 1000) {
                log.warn("深度分页查询: current={}, size={}",
                        request.getCurrent(), request.getSize());
            }

            // 构建查询条件
            QueryWrapper<User> queryWrapper = buildQueryWrapper(request);

            // 分页查询
            Page<User> page = new Page<>(request.getCurrent(), request.getSize());
            Page<User> result = userService.page(page, queryWrapper);

            // 记录分页查询指标
            sample.stop(Timer.builder("pagination.query.duration")
                    .tag("entity", "user")
                    .tag("current", String.valueOf(request.getCurrent()))
                    .tag("size", String.valueOf(request.getSize()))
                    .register(meterRegistry));

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (IllegalArgumentException e) {
            return handleQueryError(sample, "查询参数错误", e);
        } catch (DataAccessException e) {
            return handleQueryError(sample, "系统繁忙，请稍后重试", e);
        } catch (Exception e) {
            return handleQueryError(sample, "系统异常，查询失败", e);
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
    @RateLimit(key = "user:detail", permits = 200)
    @Operation(summary = "获取用户详情",
               description = "根据用户ID获取详细信息")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {

        log.debug("获取用户详情: id={}", id);

        try {
            var userOpt = userService.getOptById(id);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(userOpt.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error(
                        com.kev1n.spring4demo.common.enums.ErrorCode.USER_NOT_FOUND.getCode(),
                        "用户不存在"));
            }
        } catch (IllegalArgumentException e) {
            log.error("用户ID参数错误: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("用户ID参数错误"));
        } catch (DataAccessException e) {
            log.error("数据库查询失败: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("系统繁忙，请稍后重试"));
        } catch (Exception e) {
            log.error("获取用户详情失败: id={}", id, e);
            return ResponseEntity.ok(ApiResponse.error("系统异常，查询失败"));
        }
    }

    /**
     * 构建查询条件
     *
     * @param request 查询请求参数
     * @return 查询包装器
     */
    private QueryWrapper<User> buildQueryWrapper(UserQueryRequest request) {
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

        return queryWrapper;
    }

    /**
     * 处理查询错误
     *
     * @param sample 计时器采样
     * @param message 错误消息
     * @param e 异常
     * @return 错误响应
     */
    private ResponseEntity<ApiResponse<Page<User>>> handleQueryError(
            Timer.Sample sample, String message, Exception e) {
        sample.stop(Timer.builder("pagination.query.duration")
                .tag("entity", "user")
                .tag("status", "error")
                .register(meterRegistry));
        log.error("获取用户列表失败: {}", message, e);
        return ResponseEntity.ok(ApiResponse.error(message));
    }
}