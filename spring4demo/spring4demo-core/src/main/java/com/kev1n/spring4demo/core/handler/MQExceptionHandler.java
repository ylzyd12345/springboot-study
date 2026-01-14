package com.kev1n.spring4demo.core.handler;

import com.kev1n.spring4demo.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;

/**
 * MQ异常处理辅助类
 *
 * <p>统一处理MQ消费异常，消除重复的异常处理代码。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class MQExceptionHandler {

    /**
     * 执行MQ消息处理并处理异常
     *
     * @param messageName 消息名称
     * @param messageId 消息ID
     * @param runnable 要执行的任务
     */
    public static void execute(String messageName, String messageId, Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException e) {
            log.error("{}消息为空: error={}", messageName, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("消息为空，拒绝重试", e);
        } catch (IllegalArgumentException e) {
            log.error("{}消息参数验证失败: messageId={}, error={}", messageName, messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("消息参数验证失败，拒绝重试", e);
        } catch (BusinessException e) {
            log.error("处理{}时发生业务异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // 业务异常，拒绝重试，避免无限重试
            throw new AmqpRejectAndDontRequeueException("业务异常，拒绝重试", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("处理{}时发生RabbitMQ异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // RabbitMQ异常，触发重试机制
            throw e;
        } catch (RuntimeException e) {
            log.error("处理{}时发生运行时异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // 运行时异常，触发重试机制
            throw e;
        }
    }

    /**
     * 执行MQ消息处理并处理异常（带返回值）
     *
     * @param messageName 消息名称
     * @param messageId 消息ID
     * @param supplier 要执行的任务
     * @param <T> 返回值类型
     * @return 执行结果
     */
    public static <T> T execute(String messageName, String messageId, java.util.function.Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            log.error("{}消息为空: error={}", messageName, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("消息为空，拒绝重试", e);
        } catch (IllegalArgumentException e) {
            log.error("{}消息参数验证失败: messageId={}, error={}", messageName, messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("消息参数验证失败，拒绝重试", e);
        } catch (BusinessException e) {
            log.error("处理{}时发生业务异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // 业务异常，拒绝重试，避免无限重试
            throw new AmqpRejectAndDontRequeueException("业务异常，拒绝重试", e);
        } catch (org.springframework.amqp.AmqpException e) {
            log.error("处理{}时发生RabbitMQ异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // RabbitMQ异常，触发重试机制
            throw e;
        } catch (RuntimeException e) {
            log.error("处理{}时发生运行时异常: messageId={}, error={}", messageName, messageId, e.getMessage(), e);
            // 运行时异常，触发重试机制
            throw e;
        }
    }
}