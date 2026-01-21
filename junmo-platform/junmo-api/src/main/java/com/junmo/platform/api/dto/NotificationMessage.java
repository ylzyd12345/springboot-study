package com.junmo.platform.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

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
     * 通知标题.
     */
    private String title;

    /**
     * 通知内容.
     */
    private String content;

    /**
     * 通知类型（SYSTEM-系统通知, EMAIL-邮件, SMS-短信, PUSH-推送）.
     */
    private String type;

    /**
     * 创建时间.
     */
    private LocalDateTime createdAt;
}