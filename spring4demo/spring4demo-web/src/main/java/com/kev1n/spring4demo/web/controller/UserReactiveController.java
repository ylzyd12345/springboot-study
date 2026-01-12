package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserReactiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户响应式控制器
 * 使用WebFlux + R2DBC实现真正的响应式API端点
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/reactive/users")
@Tag(name = "用户响应式API", description = "用户管理的响应式API接口（使用R2DBC）")
public class UserReactiveController {

    @Autowired
    private UserReactiveService userReactiveService;

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    @Operation(summary = "根据ID查询用户", description = "使用R2DBC响应式编程根据ID查询用户信息")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("R2DBC响应式查询用户: id={}", id);
        return userReactiveService.findById(id);
    }

    /**
     * 查询所有用户
     *
     * @return 用户Flux
     */
    @Operation(summary = "查询所有用户", description = "使用R2DBC响应式编程查询所有用户信息")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getAllUsers() {
        log.info("R2DBC响应式查询所有用户");
        return userReactiveService.findAll();
    }

    /**
     * 创建用户
     *
     * @param user 用户实体
     * @return 创建后的用户Mono
     */
    @Operation(summary = "创建用户", description = "使用R2DBC响应式编程创建新用户")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> createUser(
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody User user) {
        log.info("R2DBC响应式创建用户: username={}", user.getUsername());
        return userReactiveService.save(user);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户实体
     * @return 更新后的用户Mono
     */
    @Operation(summary = "更新用户", description = "使用R2DBC响应式编程更新用户信息")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody User user) {
        log.info("R2DBC响应式更新用户: id={}", id);
        user.setId(id);
        return userReactiveService.update(user);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功Mono
     */
    @Operation(summary = "删除用户", description = "使用R2DBC响应式编程删除用户")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Boolean> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("R2DBC响应式删除用户: id={}", id);
        return userReactiveService.deleteById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功Mono
     */
    @Operation(summary = "批量删除用户", description = "使用R2DBC响应式编程批量删除用户")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Boolean> deleteUsers(
            @Parameter(description = "用户ID列表", required = true)
            @RequestBody List<Long> ids) {
        log.info("R2DBC响应式批量删除用户: count={}", ids.size());
        return userReactiveService.deleteAllById(ids);
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户Mono
     */
    @Operation(summary = "根据用户名查找用户", description = "使用R2DBC响应式编程根据用户名查找用户")
    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUserByUsername(
            @Parameter(description = "用户名", required = true)
            @PathVariable String username) {
        log.info("R2DBC响应式根据用户名查找: username={}", username);
        return userReactiveService.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户Mono
     */
    @Operation(summary = "根据邮箱查找用户", description = "使用R2DBC响应式编程根据邮箱查找用户")
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUserByEmail(
            @Parameter(description = "邮箱", required = true)
            @PathVariable String email) {
        log.info("R2DBC响应式根据邮箱查找: email={}", email);
        return userReactiveService.findByEmail(email);
    }

    /**
     * 根据状态查找用户
     *
     * @param status 状态
     * @return 用户Flux
     */
    @Operation(summary = "根据状态查找用户", description = "使用R2DBC响应式编程根据状态查找用户")
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getUsersByStatus(
            @Parameter(description = "状态", required = true)
            @PathVariable Integer status) {
        log.info("R2DBC响应式根据状态查找用户: status={}", status);
        return userReactiveService.findByStatus(status);
    }

    /**
     * 查找所有活跃用户
     *
     * @return 用户Flux
     */
    @Operation(summary = "查找所有活跃用户", description = "使用R2DBC响应式编程查找所有活跃用户")
    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getActiveUsers() {
        log.info("R2DBC响应式查找活跃用户");
        return userReactiveService.findActiveUsers();
    }

    /**
     * 根据部门ID查找用户
     *
     * @param deptId 部门ID
     * @return 用户Flux
     */
    @Operation(summary = "根据部门ID查找用户", description = "使用R2DBC响应式编程根据部门ID查找用户")
    @GetMapping(value = "/dept/{deptId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getUsersByDeptId(
            @Parameter(description = "部门ID", required = true)
            @PathVariable String deptId) {
        log.info("R2DBC响应式根据部门ID查找用户: deptId={}", deptId);
        return userReactiveService.findByDeptId(deptId);
    }

    /**
     * 统计用户数量
     *
     * @return 用户数量Mono
     */
    @Operation(summary = "统计用户数量", description = "使用R2DBC响应式编程统计用户数量")
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Long> countUsers() {
        log.info("R2DBC响应式统计用户数量");
        return userReactiveService.count();
    }

    /**
     * 根据状态统计用户数量
     *
     * @param status 状态
     * @return 用户数量Mono
     */
    @Operation(summary = "根据状态统计用户数量", description = "使用R2DBC响应式编程根据状态统计用户数量")
    @GetMapping(value = "/count/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Long> countUsersByStatus(
            @Parameter(description = "状态", required = true)
            @PathVariable Integer status) {
        log.info("R2DBC响应式根据状态统计用户数量: status={}", status);
        return userReactiveService.countByStatus(status);
    }

    /**
     * 批量创建用户
     *
     * @param users 用户列表
     * @return 创建后的用户Flux
     */
    @Operation(summary = "批量创建用户", description = "使用R2DBC响应式编程批量创建用户")
    @PostMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> createUsers(
            @Parameter(description = "用户列表", required = true)
            @Valid @RequestBody List<User> users) {
        log.info("R2DBC响应式批量创建用户: count={}", users.size());
        return userReactiveService.saveAll(users);
    }
}