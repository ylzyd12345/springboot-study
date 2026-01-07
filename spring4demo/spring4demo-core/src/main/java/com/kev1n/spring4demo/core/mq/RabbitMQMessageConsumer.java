package com.kev1n.spring4demo.core.mq;

import com.kev1n.spring4demo.api.dto.NotificationMessage;
import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * RabbitMQ消息消费者
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class RabbitMQMessageConsumer {

    /**
     * 消费用户创建消息（RabbitMQ）
     */
    @Bean
    public Consumer<Message<UserCreatedMessage>> userCreated() {
        return message -> {
            UserCreatedMessage payload = message.getPayload();
            log.info("消费RabbitMQ用户创建消息: {}", payload);
            handleUserCreated(payload);
        };
    }

    /**
     * 消费通知消息（RabbitMQ）
     */
    @Bean
    public Consumer<Message<NotificationMessage>> notification() {
        return message -> {
            NotificationMessage payload = message.getPayload();
            log.info("消费RabbitMQ通知消息: {}", payload);
            handleNotification(payload);
        };
    }

    /**
     * 处理用户创建事件
     */
    private void handleUserCreated(UserCreatedMessage message) {
        log.info("处理用户创建事件: userId={}, username={}", message.getUserId(), message.getUsername());
        // TODO: 根据实际业务需求处理用户创建事件
        // 例如：发送欢迎邮件、初始化用户数据、创建用户档案等
    }

    /**
     * 处理通知消息
     */
    private void handleNotification(NotificationMessage message) {
        log.info("处理通知消息: userId={}, type={}, title={}", message.getUserId(), message.getType(), message.getTitle());

        // 根据通知类型处理
        switch (message.getType()) {
            case "EMAIL":
                // TODO: 发送邮件通知
                log.info("TODO: 发送邮件通知给用户: {}", message.getUserId());
                break;
            case "SMS":
                // TODO: 发送短信通知
                log.info("TODO: 发送短信通知给用户: {}", message.getUserId());
                break;
            case "PUSH":
                // TODO: 发送推送通知
                log.info("TODO: 发送推送通知给用户: {}", message.getUserId());
                break;
            case "SYSTEM":
                // TODO: 保存系统通知
                log.info("TODO: 保存系统通知给用户: {}", message.getUserId());
                break;
            default:
                log.warn("未知的通知类型: {}", message.getType());
        }
    }
}