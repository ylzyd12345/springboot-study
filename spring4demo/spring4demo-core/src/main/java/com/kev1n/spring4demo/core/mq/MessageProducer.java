package com.kev1n.spring4demo.core.mq;

import com.kev1n.spring4demo.api.dto.NotificationMessage;
import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 消息生产者
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final StreamBridge streamBridge;

    /**
     * 发送用户创建消息到RabbitMQ
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param realName 真实姓名
     */
    public void sendUserCreatedMessageToRabbitMQ(Long userId, String username, String email, String realName) {
        UserCreatedMessage message = UserCreatedMessage.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .realName(realName)
                .createdAt(LocalDateTime.now())
                .build();

        boolean sent = streamBridge.send("userCreated-out-0", message);
        if (sent) {
            log.info("发送用户创建消息到RabbitMQ成功: {}", message);
        } else {
            log.error("发送用户创建消息到RabbitMQ失败: {}", message);
        }
    }

    /**
     * 发送用户创建消息到Kafka
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param realName 真实姓名
     */
    public void sendUserCreatedMessageToKafka(Long userId, String username, String email, String realName) {
        UserCreatedMessage message = UserCreatedMessage.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .realName(realName)
                .createdAt(LocalDateTime.now())
                .build();

        boolean sent = streamBridge.send("userCreatedKafka-out-0", message);
        if (sent) {
            log.info("发送用户创建消息到Kafka成功: {}", message);
        } else {
            log.error("发送用户创建消息到Kafka失败: {}", message);
        }
    }

    /**
     * 发送通知消息
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型（SYSTEM-系统通知, EMAIL-邮件, SMS-短信, PUSH-推送）
     */
    public void sendNotificationMessage(Long userId, String username, String title, String content, String type) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(userId)
                .username(username)
                .title(title)
                .content(content)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        boolean sent = streamBridge.send("notification-out-0", message);
        if (sent) {
            log.info("发送通知消息成功: {}", message);
        } else {
            log.error("发送通知消息失败: {}", message);
        }
    }
}