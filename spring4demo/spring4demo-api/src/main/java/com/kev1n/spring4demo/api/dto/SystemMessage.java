package com.kev1n.spring4demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统消息.
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemMessage implements Serializable {

    /**
     * 消息ID.
     */
    private Long messageId;

    /**
     * 消息标题.
     */
    private String title;

    /**
     * 消息内容.
     */
    private String content;

    /**
     * 消息类型 (NOTICE, WARNING, ERROR, INFO).
     */
    private String messageType;

    /**
     * 发送时间.
     */
    private LocalDateTime sentAt;

    public SystemMessage(String content) {
        this.content = content;
        this.messageType = "INFO";
        this.sentAt = LocalDateTime.now();
    }

    public SystemMessage(String title, String content, String messageType) {
        this.title = title;
        this.content = content;
        this.messageType = messageType;
        this.sentAt = LocalDateTime.now();
    }
}