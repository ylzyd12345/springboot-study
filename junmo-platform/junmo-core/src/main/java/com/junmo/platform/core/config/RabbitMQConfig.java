package com.junmo.platform.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 *
 * @author junmo-platform
 * @version 2.0.0
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    // ==================== 队列定义 ====================

    /**
     * 用户创建队列
     */
    public static final String USER_CREATED_QUEUE = "user.created.queue";

    /**
     * 通知队列
     */
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // ==================== 交换机定义 ====================

    /**
     * 用户主题交换机
     */
    public static final String USER_EXCHANGE = "user.topic.exchange";

    /**
     * 通知主题交换机
     */
    public static final String NOTIFICATION_EXCHANGE = "notification.topic.exchange";

    // ==================== 路由键定义 ====================

    /**
     * 用户创建路由键
     */
    public static final String USER_CREATED_ROUTING_KEY = "user.created";

    /**
     * 通知路由键
     */
    public static final String NOTIFICATION_ROUTING_KEY = "notification.#";

    // ==================== 队列Bean ====================

    /**
     * 用户创建队列
     */
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "user.dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "user.created.dlq")
                .build();
    }

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "notification.dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "notification.dlq")
                .build();
    }

    // ==================== 交换机Bean ====================

    /**
     * 用户主题交换机
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE, true, false);
    }

    /**
     * 通知主题交换机
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    // ==================== 绑定Bean ====================

    /**
     * 绑定用户创建队列到交换机
     */
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(userExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    /**
     * 绑定通知队列到交换机
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    // ==================== 死信队列 ====================

    /**
     * 用户死信队列
     */
    @Bean
    public Queue userDeadLetterQueue() {
        return QueueBuilder.durable("user.created.dlq").build();
    }

    /**
     * 通知死信队列
     */
    @Bean
    public Queue notificationDeadLetterQueue() {
        return QueueBuilder.durable("notification.dlq").build();
    }

    /**
     * 用户死信交换机
     */
    @Bean
    public DirectExchange userDeadLetterExchange() {
        return new DirectExchange("user.dlx.exchange", true, false);
    }

    /**
     * 通知死信交换机
     */
    @Bean
    public DirectExchange notificationDeadLetterExchange() {
        return new DirectExchange("notification.dlx.exchange", true, false);
    }

    /**
     * 绑定用户死信队列
     */
    @Bean
    public Binding userDeadLetterBinding() {
        return BindingBuilder.bind(userDeadLetterQueue())
                .to(userDeadLetterExchange())
                .with("user.created.dlq");
    }

    /**
     * 绑定通知死信队列
     */
    @Bean
    public Binding notificationDeadLetterBinding() {
        return BindingBuilder.bind(notificationDeadLetterQueue())
                .to(notificationDeadLetterExchange())
                .with("notification.dlq");
    }

    // ==================== 消息转换器 ====================

    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ==================== RabbitTemplate ====================

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        // 开启发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功: correlationId={}", correlationData);
            } else {
                log.error("消息发送失败: correlationId={}, cause={}", correlationData, cause);
            }
        });

        // 开启返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息被退回: message={}, replyCode={}, replyText={}, exchange={}, routingKey={}",
                    returned.getMessage(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey());
        });

        return rabbitTemplate;
    }
}