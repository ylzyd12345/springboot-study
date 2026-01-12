package com.kev1n.spring4demo.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 认证结果 DTO.
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Builder
public class AuthResult {
    /**
         * 是否成功.
         */
        private boolean success;
    
        /**
         * Token.
         */
        private String token;
    
        /**
         * Token 类型.
         */
        private String tokenType;
    
        /**
         * 过期时间（秒）.
         */
        private Long expiresIn;
    
        /**
         * 用户信息.
         */
        private UserDTO user;
    
        /**
         * 消息.
         */
        private String message;}