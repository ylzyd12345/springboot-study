package com.junmo.platform.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户状态消息.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusMessage implements Serializable {

    /**
     * 用户ID.
     */
    private Long userId;

    /**
     * 用户名.
     */
    private String username;

    /**
     * 状态 (ONLINE, OFFLINE, BUSY, AWAY).
     */
    private String status;

    /**
     * 创建时间.
     */
    private LocalDateTime createdAt;

    public UserStatusMessage(Long userId, String status) {
        this.userId = userId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public UserStatusMessage(Long userId, String username, String status) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}