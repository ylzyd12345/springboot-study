package com.kev1n.spring4demo.core.mq;

import com.kev1n.spring4demo.api.dto.NotificationMessage;
import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka消息消费者
 * 使用 Spring Boot 原生的 @KafkaListener 注解
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Component
@Slf4j
public class KafkaMessageConsumer {

    /**
     * 消费用户创建消息（Kafka）
     *
     * @param message 消息内容
     * @param topic 主题
     * @param partition 分区
     * @param offset 偏移量
     * @param acknowledgment 手动确认对象
     */
    @KafkaListener(
            topics = "user-created-topic",
            groupId = "user-created-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(
            @Payload UserCreatedMessage message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("消费Kafka用户创建消息: topic={}, partition={}, offset={}, userId={}, username={}",
                topic, partition, offset, message.getUserId(), message.getUsername());

        try {
            handleUserCreatedEvent(message);
            // 手动确认消息
            acknowledgment.acknowledge();
            log.info("处理用户创建事件成功并确认: userId={}", message.getUserId());
        } catch (Exception e) {
            log.error("处理用户创建事件失败: userId={}, error={}", message.getUserId(), e.getMessage(), e);
            // 不确认消息，让Kafka重新投递
            throw new RuntimeException("处理用户创建事件失败", e);
        }
    }

    /**
     * 消费通知消息（Kafka）
     *
     * @param message 消息内容
     * @param topic 主题
     * @param partition 分区
     * @param offset 偏移量
     * @param acknowledgment 手动确认对象
     */
    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(
            @Payload NotificationMessage message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("消费Kafka通知消息: topic={}, partition={}, offset={}, userId={}, type={}, title={}",
                topic, partition, offset, message.getUserId(), message.getType(), message.getTitle());

        try {
            handleNotificationEvent(message);
            // 手动确认消息
            acknowledgment.acknowledge();
            log.info("处理通知消息成功并确认: userId={}, type={}", message.getUserId(), message.getType());
        } catch (Exception e) {
            log.error("处理通知消息失败: userId={}, type={}, error={}",
                    message.getUserId(), message.getType(), e.getMessage(), e);
            // 不确认消息，让Kafka重新投递
            throw new RuntimeException("处理通知消息失败", e);
        }
    }

    /**
     * 处理用户创建事件
     */
    private void handleUserCreatedEvent(UserCreatedMessage message) {
        log.info("处理用户创建事件（Kafka）: userId={}, username={}, email={}",
                message.getUserId(), message.getUsername(), message.getEmail());

        // TODO: 根据实际业务需求处理用户创建事件
        // 例如：
        // 1. 数据同步到其他系统
        // 2. 记录用户日志
        // 3. 统计分析
        // 4. 发送欢迎邮件
        // 5. 初始化用户数据
    }

    /**
     * 处理通知消息
     */
    private void handleNotificationEvent(NotificationMessage message) {
        log.info("处理通知消息（Kafka）: userId={}, type={}, title={}, content={}",
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