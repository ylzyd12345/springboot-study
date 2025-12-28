package com.kev1n.spring4demo.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kev1n.spring4demo.core.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(Integer status);

    /**
     * 统计指定状态的用户数量
     */
    long countByStatus(Integer status);

    /**
     * 查找最近创建的用户
     */
    List<User> findRecentActiveUsers();
}