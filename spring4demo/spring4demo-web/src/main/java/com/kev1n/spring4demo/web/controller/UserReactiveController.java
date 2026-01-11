package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.api.dto.UserVO;
import com.kev1n.spring4demo.api.enums.UserStatus;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 用户响应式控制器
 * 提供响应式编程风格的API接口
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Tag(name = "用户响应式API", description = "用户响应式编程接口")
@Slf4j
@RestController
@RequestMapping("/api/reactive/users")
@RequiredArgsConstructor
public class UserReactiveController {

    private final UserService userService;

    /**
     * 响应式查询用户列表
     * 使用Flux处理多个用户数据，支持背压
     *
     * @return 用户列表Flux
     */
    @Operation(summary = "响应式查询用户列表", description = "使用Flux返回所有用户数据")
    @GetMapping
    public Flux<UserVO> listUsers() {
        log.info("响应式查询用户列表");
        return userService.listUsersReactive()
                .map(this::convertToVO)
                .doOnComplete(() -> log.info("用户列表查询完成"));
    }

    /**
     * 响应式查询单个用户
     * 使用Mono处理单个用户数据
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    @Operation(summary = "响应式查询单个用户", description = "使用Mono返回单个用户数据")
    @GetMapping("/{id}")
    public Mono<UserVO> getUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("响应式查询用户: {}", id);
        return userService.getUserByIdReactive(id)
                .map(this::convertToVO)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + id)));
    }

    /**
     * 响应式创建用户
     * 使用Mono处理异步创建操作
     *
     * @param dto 用户创建DTO
     * @return 创建后的用户Mono
     */
    @Operation(summary = "响应式创建用户", description = "使用Mono异步创建用户")
    @PostMapping
    public Mono<UserVO> createUser(
            @Parameter(description = "用户创建DTO", required = true)
            @Valid @RequestBody UserCreateDTO dto) {
        log.info("响应式创建用户: {}", dto.getUsername());
        return userService.createUserReactive(dto)
                .map(this::convertToVO);
    }

    /**
     * 响应式批量创建用户
     * 使用Flux处理批量数据
     *
     * @param dtos 用户创建DTO列表
     * @return 创建后的用户列表Flux
     */
    @Operation(summary = "响应式批量创建用户", description = "使用Flux批量创建用户")
    @PostMapping("/batch")
    public Flux<UserVO> batchCreate(
            @Parameter(description = "用户创建DTO列表", required = true)
            @Valid @RequestBody java.util.List<UserCreateDTO> dtos) {
        log.info("响应式批量创建用户: {}", dtos.size());
        return Flux.fromIterable(dtos)
                .flatMap(userService::createUserReactive)
                .map(this::convertToVO);
    }

    /**
     * 响应式流式数据推送
     * 使用ServerSentEvent实现实时数据推送
     *
     * @return 用户流Flux
     */
    @Operation(summary = "响应式流式推送用户数据", description = "使用SSE实时推送用户数据")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UserVO>> streamUsers() {
        log.info("响应式流式推送用户数据");
        return userService.streamUsersReactive()
                .map(user -> ServerSentEvent.<UserVO>builder()
                        .data(convertToVO(user))
                        .id(String.valueOf(user.getId()))
                        .event("user-update")
                        .build())
                .delayElements(Duration.ofSeconds(1));
    }

    /**
     * 响应式根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    @Operation(summary = "响应式根据用户名查询用户", description = "使用Mono根据用户名查询用户")
    @GetMapping("/username/{username}")
    public Mono<UserVO> getUserByUsername(
            @Parameter(description = "用户名", required = true)
            @PathVariable String username) {
        log.info("响应式根据用户名查询用户: {}", username);
        return userService.getUserByUsernameReactive(username)
                .map(this::convertToVO)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + username)));
    }

    /**
     * 响应式根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    @Operation(summary = "响应式根据邮箱查询用户", description = "使用Mono根据邮箱查询用户")
    @GetMapping("/email/{email}")
    public Mono<UserVO> getUserByEmail(
            @Parameter(description = "邮箱", required = true)
            @PathVariable String email) {
        log.info("响应式根据邮箱查询用户: {}", email);
        return userService.getUserByEmailReactive(email)
                .map(this::convertToVO)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户不存在: " + email)));
    }

    /**
     * 响应式根据状态查询用户列表
     *
     * @param status 用户状态
     * @return 用户列表Flux
     */
    @Operation(summary = "响应式根据状态查询用户列表", description = "使用Flux根据状态查询用户")
    @GetMapping("/status/{status}")
    public Flux<UserVO> getUsersByStatus(
            @Parameter(description = "用户状态", required = true)
            @PathVariable Integer status) {
        log.info("响应式根据状态查询用户列表: {}", status);
        return userService.getUsersByStatusReactive(status)
                .map(this::convertToVO);
    }

    /**
     * 响应式统计指定状态的用户数量
     *
     * @param status 用户状态
     * @return 用户数量Mono
     */
    @Operation(summary = "响应式统计用户数量", description = "使用Mono统计指定状态的用户数量")
    @GetMapping("/count/{status}")
    public Mono<Long> countByStatus(
            @Parameter(description = "用户状态", required = true)
            @PathVariable Integer status) {
        log.info("响应式统计用户数量: {}", status);
        return userService.countByStatusReactive(status);
    }

    /**
     * TODO: 响应式查询用户订单列表
     * 待实现订单模块后补充
     *
     * @param userId 用户ID
     * @return 订单列表Flux
     */
    @Operation(summary = "响应式查询用户订单列表", description = "待实现")
    @GetMapping("/{userId}/orders")
    public Flux<String> getUserOrders(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        log.info("响应式查询用户订单列表: {}", userId);
        // TODO: 待实现订单模块后补充
        return Flux.error(new UnsupportedOperationException("订单模块待实现"));
    }

    /**
     * 将User实体转换为UserVO
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // 转换状态枚举
        if (user.getStatus() != null) {
            vo.setStatus(UserStatus.fromValue(user.getStatus()));
        }
        return vo;
    }
}