package com.kev1n.spring4demo.core.mq;

import com.kev1n.spring4demo.api.dto.NotificationMessage;
import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import com.kev1n.spring4demo.core.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 消息生产者
 * 使用 Spring Boot 原生的 RabbitTemplate 和 KafkaTemplate
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ==================== RabbitMQ ====================

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

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EXCHANGE,
                    RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                    message
            );
            log.info("发送用户创建消息到RabbitMQ成功: userId={}, username={}", userId, username);
        } catch (org.springframework.amqp.AmqpConnectException e) {
            log.error("RabbitMQ连接失败，无法发送用户创建消息: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ连接失败", e);
        } catch (org.springframework.amqp.AmqpTimeoutException e) {
            log.error("RabbitMQ发送超时，无法发送用户创建消息: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ发送超时", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("RabbitMQ发送用户创建消息失败: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ发送失败", e);
        } catch (IllegalArgumentException e) {
            log.error("用户创建消息参数验证失败: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("用户创建消息对象为空: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("消息对象为空", e);
        }
    }

    /**
     * 发送通知消息到RabbitMQ
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

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "notification." + type.toLowerCase(),
                    message
            );
            log.info("发送通知消息到RabbitMQ成功: userId={}, type={}, title={}", userId, type, title);
        } catch (org.springframework.amqp.AmqpConnectException e) {
            log.error("RabbitMQ连接失败，无法发送通知消息: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ连接失败", e);
        } catch (org.springframework.amqp.AmqpTimeoutException e) {
            log.error("RabbitMQ发送超时，无法发送通知消息: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ发送超时", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("RabbitMQ发送通知消息失败: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("RabbitMQ发送失败", e);
        } catch (IllegalArgumentException e) {
            log.error("通知消息参数验证失败: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("通知消息对象为空: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("消息对象为空", e);
        }
    }

    // ==================== Kafka ====================

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

        String topic = "user-created-topic";
        String key = String.valueOf(userId);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("发送用户创建消息到Kafka成功: topic={}, partition={}, offset={}, userId={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            userId);
                } else {
                    log.error("发送用户创建消息到Kafka失败: topic={}, key={}, userId={}, error={}",
                            topic, key, userId, ex.getMessage(), ex);
                    throw new com.kev1n.spring4demo.common.exception.BusinessException("发送用户创建消息到Kafka失败", ex);
                }
            });
        } catch (org.apache.kafka.common.errors.SerializationException e) {
            log.error("Kafka序列化失败，无法发送用户创建消息: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka序列化失败", e);
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            log.error("Kafka发送超时，无法发送用户创建消息: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka发送超时", e);
        } catch (org.apache.kafka.common.KafkaException e) {
            log.error("Kafka发送用户创建消息失败: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka发送失败", e);
        } catch (IllegalArgumentException e) {
            log.error("用户创建消息参数验证失败: userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("用户创建消息对象为空: userId={}, error={}", userId, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("消息对象为空", e);
        }
    }

    /**
     * 发送通知消息到Kafka
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型（SYSTEM-系统通知, EMAIL-邮件, SMS-短信, PUSH-推送）
     */
    public void sendNotificationMessageToKafka(Long userId, String username, String title, String content, String type) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(userId)
                .username(username)
                .title(title)
                .content(content)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        String topic = "notification-topic";
        String key = String.valueOf(userId);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("发送通知消息到Kafka成功: topic={}, partition={}, offset={}, userId={}, type={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            userId, type);
                } else {
                    log.error("发送通知消息到Kafka失败: topic={}, key={}, userId={}, type={}, error={}",
                            topic, key, userId, type, ex.getMessage(), ex);
                    throw new com.kev1n.spring4demo.common.exception.BusinessException("发送通知消息到Kafka失败", ex);
                }
            });
        } catch (org.apache.kafka.common.errors.SerializationException e) {
            log.error("Kafka序列化失败，无法发送通知消息: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka序列化失败", e);
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            log.error("Kafka发送超时，无法发送通知消息: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka发送超时", e);
        } catch (org.apache.kafka.common.KafkaException e) {
            log.error("Kafka发送通知消息失败: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("Kafka发送失败", e);
        } catch (IllegalArgumentException e) {
            log.error("通知消息参数验证失败: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("通知消息对象为空: userId={}, type={}, error={}", userId, type, e.getMessage());
            throw new com.kev1n.spring4demo.common.exception.BusinessException("消息对象为空", e);
        }
    }
}