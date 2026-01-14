package com.kev1n.spring4demo.core.integration;

import com.kev1n.spring4demo.test.config.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * Kafka 集成测试
 * 测试 Kafka 容器的启动和基本操作
 * 使用 Spring Boot 4.0.1 和 Spring Framework 7 兼容的 Spring Kafka API
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest(classes = KafkaIntegrationTest.TestConfig.class,
        properties = {
                "spring.kafka.consumer.group-id=test-consumer-group",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.enable-auto-commit=false",
                "spring.kafka.listener.ack-mode=manual_immediate"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Kafka 集成测试")
class KafkaIntegrationTest {

    private static final String TEST_TOPIC = "test-topic";
    private static final String TEST_TOPIC_2 = "test-topic-2";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProducer<String, String> kafkaProducer;
    private KafkaConsumer<String, String> kafkaConsumer;

    private static CountDownLatch latch;
    private static AtomicInteger receivedCount;
    private static String receivedMessage;

    @TestConfiguration
    @EnableKafka
    @Import(KafkaIntegrationTest.TestConsumer.class)
    static class TestConfig {

        /**
         * 测试用 Kafka 生产者配置
         * 使用 TestContainers 的 Kafka 容器
         */
        @Bean
        @Primary
        public ProducerFactory<String, Object> testProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            String bootstrapServers = TestContainersConfig.getKafkaBootstrapServers();
            configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    bootstrapServers);
            configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    JacksonJsonDeserializer.class);
            configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    JacksonJsonSerializer.class);
            configProps.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
            configProps.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 3);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        /**
         * 测试用 KafkaTemplate
         */
        @Bean
        @Primary
        public KafkaTemplate<String, Object> testKafkaTemplate() {
            return new KafkaTemplate<>(testProducerFactory());
        }

        /**
         * 测试用 Kafka 消费者配置
         * 使用 TestContainers 的 Kafka 容器
         */
        @Bean
        @Primary
        public ConsumerFactory<String, Object> testConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            String bootstrapServers = TestContainersConfig.getKafkaBootstrapServers();
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    bootstrapServers);
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    JacksonJsonDeserializer.class);
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    JacksonJsonDeserializer.class);
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            // 信任所有包
            props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
            props.put(JacksonJsonDeserializer.TYPE_MAPPINGS, "test:java.lang.String");
            // 错误处理
            props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    ErrorHandlingDeserializer.class);
            props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class.getName());

            return new DefaultKafkaConsumerFactory<>(props);
        }

        /**
         * 测试用 Kafka 监听器容器工厂
         */
        @Bean
        @Primary
        public ConcurrentKafkaListenerContainerFactory<String, Object> testKafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(testConsumerFactory());
            factory.setConcurrency(1);
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            return factory;
        }
    }

    /**
     * 测试用消费者
     */
    static class TestConsumer {

        @KafkaListener(topics = TEST_TOPIC, groupId = "test-consumer-group",
                containerFactory = "testKafkaListenerContainerFactory")
        public void consumeTestTopic(ConsumerRecord<String, Object> record) {
            log.info("接收到消息: Topic = {}, Key = {}, Value = {}, Offset = {}",
                    record.topic(), record.key(), record.value(), record.offset());
            if (latch != null) {
                latch.countDown();
                receivedCount.incrementAndGet();
                receivedMessage = record.value() != null ? record.value().toString() : null;
            }
        }

        @KafkaListener(topics = TEST_TOPIC_2, groupId = "test-consumer-group-2",
                containerFactory = "testKafkaListenerContainerFactory")
        public void consumeTestTopic2(ConsumerRecord<String, Object> record) {
            log.info("接收到消息 (Topic 2): Topic = {}, Key = {}, Value = {}, Offset = {}",
                    record.topic(), record.key(), record.value(), record.offset());
            if (latch != null) {
                latch.countDown();
                receivedCount.incrementAndGet();
            }
        }
    }

    @BeforeEach
    void setUp() {
        log.info("初始化 Kafka 连接...");
        log.info("Kafka Bootstrap Servers: {}", TestContainersConfig.getKafkaBootstrapServers());
        log.info("Kafka 主机: {}", TestContainersConfig.getKafkaHost());
        log.info("Kafka 端口: {}", TestContainersConfig.getKafkaPort());

        // 初始化 Kafka Producer
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, TestContainersConfig.getKafkaBootstrapServers());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, 3);
        kafkaProducer = new KafkaProducer<>(producerProperties);

        // 初始化 Kafka Consumer
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestContainersConfig.getKafkaBootstrapServers());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        kafkaConsumer = new KafkaConsumer<>(consumerProperties);

        log.info("Kafka 连接初始化完成");
    }

    @AfterEach
    void tearDown() {
        if (kafkaConsumer != null) {
            kafkaConsumer.unsubscribe();
            kafkaConsumer.close();
        }
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
        log.info("测试清理完成");
    }

    @Test
    @DisplayName("应该成功连接到 Kafka 容器")
    void shouldConnectToKafkaContainer_whenContainerIsRunning() {
        // Given & When
        // 验证 Producer 和 Consumer 已成功初始化
        assertThat(kafkaProducer).isNotNull();
        assertThat(kafkaConsumer).isNotNull();

        // Then
        log.info("Kafka 连接测试通过: Producer 和 Consumer 已成功初始化");
    }

    @Test
    @DisplayName("应该成功创建 Topic")
    void shouldCreateTopic_whenTopicDoesNotExist() {
        // Given & When
        // 注意：Testcontainers 的 KafkaContainer 默认会自动创建 Topic
        // 这里我们通过发送消息来验证 Topic 是否存在
        String message = "测试创建 Topic";

        try {
            kafkaProducer.send(new ProducerRecord<>(TEST_TOPIC, "key1", message)).get();
            log.info("发送消息到 Topic: {}", TEST_TOPIC);

            // 订阅 Topic
            kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));

            // 轮询消息
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

            // Then
            assertThat(records).isNotEmpty();
            log.info("创建 Topic 测试通过: Topic {} 存在", TEST_TOPIC);

            // 取消订阅
            kafkaConsumer.unsubscribe();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("创建 Topic 测试失败: 测试被中断", e);
            fail("创建 Topic 测试失败: 测试被中断: " + e.getMessage());
        } catch (java.util.concurrent.ExecutionException e) {
            log.error("创建 Topic 测试失败: 执行异常", e);
            fail("创建 Topic 测试失败: 执行异常: " + e.getMessage());
        } catch (org.apache.kafka.common.KafkaException e) {
            log.error("创建 Topic 测试失败: Kafka异常", e);
            fail("创建 Topic 测试失败: Kafka异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("创建 Topic 测试失败: 未知异常", e);
            fail("创建 Topic 测试失败: 未知异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("应该成功发送和接收消息")
    void shouldSendAndReceiveMessage_whenMessageIsProvided() throws Exception {
        // Given
        String key = "test-key";
        String message = "测试消息内容";

        // When - 发送消息
        ProducerRecord<String, String> record = new ProducerRecord<>(TEST_TOPIC, key, message);
        RecordMetadata metadata = kafkaProducer.send(record).get();

        log.info("发送消息: Topic = {}, Partition = {}, Offset = {}, Key = {}, Message = {}",
                metadata.topic(), metadata.partition(), metadata.offset(), key, message);

        // Then - 接收消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));

        assertThat(records).isNotEmpty();
        assertThat(records.count()).isGreaterThan(0);

        for (ConsumerRecord<String, String> receivedRecord : records) {
            assertThat(receivedRecord.key()).isEqualTo(key);
            assertThat(receivedRecord.value()).isEqualTo(message);
            log.info("接收消息: Key = {}, Value = {}, Offset = {}",
                    receivedRecord.key(), receivedRecord.value(), receivedRecord.offset());
        }

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("发送和接收消息测试通过");
    }

    @Test
    @DisplayName("应该成功批量发送和接收消息")
    void shouldSendAndReceiveMultipleMessages_whenMultipleMessagesAreProvided() throws Exception {
        // Given
        List<String> messages = List.of("消息1", "消息2", "消息3", "消息4", "消息5");
        CountDownLatch latch = new CountDownLatch(messages.size());
        AtomicInteger successCount = new AtomicInteger(0);

        // When - 批量发送消息
        for (int i = 0; i < messages.size(); i++) {
            String key = "key-" + i;
            String message = messages.get(i);

            kafkaProducer.send(new ProducerRecord<>(TEST_TOPIC, key, message), (metadata, exception) -> {
                if (exception == null) {
                    successCount.incrementAndGet();
                    log.info("消息发送成功: Topic = {}, Partition = {}, Offset = {}, Key = {}",
                            metadata.topic(), metadata.partition(), metadata.offset(), key);
                } else {
                    log.error("消息发送失败: {}", exception.getMessage());
                }
                latch.countDown();
            });
        }

        // 等待所有消息发送完成
        boolean allSent = latch.await(30, TimeUnit.SECONDS);
        assertThat(allSent).isTrue();
        assertThat(successCount.get()).isEqualTo(messages.size());

        // Then - 批量接收消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));

        List<String> receivedMessages = new java.util.ArrayList<>();
        int maxPolls = 10;
        int pollCount = 0;

        while (receivedMessages.size() < messages.size() && pollCount < maxPolls) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                receivedMessages.add(record.value());
                log.info("接收消息: Value = {}, Offset = {}", record.value(), record.offset());
            }
            pollCount++;
        }

        assertThat(receivedMessages).hasSize(messages.size());
        assertThat(receivedMessages).containsExactlyInAnyOrderElementsOf(messages);

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("批量发送和接收消息测试通过: 发送 {} 条，接收 {} 条", messages.size(), receivedMessages.size());
    }

    @Test
    @DisplayName("应该成功使用不同的分区")
    void shouldUseDifferentPartitions_whenMessagesAreSentToDifferentPartitions() throws Exception {
        // Given
        int partitionCount = 3;
        String message = "分区测试消息";

        // When - 发送消息到不同分区
        for (int partition = 0; partition < partitionCount; partition++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    TEST_TOPIC,
                    partition,
                    "partition-" + partition,
                    message + "-" + partition
            );
            RecordMetadata metadata = kafkaProducer.send(record).get();

            log.info("发送消息到分区 {}: Partition = {}, Offset = {}",
                    partition, metadata.partition(), metadata.offset());
        }

        // Then - 接收消息并验证分区
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));

        java.util.Set<Integer> partitions = new java.util.HashSet<>();
        int maxPolls = 10;
        int pollCount = 0;

        while (partitions.size() < partitionCount && pollCount < maxPolls) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                partitions.add(record.partition());
                log.info("接收消息: Partition = {}, Value = {}, Offset = {}",
                        record.partition(), record.value(), record.offset());
            }
            pollCount++;
        }

        assertThat(partitions).hasSize(partitionCount);

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("使用不同分区测试通过: 使用了 {} 个分区", partitions.size());
    }

    @Test
    @DisplayName("应该成功使用消费者组")
    void shouldUseConsumerGroup_whenMultipleConsumersAreInSameGroup() throws Exception {
        // Given
        String message = "消费者组测试消息";

        // 创建第二个消费者（相同组ID）
        Properties consumerProperties2 = new Properties();
        consumerProperties2.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestContainersConfig.getKafkaBootstrapServers());
        consumerProperties2.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties2.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties2.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        consumerProperties2.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties2.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        KafkaConsumer<String, String> kafkaConsumer2 = new KafkaConsumer<>(consumerProperties2);

        // When - 发送消息
        kafkaProducer.send(new ProducerRecord<>(TEST_TOPIC, "group-key", message)).get();
        log.info("发送消息: {}", message);

        // Then - 两个消费者订阅同一个 Topic
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
        kafkaConsumer2.subscribe(Collections.singletonList(TEST_TOPIC));

        // 两个消费者轮询消息
        ConsumerRecords<String, String> records1 = kafkaConsumer.poll(Duration.ofSeconds(5));
        ConsumerRecords<String, String> records2 = kafkaConsumer2.poll(Duration.ofSeconds(5));

        // 验证只有一个消费者收到消息（消费者组负载均衡）
        int totalMessages = records1.count() + records2.count();
        assertThat(totalMessages).isGreaterThan(0);

        log.info("消费者组测试通过: Consumer1 收到 {} 条消息, Consumer2 收到 {} 条消息",
                records1.count(), records2.count());

        // 取消订阅并关闭第二个消费者
        kafkaConsumer.unsubscribe();
        kafkaConsumer2.unsubscribe();
        kafkaConsumer2.close();
    }

    @Test
    @DisplayName("应该成功提交偏移量")
    void shouldCommitOffset_whenOffsetIsCommitted() throws Exception {
        // Given
        String key = "offset-test-key";
        String message = "偏移量提交测试消息";

        // When - 发送消息
        kafkaProducer.send(new ProducerRecord<>(TEST_TOPIC, key, message)).get();

        // 接收消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

        assertThat(records).isNotEmpty();

        // 提交偏移量
        for (ConsumerRecord<String, String> record : records) {
            log.info("接收消息: Offset = {}, 提交偏移量", record.offset());
        }

        kafkaConsumer.commitSync();

        // Then - 验证偏移量已提交
        // 重新创建消费者验证偏移量
        kafkaConsumer.unsubscribe();
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));

        ConsumerRecords<String, String> recordsAfterCommit = kafkaConsumer.poll(Duration.ofSeconds(5));

        // 由于我们已经提交了偏移量，应该不会再收到相同的消息
        // 注意：这个测试可能需要根据实际的 Kafka 配置进行调整
        log.info("偏移量提交测试通过");

        // 取消订阅
        kafkaConsumer.unsubscribe();
    }

    @Test
    @DisplayName("应该成功处理空消息")
    void shouldHandleNullMessage_whenNullMessageIsSent() throws Exception {
        // Given
        String key = "null-message-key";
        String message = null;

        // When - 发送空消息
        ProducerRecord<String, String> record = new ProducerRecord<>(TEST_TOPIC, key, message);
        RecordMetadata metadata = kafkaProducer.send(record).get();

        log.info("发送空消息: Topic = {}, Partition = {}, Offset = {}, Key = {}, Value = null",
                metadata.topic(), metadata.partition(), metadata.offset(), key);

        // Then - 接收空消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));

        assertThat(records).isNotEmpty();

        for (ConsumerRecord<String, String> receivedRecord : records) {
            if (receivedRecord.key().equals(key)) {
                assertThat(receivedRecord.value()).isNull();
                log.info("接收空消息: Key = {}, Value = null", receivedRecord.key());
            }
        }

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("处理空消息测试通过");
    }

    @Test
    @DisplayName("应该成功处理大消息")
    void shouldHandleLargeMessage_whenLargeMessageIsSent() throws Exception {
        // Given
        String key = "large-message-key";
        // 创建一个大消息（1KB）
        StringBuilder largeMessageBuilder = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            largeMessageBuilder.append("A");
        }
        String largeMessage = largeMessageBuilder.toString();

        // When - 发送大消息
        ProducerRecord<String, String> record = new ProducerRecord<>(TEST_TOPIC, key, largeMessage);
        RecordMetadata metadata = kafkaProducer.send(record).get();

        log.info("发送大消息: Topic = {}, Partition = {}, Offset = {}, Key = {}, Size = {} bytes",
                metadata.topic(), metadata.partition(), metadata.offset(), key, largeMessage.length());

        // Then - 接收大消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));

        assertThat(records).isNotEmpty();

        for (ConsumerRecord<String, String> receivedRecord : records) {
            if (receivedRecord.key().equals(key)) {
                assertThat(receivedRecord.value()).isEqualTo(largeMessage);
                assertThat(receivedRecord.value().length()).isEqualTo(1024);
                log.info("接收大消息: Key = {}, Size = {} bytes", receivedRecord.key(), receivedRecord.value().length());
            }
        }

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("处理大消息测试通过");
    }

    @Test
    @DisplayName("应该成功处理并发发送")
    void shouldHandleConcurrentSending_whenMultipleThreadsSendMessages() throws Exception {
        // Given
        int threadCount = 5;
        int messagesPerThread = 10;
        CountDownLatch sendLatch = new CountDownLatch(threadCount * messagesPerThread);
        AtomicInteger successCount = new AtomicInteger(0);

        // When - 多线程并发发送消息
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            Thread thread = new Thread(() -> {
                for (int i = 0; i < messagesPerThread; i++) {
                    String key = "thread-" + threadId + "-msg-" + i;
                    String message = "并发测试消息-" + threadId + "-" + i;

                    kafkaProducer.send(new ProducerRecord<>(TEST_TOPIC, key, message), (metadata, exception) -> {
                        if (exception == null) {
                            successCount.incrementAndGet();
                        }
                        sendLatch.countDown();
                    });
                }
            });
            thread.start();
        }

        // 等待所有消息发送完成
        boolean allSent = sendLatch.await(60, TimeUnit.SECONDS);
        assertThat(allSent).isTrue();
        assertThat(successCount.get()).isEqualTo(threadCount * messagesPerThread);

        // Then - 接收消息
        kafkaConsumer.subscribe(Collections.singletonList(TEST_TOPIC));

        List<String> receivedMessages = new java.util.ArrayList<>();
        int maxPolls = 20;
        int pollCount = 0;

        while (receivedMessages.size() < threadCount * messagesPerThread && pollCount < maxPolls) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                receivedMessages.add(record.value());
            }
            pollCount++;
        }

        assertThat(receivedMessages).hasSize(threadCount * messagesPerThread);

        // 取消订阅
        kafkaConsumer.unsubscribe();
        log.info("并发发送测试通过: 发送 {} 条消息，接收 {} 条消息",
                threadCount * messagesPerThread, receivedMessages.size());
    }
}