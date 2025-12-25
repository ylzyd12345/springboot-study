package com.kev1n.spring4demo.common.security;

import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户详情服务实现
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        if (user.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 这里应该从角色权限表查询用户的角色，暂时使用默认角色
        List<String> roles = getUserRoles(user.getId());
        
        return UserPrincipal.create(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRealName(),
                user.getAvatar(),
                user.getStatus(),
                user.getDeptId(),
                roles
        );
    }

    /**
     * 获取用户角色
     * TODO: 从角色权限表查询
     */
    private List<String> getUserRoles(String userId) {
        // 临时实现，实际应该从数据库查询
        return Arrays.asList("USER");
    }
}