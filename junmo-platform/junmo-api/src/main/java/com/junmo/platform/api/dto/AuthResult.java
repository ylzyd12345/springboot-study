package com.junmo.platform.api.dto;

import com.junmo.platform.api.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

/**
 * 认证结果 DTO.
 *
 * @author junmo-platform
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
    private String message;
}