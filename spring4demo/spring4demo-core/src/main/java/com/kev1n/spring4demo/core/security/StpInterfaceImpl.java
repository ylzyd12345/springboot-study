package com.kev1n.spring4demo.core.security;

import cn.dev33.satoken.stp.StpInterface;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Sa-Token 权限接口实现
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // TODO: 从数据库查询用户权限
        // 临时实现，返回基本权限
        return List.of("user:list", "user:info", "user:update", "user:status");
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // TODO: 从数据库查询用户角色
        // 临时实现，返回基本角色
        return List.of("USER", "ADMIN");
    }
}