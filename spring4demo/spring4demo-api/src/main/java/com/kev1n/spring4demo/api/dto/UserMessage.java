package com.kev1n.spring4demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户消息.
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage implements Serializable {

    /**
     * 消息ID.
     */
    private Long messageId;

    /**
     * 发送者用户ID.
     */
    private Long senderId;

    /**
     * 发送者用户名.
     */
    private String senderName;

    /**
     * 接收者用户ID.
     */
    private Long receiverId;

    /**
     * 接收者用户名.
     */
    private String receiverName;

    /**
     * 消息内容.
     */
    private String content;

    /**
     * 消息类型 (TEXT, IMAGE, FILE).
     */
    private String messageType;

    /**
     * 发送时间.
     */
    private LocalDateTime sentAt;

    public UserMessage(Long userId, String content) {
        this.senderId = userId;
        this.content = content;
        this.messageType = "TEXT";
        this.sentAt = LocalDateTime.now();
    }

    public UserMessage(Long senderId, String senderName, Long receiverId, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.messageType = "TEXT";
        this.sentAt = LocalDateTime.now();
    }
}