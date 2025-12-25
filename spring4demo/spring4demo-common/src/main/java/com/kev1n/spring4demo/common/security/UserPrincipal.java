package com.kev1n.spring4demo.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户主体信息
 * 实现Spring Security的UserDetails接口
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private String id;
    
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String email;
    
    private String realName;
    
    private String avatar;
    
    private Integer status;
    
    private String deptId;
    
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 创建用户主体
     */
    public static UserPrincipal create(String id, String username, String password, String email, 
                                     String realName, String avatar, Integer status, String deptId,
                                     List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        
        return new UserPrincipal(id, username, password, email, realName, avatar, status, deptId, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != null && status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}