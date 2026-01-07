package com.kev1n.spring4demo.core.mq;

import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * Kafka消息消费者
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class KafkaMessageConsumer {

    /**
     * 消费用户创建消息（Kafka）
     */
    @Bean
    public Consumer<Message<UserCreatedMessage>> userCreatedKafka() {
        return message -> {
            UserCreatedMessage payload = message.getPayload();
            log.info("消费Kafka用户创建消息: {}", payload);
            handleUserCreated(payload);
        };
    }

    /**
     * 处理用户创建事件
     */
    private void handleUserCreated(UserCreatedMessage message) {
        log.info("处理用户创建事件（Kafka）: userId={}, username={}", message.getUserId(), message.getUsername());
        // TODO: 根据实际业务需求处理用户创建事件
        // 例如：数据同步、日志记录、统计分析等
    }
}