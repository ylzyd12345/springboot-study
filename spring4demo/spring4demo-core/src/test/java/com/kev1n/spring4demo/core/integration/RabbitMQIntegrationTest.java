package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RabbitMQ 集成测试
 * 测试 RabbitMQ 容器的启动和基本操作
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Testcontainers
@DisplayName("RabbitMQ 集成测试")
class RabbitMQIntegrationTest {

    private static final String TEST_QUEUE = "test-queue";
    private static final String TEST_EXCHANGE = "test-exchange";
    private static final String TEST_ROUTING_KEY = "test.routing.key";

    private Connection rabbitMQConnection;
    private Channel rabbitMQChannel;

    @BeforeEach
    void setUp() throws IOException, TimeoutException {
        log.info("初始化 RabbitMQ 连接...");
        log.info("RabbitMQ AMQP URL: {}", TestContainersConfig.getRabbitMQAmqpUrl());
        log.info("RabbitMQ 主机: {}", TestContainersConfig.getRabbitMQHost());
        log.info("RabbitMQ AMQP 端口: {}", TestContainersConfig.getRabbitMQAmqpPort());
        log.info("RabbitMQ 管理界面: {}", TestContainersConfig.getRabbitMQManagementUrl());

        // 创建 RabbitMQ 连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(TestContainersConfig.getRabbitMQHost());
        factory.setPort(TestContainersConfig.getRabbitMQAmqpPort());
        factory.setUsername(TestContainersConfig.getRabbitMQUsername());
        factory.setPassword(TestContainersConfig.getRabbitMQPassword());

        // 创建连接和通道
        rabbitMQConnection = factory.newConnection();
        rabbitMQChannel = rabbitMQConnection.createChannel();

        log.info("RabbitMQ 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        try {
            // 清理测试队列和交换机
            if (rabbitMQChannel != null && rabbitMQChannel.isOpen()) {
                rabbitMQChannel.queueDelete(TEST_QUEUE);
                rabbitMQChannel.exchangeDelete(TEST_EXCHANGE);
                log.info("RabbitMQ 测试队列和交换机已清理");
            }

            if (rabbitMQChannel != null) {
                rabbitMQChannel.close();
            }

            if (rabbitMQConnection != null) {
                rabbitMQConnection.close();
            }

            log.info("RabbitMQ 连接已关闭");
        } catch (IOException e) {
            log.error("关闭 RabbitMQ 连接失败: IO异常", e);
        } catch (TimeoutException e) {
            log.error("关闭 RabbitMQ 连接失败: 超时异常", e);
        } catch (Exception e) {
            log.error("关闭 RabbitMQ 连接失败: 未知异常", e);
        }
    }

    @Test
    @DisplayName("应该成功连接到 RabbitMQ 容器")
    void shouldConnectToRabbitMQContainer_whenContainerIsRunning() throws IOException {
        // Given & When
        boolean isConnected = rabbitMQConnection.isOpen();
        boolean isChannelOpen = rabbitMQChannel.isOpen();

        // Then
        assertThat(isConnected).isTrue();
        assertThat(isChannelOpen).isTrue();
        log.info("RabbitMQ 连接测试通过: 连接 = {}, 通道 = {}", isConnected, isChannelOpen);
    }

    @Test
    @DisplayName("应该成功声明队列")
    void shouldDeclareQueue_whenQueueDoesNotExist() throws IOException {
        // Given & When
        AMQP.Queue.DeclareOk result = rabbitMQChannel.queueDeclare(
                TEST_QUEUE,
                false,  // 非持久化
                false,  // 非独占
                false,  // 不自动删除
                null    // 无参数
        );

        // Then
        assertThat(result.getQueue()).isEqualTo(TEST_QUEUE);
        assertThat(result.getMessageCount()).isEqualTo(0);
        assertThat(result.getConsumerCount()).isEqualTo(0);
        log.info("声明队列测试通过: 队列 = {}, 消息数 = {}, 消费者数 = {}",
                result.getQueue(), result.getMessageCount(), result.getConsumerCount());
    }

    @Test
    @DisplayName("应该成功声明交换机")
    void shouldDeclareExchange_whenExchangeDoesNotExist() throws IOException {
        // Given & When
        AMQP.Exchange.DeclareOk result = rabbitMQChannel.exchangeDeclare(
                TEST_EXCHANGE,
                BuiltinExchangeType.DIRECT,
                false  // 非持久化
        );

        // Then
        assertThat(result).isNotNull();
        log.info("声明交换机测试通过: 交换机 = {}", TEST_EXCHANGE);
    }

    @Test
    @DisplayName("应该成功发送和接收消息")
    void shouldSendAndReceiveMessage_whenMessageIsProvided() throws IOException, InterruptedException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        String message = "测试消息内容";

        // When - 发送消息
        rabbitMQChannel.basicPublish(
                "",           // 默认交换机
                TEST_QUEUE,   // 路由键（队列名）
                null,
                message.getBytes(StandardCharsets.UTF_8)
        );
        log.info("发送消息: {}", message);

        // Then - 接收消息
        GetResponse response = rabbitMQChannel.basicGet(TEST_QUEUE, true);  // auto-ack
        assertThat(response).isNotNull();

        String receivedMessage = new String(response.getBody(), StandardCharsets.UTF_8);
        assertThat(receivedMessage).isEqualTo(message);
        log.info("接收消息测试通过: {}", receivedMessage);
    }

    @Test
    @DisplayName("应该成功绑定队列到交换机")
    void shouldBindQueueToExchange_whenBindingIsCreated() throws IOException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        rabbitMQChannel.exchangeDeclare(TEST_EXCHANGE, BuiltinExchangeType.DIRECT, false);

        // When
        AMQP.Queue.BindOk result = rabbitMQChannel.queueBind(
                TEST_QUEUE,
                TEST_EXCHANGE,
                TEST_ROUTING_KEY
        );

        // Then
        assertThat(result).isNotNull();
        log.info("绑定队列到交换机测试通过: 队列 = {}, 交换机 = {}, 路由键 = {}",
                TEST_QUEUE, TEST_EXCHANGE, TEST_ROUTING_KEY);
    }

    @Test
    @DisplayName("应该成功通过交换机发送消息")
    void shouldSendMessageViaExchange_whenExchangeIsUsed() throws IOException, InterruptedException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        rabbitMQChannel.exchangeDeclare(TEST_EXCHANGE, BuiltinExchangeType.DIRECT, false);
        rabbitMQChannel.queueBind(TEST_QUEUE, TEST_EXCHANGE, TEST_ROUTING_KEY);

        String message = "通过交换机发送的消息";

        // When
        rabbitMQChannel.basicPublish(
                TEST_EXCHANGE,
                TEST_ROUTING_KEY,
                null,
                message.getBytes(StandardCharsets.UTF_8)
        );
        log.info("通过交换机发送消息: 交换机 = {}, 路由键 = {}, 消息 = {}",
                TEST_EXCHANGE, TEST_ROUTING_KEY, message);

        // Then
        GetResponse response = rabbitMQChannel.basicGet(TEST_QUEUE, true);
        assertThat(response).isNotNull();

        String receivedMessage = new String(response.getBody(), StandardCharsets.UTF_8);
        assertThat(receivedMessage).isEqualTo(message);
        log.info("通过交换机接收消息测试通过: {}", receivedMessage);
    }

    @Test
    @DisplayName("应该成功批量发送和接收消息")
    void shouldSendAndReceiveMultipleMessages_whenMultipleMessagesAreProvided() throws IOException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        List<String> messages = List.of("消息1", "消息2", "消息3", "消息4", "消息5");

        // When - 批量发送消息
        for (String message : messages) {
            rabbitMQChannel.basicPublish(
                    "",
                    TEST_QUEUE,
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );
        }
        log.info("批量发送消息: {} 条", messages.size());

        // Then - 批量接收消息
        List<String> receivedMessages = new ArrayList<>();
        while (true) {
            GetResponse response = rabbitMQChannel.basicGet(TEST_QUEUE, true);
            if (response == null) {
                break;
            }
            receivedMessages.add(new String(response.getBody(), StandardCharsets.UTF_8));
        }

        assertThat(receivedMessages).hasSize(messages.size());
        assertThat(receivedMessages).containsExactlyInAnyOrderElementsOf(messages);
        log.info("批量接收消息测试通过: 接收 {} 条消息", receivedMessages.size());
    }

    @Test
    @DisplayName("应该成功使用消费者接收消息")
    void shouldConsumeMessages_whenConsumerIsCreated() throws IOException, InterruptedException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        String message = "消费者测试消息";

        CountDownLatch latch = new CountDownLatch(1);
        List<String> receivedMessages = new ArrayList<>();

        // 创建消费者
        DeliverCallback deliverCallback = (tag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            receivedMessages.add(receivedMessage);
            log.info("消费者收到消息: {}", receivedMessage);
            latch.countDown();
        };

        String consumerTag = rabbitMQChannel.basicConsume(TEST_QUEUE, true, deliverCallback, cancelTag -> {});

        // When
        rabbitMQChannel.basicPublish("", TEST_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));

        // Then
        boolean received = latch.await(5, TimeUnit.SECONDS);
        assertThat(received).isTrue();
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages.get(0)).isEqualTo(message);

        // 取消消费者
        rabbitMQChannel.basicCancel(consumerTag);
        log.info("消费者接收消息测试通过");
    }

    @Test
    @DisplayName("应该成功手动确认消息")
    void shouldAcknowledgeMessage_whenManualAckIsUsed() throws IOException, InterruptedException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        String message = "手动确认测试消息";

        CountDownLatch latch = new CountDownLatch(1);
        List<String> receivedMessages = new ArrayList<>();

        // 创建消费者（关闭自动确认）
        DeliverCallback deliverCallback = (tag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            receivedMessages.add(receivedMessage);
            log.info("消费者收到消息: {}, deliveryTag = {}", receivedMessage, delivery.getEnvelope().getDeliveryTag());

            // 手动确认消息
            rabbitMQChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            latch.countDown();
        };

        String consumerTag = rabbitMQChannel.basicConsume(TEST_QUEUE, false, deliverCallback, cancelTag -> {});

        // When
        rabbitMQChannel.basicPublish("", TEST_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));

        // Then
        boolean received = latch.await(5, TimeUnit.SECONDS);
        assertThat(received).isTrue();
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages.get(0)).isEqualTo(message);

        // 取消消费者
        rabbitMQChannel.basicCancel(consumerTag);
        log.info("手动确认消息测试通过");
    }

    @Test
    @DisplayName("应该成功拒绝消息")
    void shouldRejectMessage_whenRejectIsUsed() throws IOException, InterruptedException {
        // Given
        rabbitMQChannel.queueDeclare(TEST_QUEUE, false, false, false, null);
        String message = "拒绝消息测试";

        CountDownLatch latch = new CountDownLatch(1);
        List<String> receivedMessages = new ArrayList<>();

        // 创建消费者（关闭自动确认）
        DeliverCallback deliverCallback = (tag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            receivedMessages.add(receivedMessage);
            log.info("消费者收到消息: {}, deliveryTag = {}", receivedMessage, delivery.getEnvelope().getDeliveryTag());

            // 拒绝消息（不重新入队）
            rabbitMQChannel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            latch.countDown();
        };

        String consumerTag = rabbitMQChannel.basicConsume(TEST_QUEUE, false, deliverCallback, cancelTag -> {});

        // When
        rabbitMQChannel.basicPublish("", TEST_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));

        // Then
        boolean received = latch.await(5, TimeUnit.SECONDS);
        assertThat(received).isTrue();
        assertThat(receivedMessages).hasSize(1);

        // 验证消息已被拒绝（队列中应该没有消息）
        GetResponse response = rabbitMQChannel.basicGet(TEST_QUEUE, true);
        assertThat(response).isNull();

        // 取消消费者
        rabbitMQChannel.basicCancel(consumerTag);
        log.info("拒绝消息测试通过");
    }

    @Test
    @DisplayName("应该成功使用 Fanout 交换机")
    void shouldUseFanoutExchange_whenFanoutExchangeIsCreated() throws IOException, InterruptedException {
        // Given
        String queue1 = "fanout-queue-1";
        String queue2 = "fanout-queue-2";
        String fanoutExchange = "fanout-test-exchange";

        rabbitMQChannel.queueDeclare(queue1, false, false, false, null);
        rabbitMQChannel.queueDeclare(queue2, false, false, false, null);
        rabbitMQChannel.exchangeDeclare(fanoutExchange, BuiltinExchangeType.FANOUT, false);

        // 绑定两个队列到 Fanout 交换机
        rabbitMQChannel.queueBind(queue1, fanoutExchange, "");
        rabbitMQChannel.queueBind(queue2, fanoutExchange, "");

        String message = "Fanout 交换机测试消息";

        // When
        rabbitMQChannel.basicPublish(fanoutExchange, "", null, message.getBytes(StandardCharsets.UTF_8));

        // Then - 两个队列都应该收到消息
        GetResponse response1 = rabbitMQChannel.basicGet(queue1, true);
        GetResponse response2 = rabbitMQChannel.basicGet(queue2, true);

        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();

        String receivedMessage1 = new String(response1.getBody(), StandardCharsets.UTF_8);
        String receivedMessage2 = new String(response2.getBody(), StandardCharsets.UTF_8);

        assertThat(receivedMessage1).isEqualTo(message);
        assertThat(receivedMessage2).isEqualTo(message);

        // 清理
        rabbitMQChannel.queueDelete(queue1);
        rabbitMQChannel.queueDelete(queue2);
        rabbitMQChannel.exchangeDelete(fanoutExchange);

        log.info("Fanout 交换机测试通过: 两个队列都收到消息");
    }

    @Test
    @DisplayName("应该成功使用 Topic 交换机")
    void shouldUseTopicExchange_whenTopicExchangeIsCreated() throws IOException {
        // Given
        String queue1 = "topic-queue-1";
        String queue2 = "topic-queue-2";
        String topicExchange = "topic-test-exchange";

        rabbitMQChannel.queueDeclare(queue1, false, false, false, null);
        rabbitMQChannel.queueDeclare(queue2, false, false, false, null);
        rabbitMQChannel.exchangeDeclare(topicExchange, BuiltinExchangeType.TOPIC, false);

        // 绑定队列到 Topic 交换机
        rabbitMQChannel.queueBind(queue1, topicExchange, "test.*");
        rabbitMQChannel.queueBind(queue2, topicExchange, "test.error");

        // When - 发送两条消息
        rabbitMQChannel.basicPublish(topicExchange, "test.info", null, "测试信息消息".getBytes(StandardCharsets.UTF_8));
        rabbitMQChannel.basicPublish(topicExchange, "test.error", null, "测试错误消息".getBytes(StandardCharsets.UTF_8));

        // Then
        GetResponse response1a = rabbitMQChannel.basicGet(queue1, true);
        GetResponse response1b = rabbitMQChannel.basicGet(queue1, true);
        GetResponse response2 = rabbitMQChannel.basicGet(queue2, true);

        assertThat(response1a).isNotNull();
        assertThat(response1b).isNotNull();
        assertThat(response2).isNotNull();

        // 清理
        rabbitMQChannel.queueDelete(queue1);
        rabbitMQChannel.queueDelete(queue2);
        rabbitMQChannel.exchangeDelete(topicExchange);

        log.info("Topic 交换机测试通过: 消息路由正确");
    }
}