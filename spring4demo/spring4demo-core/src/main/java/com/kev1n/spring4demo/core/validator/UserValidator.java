package com.kev1n.spring4demo.core.validator;

import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.api.dto.UserUpdateDTO;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户验证器
 *
 * <p>负责用户相关的验证逻辑，包括：
 * <ul>
 *   <li>用户名唯一性验证</li>
 *   <li>邮箱唯一性验证</li>
 *   <li>用户数据完整性验证</li>
 * </ul>
 * </p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserMapper userMapper;

    /**
     * 验证用户名是否已存在
     *
     * @param username 用户名
     * @return 如果用户名已存在返回true，否则返回false
     */
    public boolean isUsernameExists(String username) {
        return userMapper.existsByUsername(username);
    }

    /**
     * 验证邮箱是否已存在
     *
     * @param email 邮箱
     * @return 如果邮箱已存在返回true，否则返回false
     */
    public boolean isEmailExists(String email) {
        return userMapper.existsByEmail(email);
    }

    /**
     * 验证用户创建DTO
     *
     * @param dto 用户创建DTO
     * @throws IllegalArgumentException 如果验证失败
     */
    public void validateUserCreateDTO(UserCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("用户创建DTO不能为空");
        }

        if (isUsernameExists(dto.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + dto.getUsername());
        }

        if (isEmailExists(dto.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在: " + dto.getEmail());
        }
    }

    /**
     * 验证用户更新DTO
     *
     * @param dto 用户更新DTO
     * @throws IllegalArgumentException 如果验证失败
     */
    public void validateUserUpdateDTO(UserUpdateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("用户更新DTO不能为空");
        }

        if (dto.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
    }

    /**
     * 验证用户实体
     *
     * @param user 用户实体
     * @throws IllegalArgumentException 如果验证失败
     */
    public void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户实体不能为空");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
    }
}