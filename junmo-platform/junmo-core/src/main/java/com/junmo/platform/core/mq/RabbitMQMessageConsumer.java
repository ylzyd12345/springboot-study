package com.junmo.platform.core.mq;

import com.junmo.platform.api.dto.NotificationMessage;
import com.junmo.platform.api.dto.UserCreatedMessage;
import com.junmo.platform.core.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ消息消费者
 * 使用 Spring Boot 原生的 @RabbitListener 注解
 *
 * @author junmo-platform
 * @version 2.0.0
 */
@Component
@Slf4j
@RabbitListener(queues = {
        RabbitMQConfig.USER_CREATED_QUEUE,
        RabbitMQConfig.NOTIFICATION_QUEUE
})
public class RabbitMQMessageConsumer {

    /**
     * 处理用户创建消息
     *
     * @param message 用户创建消息
     */
    @RabbitHandler
    public void handleUserCreated(UserCreatedMessage message) {
        log.info("消费RabbitMQ用户创建消息: userId={}, username={}", message.getUserId(), message.getUsername());

        try {
            handleUserCreatedEvent(message);
            log.info("处理用户创建事件成功: userId={}", message.getUserId());
        } catch (NullPointerException e) {
            log.error("用户创建消息为空: error={}", e.getMessage());
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("消息为空，拒绝重试", e);
        } catch (IllegalArgumentException e) {
            log.error("用户创建消息参数验证失败: userId={}, error={}", message.getUserId(), e.getMessage());
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("消息参数验证失败，拒绝重试", e);
        } catch (com.junmo.platform.common.exception.BusinessException e) {
            log.error("处理用户创建事件时发生业务异常: userId={}, error={}", message.getUserId(), e.getMessage(), e);
            // 业务异常，拒绝重试，避免无限重试
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("业务异常，拒绝重试", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("处理用户创建事件时发生RabbitMQ异常: userId={}, error={}", message.getUserId(), e.getMessage(), e);
            // RabbitMQ异常，触发重试机制
            throw e;
        } catch (RuntimeException e) {
            log.error("处理用户创建事件时发生运行时异常: userId={}, error={}", message.getUserId(), e.getMessage(), e);
            // 运行时异常，触发重试机制
            throw e;
        }
    }

    /**
     * 处理通知消息
     *
     * @param message 通知消息
     */
    @RabbitHandler
    public void handleNotification(NotificationMessage message) {
        log.info("消费RabbitMQ通知消息: userId={}, type={}, title={}",
                message.getUserId(), message.getType(), message.getTitle());

        try {
            handleNotificationEvent(message);
            log.info("处理通知消息成功: userId={}, type={}", message.getUserId(), message.getType());
        } catch (NullPointerException e) {
            log.error("通知消息为空: error={}", e.getMessage());
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("消息为空，拒绝重试", e);
        } catch (IllegalArgumentException e) {
            log.error("通知消息参数验证失败: userId={}, type={}, error={}", message.getUserId(), message.getType(), e.getMessage());
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("消息参数验证失败，拒绝重试", e);
        } catch (com.junmo.platform.common.exception.BusinessException e) {
            log.error("处理通知消息时发生业务异常: userId={}, type={}, error={}", message.getUserId(), message.getType(), e.getMessage(), e);
            // 业务异常，拒绝重试，避免无限重试
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("业务异常，拒绝重试", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("处理通知消息时发生RabbitMQ异常: userId={}, type={}, error={}", message.getUserId(), message.getType(), e.getMessage(), e);
            // RabbitMQ异常，触发重试机制
            throw e;
        } catch (RuntimeException e) {
            log.error("处理通知消息时发生运行时异常: userId={}, type={}, error={}", message.getUserId(), message.getType(), e.getMessage(), e);
            // 运行时异常，触发重试机制
            throw e;
        }
    }

    /**
     * 处理用户创建事件
     */
    private void handleUserCreatedEvent(UserCreatedMessage message) {
        log.info("处理用户创建事件: userId={}, username={}, email={}",
                message.getUserId(), message.getUsername(), message.getEmail());

        // TODO: 根据实际业务需求处理用户创建事件
        // 例如：
        // 1. 发送欢迎邮件
        // 2. 初始化用户数据
        // 3. 创建用户档案
        // 4. 记录用户日志
        // 5. 发送欢迎短信
    }

    /**
     * 处理通知消息
     */
    private void handleNotificationEvent(NotificationMessage message) {
        log.info("处理通知消息: userId={}, type={}, title={}, content={}",
                message.getUserId(), message.getType(), message.getTitle(), message.getContent());

        // 根据通知类型处理
        switch (message.getType()) {
            case "EMAIL":
                // TODO: 发送邮件通知
                log.info("TODO: 发送邮件通知给用户: userId={}, title={}", message.getUserId(), message.getTitle());
                break;
            case "SMS":
                // TODO: 发送短信通知
                log.info("TODO: 发送短信通知给用户: userId={}, title={}", message.getUserId(), message.getTitle());
                break;
            case "PUSH":
                // TODO: 发送推送通知
                log.info("TODO: 发送推送通知给用户: userId={}, title={}", message.getUserId(), message.getTitle());
                break;
            case "SYSTEM":
                // TODO: 保存系统通知
                log.info("TODO: 保存系统通知给用户: userId={}, title={}", message.getUserId(), message.getTitle());
                break;
            default:
                log.warn("未知的通知类型: userId={}, type={}", message.getUserId(), message.getType());
        }
    }
}