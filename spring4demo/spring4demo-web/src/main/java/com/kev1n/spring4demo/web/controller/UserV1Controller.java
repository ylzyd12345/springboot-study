package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import com.kev1n.spring4demo.web.annotation.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理API v1控制器
 * 提供用户管理的v1版本API接口
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理API v1", description = "用户管理的v1版本API接口")
@ApiVersion(value = 1, type = ApiVersion.ApiVersionType.URL_PATH)
public class UserV1Controller {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Operation(summary = "查询所有用户", description = "v1版本：查询所有用户信息")
    @GetMapping
    public List<User> getAllUsers() {
        log.info("v1版本查询所有用户");
        return userService.list();
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID查询用户", description = "v1版本：根据ID查询用户信息")
    @GetMapping("/{id}")
    public User getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("v1版本查询用户: id={}", id);
        return userService.getById(id);
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建的用户信息
     */
    @Operation(summary = "创建用户", description = "v1版本：创建新用户")
    @PostMapping
    public User createUser(
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        log.info("v1版本创建用户: username={}", user.getUsername());
        userService.save(user);
        return user;
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户", description = "v1版本：更新用户信息")
    @PutMapping("/{id}")
    public User updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        log.info("v1版本更新用户: id={}", id);
        user.setId(id);
        userService.updateById(user);
        return user;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Operation(summary = "删除用户", description = "v1版本：删除用户")
    @DeleteMapping("/{id}")
    public Boolean deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        log.info("v1版本删除用户: id={}", id);
        return userService.removeById(id);
    }
}