package com.kev1n.spring4demo.core.graphql;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL变更解析器
 * 处理所有GraphQL变更请求
 * 使用Spring Boot 4.0.1原生GraphQL支持
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Controller
public class UserMutationResolver {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     *
     * @param input 用户输入
     * @return 创建的用户信息
     */
    @MutationMapping
    public User createUser(@Argument UserInput input) {
        log.info("GraphQL创建用户: username={}", input.getUsername());
        
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(input.getPassword());
        user.setEmail(input.getEmail());
        user.setPhone(input.getPhone());
        user.setRealName(input.getRealName());
        user.setAvatar(input.getAvatar());
        user.setStatus(input.getStatus());
        user.setDeptId(input.getDeptId());
        user.setDeleted(0);
        user.setVersion(1);
        
        userService.save(user);
        return user;
    }

    /**
     * 更新用户
     *
     * @param input 用户更新输入
     * @return 更新后的用户信息
     */
    @MutationMapping
    public User updateUser(@Argument UserUpdateInput input) {
        log.info("GraphQL更新用户: id={}", input.getId());
        
        User user = userService.getById(Long.parseLong(input.getId()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (input.getUsername() != null) {
            user.setUsername(input.getUsername());
        }
        if (input.getEmail() != null) {
            user.setEmail(input.getEmail());
        }
        if (input.getPhone() != null) {
            user.setPhone(input.getPhone());
        }
        if (input.getRealName() != null) {
            user.setRealName(input.getRealName());
        }
        if (input.getAvatar() != null) {
            user.setAvatar(input.getAvatar());
        }
        if (input.getStatus() != null) {
            user.setStatus(input.getStatus());
        }
        if (input.getDeptId() != null) {
            user.setDeptId(input.getDeptId());
        }
        
        userService.updateById(user);
        return user;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        log.info("GraphQL删除用户: id={}", id);
        return userService.removeById(Long.parseLong(id));
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    @MutationMapping
    public Boolean deleteUsers(@Argument List<String> ids) {
        log.info("GraphQL批量删除用户: count={}", ids.size());
        return userService.removeByIds(ids.stream().map(Long::parseLong).toList());
    }

    /**
     * 用户输入DTO
     */
    public static class UserInput {
        private String username;
        private String password;
        private String email;
        private String phone;
        private String realName;
        private String avatar;
        private Integer status;
        private String deptId;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getDeptId() { return deptId; }
        public void setDeptId(String deptId) { this.deptId = deptId; }
    }

    /**
     * 用户更新输入DTO
     */
    public static class UserUpdateInput {
        private String id;
        private String username;
        private String email;
        private String phone;
        private String realName;
        private String avatar;
        private Integer status;
        private String deptId;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getDeptId() { return deptId; }
        public void setDeptId(String deptId) { this.deptId = deptId; }
    }
}