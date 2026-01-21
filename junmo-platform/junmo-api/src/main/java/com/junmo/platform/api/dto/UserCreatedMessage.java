package com.junmo.platform.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户创建消息.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID.
     */
    private Long userId;

    /**
     * 用户名.
     */
    private String username;

    /**
     * 邮箱.
     */
    private String email;

    /**
     * 真实姓名.
     */
    private String realName;

    /**
     * 创建时间.
     */
    private LocalDateTime createdAt;
}