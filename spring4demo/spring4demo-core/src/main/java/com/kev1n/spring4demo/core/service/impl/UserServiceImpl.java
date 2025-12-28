package com.kev1n.spring4demo.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Override
    public List<User> findByStatus(Integer status) {
        return userMapper.findByStatus(status);
    }

    @Override
    public long countByStatus(Integer status) {
        return userMapper.countByStatus(status);
    }

    @Override
    public List<User> findRecentActiveUsers() {
        return userMapper.findRecentActiveUsers();
    }
}