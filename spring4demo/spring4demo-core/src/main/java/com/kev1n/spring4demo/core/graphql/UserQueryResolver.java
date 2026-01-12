package com.kev1n.spring4demo.core.graphql;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL查询解析器
 * 处理所有GraphQL查询请求
 * 使用Spring Boot 4.0.1原生GraphQL支持
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Controller
public class UserQueryResolver {

    @Autowired
    private UserService userService;

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @QueryMapping
    public User user(@Argument String id) {
        log.info("GraphQL查询用户: id={}", id);
        return userService.getById(Long.parseLong(id));
    }

    /**
     * 查询所有用户
     *
     * @param status 用户状态（可选）
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 用户列表
     */
    @QueryMapping
    public List<User> users(
            @Argument Integer status,
            @Argument Integer limit,
            @Argument Integer offset) {
        log.info("GraphQL查询用户列表: status={}, limit={}, offset={}", status, limit, offset);
        
        // 根据状态查询用户，并应用分页
        List<User> allUsers = status != null ? userService.findByStatus(status) : userService.list();
        
        // 应用分页
        int actualOffset = offset != null ? offset : 0;
        int actualLimit = limit != null ? limit : 10;
        
        int fromIndex = Math.min(actualOffset, allUsers.size());
        int toIndex = Math.min(fromIndex + actualLimit, allUsers.size());
        
        return allUsers.subList(fromIndex, toIndex);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @QueryMapping
    public User userByUsername(@Argument String username) {
        log.info("GraphQL根据用户名查询: username={}", username);
        return userService.findByUsername(username).orElse(null);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @QueryMapping
    public User userByEmail(@Argument String email) {
        log.info("GraphQL根据邮箱查询: email={}", email);
        return userService.findByEmail(email).orElse(null);
    }
}