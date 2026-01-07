package com.kev1n.spring4demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUser implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 (ONLINE, BUSY, AWAY)
     */
    private String status;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 上线时间
     */
    private LocalDateTime onlineTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
}