# Spring4demo 项目架构设计参考文档

## 📋 文档信息

| 项目 | 内容 |
|------|------|
| **文档名称** | Spring4demo 项目架构设计参考文档 |
| **版本号** | v3.3.0 |
| **生成日期** | 2026-01-07 |
| **更新日期** | 2026-01-11 |
| **文档类型** | 架构师视角技术架构参考 |
| **项目阶段** | 功能完善阶段 |

---

## 🎯 文档说明

本文档作为架构师视角的技术架构参考文档，为研发团队提供技术实现的最佳实践和代码示例。当前项目处于**工程框架搭建阶段**，重点在于技术架构的完善和最佳实践的落地。

**重要说明**:
- 当前代码框架只有user模型
- 示例代码中只引用user模型相关的service和mapper
- 其他相关对象模型（如订单、短信、邮件等）已用TODO注释标注，待后续实现
- 研发人员可根据实际业务需求，参考示例代码实现相应功能

### 已确认的技术决策

| 技术领域 | 选型方案 | 状态 |
|---------|---------|------|
| **架构分层** | 三层架构（Web层、Service层、Mapper层） | ✅ 已确认 |
| **认证框架** | Sa-Token | ✅ 已确认 |
| **数据访问** | MyBatis-Plus | ✅ 已确认 |
| **主键策略** | 雪花算法 | ✅ 已确认 |

### 待实现的技术特性

| 技术特性 | 优先级 | 状态 |
|---------|--------|------|
| **Guava限流** | P0 | ✅ 已实现 |
| **Spring Boot原生消息队列 (RabbitMQ)** | P0 | ✅ 已实现 |
| **Spring Boot原生消息队列 (Kafka)** | P0 | ✅ 已实现 |
| **MongoDB** | P1 | ✅ 已实现 |
| **Elasticsearch** | P1 | ✅ 已实现 |
| **Neo4j** | P2 | ❌ 待实现 |
| **InfluxDB** | P2 | ❌ 待实现 |
| **WebFlux** | P1 | ⚠️ 部分实现 |
| **WebSocket** | P1 | ⚠️ 部分实现 |
| **GraphQL** | P2 | ❌ 待实现 |
| **数据库分库分表** | P1 | ❌ 待实现 |
| **Caffeine+Redis双缓存** | P1 | ✅ 已实现 |
| **异步处理** | P1 | ⚠️ 部分实现 |
| **分布式事务** | P1 | ⚠️ 部分实现 |
| **定时任务** | P1 | ⚠️ 部分实现 |

---

## 📚 技术架构最佳实践

### 0. Guava限流

**技术选型**: Guava RateLimiter

**适用场景**:
- 单体应用限流
- API接口限流
- 防止系统过载

**最佳实践**:

```java
/**
 * 限流配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 创建限流器缓存
     */
    @Bean
    public LoadingCache<String, RateLimiter> rateLimiterCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(key -> RateLimiter.create(100)); // 默认每秒100个请求
    }

    /**
     * 创建限流切面
     */
    @Bean
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }
}

/**
 * 限流注解
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 每秒允许的请求数
     */
    double permits() default 100;

    /**
     * 超时时间（秒）
     */
    long timeout() default 0;
}

/**
 * 限流切面
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    @Autowired
    private LoadingCache<String, RateLimiter> rateLimiterCache;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = rateLimit.key();
        if (StringUtils.isEmpty(key)) {
            key = joinPoint.getSignature().toShortString();
        }

        RateLimiter rateLimiter = rateLimiterCache.get(key);

        if (rateLimit.timeout() > 0) {
            // 尝试获取令牌，等待超时时间
            if (!rateLimiter.tryAcquire(rateLimit.timeout(), TimeUnit.SECONDS)) {
                log.warn("限流触发: key={}, permits={}, timeout={}", key, rateLimit.permits(), rateLimit.timeout());
                throw new RateLimitException("请求过于频繁，请稍后重试");
            }
        } else {
            // 不等待，直接返回
            if (!rateLimiter.tryAcquire()) {
                log.warn("限流触发: key={}, permits={}", key, rateLimit.permits());
                throw new RateLimitException("请求过于频繁，请稍后重试");
            }
        }

        return joinPoint.proceed();
    }
}

/**
 * 限流异常
 *
 * @author spring4demo
 * @version 1.0.0
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }
}

/**
 * 用户控制器（使用限流）
 *
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表（限流：每秒100个请求）
     */
    @GetMapping
    @RateLimit(key = "user:list", permits = 100)
    public ResponseEntity<List<UserVO>> listUsers() {
        log.info("获取用户列表");
        List<UserVO> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 获取用户详情（限流：每秒200个请求）
     */
    @GetMapping("/{id}")
    @RateLimit(key = "user:detail", permits = 200)
    public ResponseEntity<UserVO> getUser(@PathVariable Long id) {
        log.info("获取用户详情: {}", id);
        UserVO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 创建用户（限流：每秒50个请求）
     */
    @PostMapping
    @RateLimit(key = "user:create", permits = 50)
    public ResponseEntity<UserVO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        log.info("创建用户: {}", dto.getUsername());
        UserVO user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
```

**最佳实践要点**:

1. **限流粒度**:
   - 接口级别限流：根据接口特点设置不同的限流阈值
   - 用户级别限流：根据用户ID设置限流器
   - IP级别限流：根据IP地址设置限流器

2. **限流策略**:
   - QPS限流：每秒请求数限流
   - 并发限流：同时处理的请求数限流
   - 令牌桶算法：平滑限流

3. **限流处理**:
   - 直接拒绝：返回限流异常
   - 排队等待：等待获取令牌
   - 降级处理：返回缓存数据

4. **监控告警**:
   - 监控限流触发次数
   - 监控限流器性能
   - 设置限流告警阈值

### 1. Spring Boot原生消息队列

**技术选型**: Spring Boot AMQP (RabbitMQ) + Spring Kafka

**适用场景**:
- 异步消息处理
- 系统解耦
- 削峰填谷

**最佳实践**:

```java
/**
 * RabbitMQ配置
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Configuration
public class RabbitMQConfig {

    // 队列定义
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // 交换机定义
    public static final String USER_EXCHANGE = "user.topic.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.topic.exchange";

    // 路由键定义
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.#";

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

    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

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

/**
 * Kafka配置
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * 生产者配置
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return props;
    }

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());

        // 设置发送回调
        kafkaTemplate.setProducerListener(new ProducerListener<String, Object>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, Object value, RecordMetadata metadata) {
                log.info("Kafka 消息发送成功: topic={}, partition={}, offset={}", topic, partition, metadata.offset());
            }

            @Override
            public void onError(String topic, Integer partition, String key, Object value, Exception exception) {
                log.error("Kafka 消息发送失败: topic={}, partition={}, key={}, value={}", topic, partition, key, value, exception);
            }
        });

        return kafkaTemplate;
    }

    /**
     * 消费者配置
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "spring4demo-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    /**
     * 消费者工厂
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     * Kafka监听器容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setAutoStartup(true);
        return factory;
    }
}

/**
 * 消息生产者
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Component
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送用户创建消息到RabbitMQ
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
        } catch (Exception e) {
            log.error("发送用户创建消息到RabbitMQ失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("发送用户创建消息失败", e);
        }
    }

    /**
     * 发送用户创建消息到Kafka
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
                    throw new RuntimeException("发送用户创建消息到Kafka失败", ex);
                }
            });
        } catch (Exception e) {
            log.error("发送用户创建消息到Kafka失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("发送用户创建消息到Kafka失败", e);
        }
    }

    /**
     * 发送通知消息
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
        } catch (Exception e) {
            log.error("发送通知消息到RabbitMQ失败: userId={}, type={}, error={}", userId, type, e.getMessage(), e);
            throw new RuntimeException("发送通知消息失败", e);
        }
    }
}

/**
 * RabbitMQ消息消费者
 *
 * @author spring4demo
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
     */
    @RabbitHandler
    public void handleUserCreated(UserCreatedMessage message) {
        log.info("消费RabbitMQ用户创建消息: userId={}, username={}", message.getUserId(), message.getUsername());

        try {
            handleUserCreatedEvent(message);
            log.info("处理用户创建事件成功: userId={}", message.getUserId());
        } catch (Exception e) {
            log.error("处理用户创建事件失败: userId={}, error={}", message.getUserId(), e.getMessage(), e);
            throw e; // 抛出异常以触发重试机制
        }
    }

    /**
     * 处理通知消息
     */
    @RabbitHandler
    public void handleNotification(NotificationMessage message) {
        log.info("消费RabbitMQ通知消息: userId={}, type={}, title={}",
                message.getUserId(), message.getType(), message.getTitle());

        try {
            handleNotificationEvent(message);
            log.info("处理通知消息成功: userId={}, type={}", message.getUserId(), message.getType());
        } catch (Exception e) {
            log.error("处理通知消息失败: userId={}, type={}, error={}",
                    message.getUserId(), message.getType(), e.getMessage(), e);
            throw e; // 抛出异常以触发重试机制
        }
    }

    /**
     * 处理用户创建事件
     */
    private void handleUserCreatedEvent(UserCreatedMessage message) {
        log.info("处理用户创建事件: userId={}, username={}, email={}",
                message.getUserId(), message.getUsername(), message.getEmail());
        // TODO: 根据实际业务需求处理用户创建事件
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
                break;
            case "SMS":
                // TODO: 发送短信通知
                break;
            case "PUSH":
                // TODO: 发送推送通知
                break;
            case "SYSTEM":
                // TODO: 保存系统通知
                break;
            default:
                log.warn("未知的通知类型: userId={}, type={}", message.getUserId(), message.getType());
        }
    }
}

/**
 * Kafka消息消费者
 *
 * @author spring4demo
 * @version 2.0.0
 */
@Component
@Slf4j
public class KafkaMessageConsumer {

    /**
     * 消费用户创建消息（Kafka）
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
     * 处理用户创建事件
     */
    private void handleUserCreatedEvent(UserCreatedMessage message) {
        log.info("处理用户创建事件（Kafka）: userId={}, username={}, email={}",
                message.getUserId(), message.getUsername(), message.getEmail());
        // TODO: 根据实际业务需求处理用户创建事件
    }
}
```

**配置示例**:

```yaml
# RabbitMQ配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: 1
        default-requeue-rejected: false

# Kafka配置
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: spring4demo-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
```

**优势**:
- ✅ 更轻量级，无额外依赖
- ✅ 更直接的 API，更易调试
- ✅ 更好的性能和资源利用
- ✅ 更灵活的配置和控制
- ✅ 完全不依赖 Spring Cloud 生态
    @Bean
    public Consumer<NotificationMessage> notification() {
        return message -> {
            log.info("消费RabbitMQ通知消息: {}", message);
            handleNotification(message);
        };
    }

    private void handleUserCreated(UserCreatedMessage message) {
        // 处理用户创建事件逻辑
        log.info("处理用户创建事件: userId={}", message.getUserId());
    }

    private void handleNotification(NotificationMessage message) {
        // 处理通知消息逻辑
        log.info("处理通知消息: content={}", message.getContent());
    }
}

/**
 * 消息消费者（Kafka）
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@Slf4j
public class KafkaMessageConsumer {

    /**
     * 消费用户创建消息
     */
    @Bean
    public Consumer<UserCreatedMessage> userCreatedKafka() {
        return message -> {
            log.info("消费Kafka用户创建消息: {}", message);
            handleUserCreated(message);
        };
    }

    private void handleUserCreated(UserCreatedMessage message) {
        // 处理用户创建事件逻辑
        log.info("处理用户创建事件: userId={}", message.getUserId());
    }
}

/**
 * 消息对象
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedMessage implements Serializable {

    private Long userId;
    private String username;
    private LocalDateTime createdAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Serializable {

    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
```

**application.yml配置**:

```yaml
spring:
  cloud:
    stream:
      # RabbitMQ配置
      rabbit:
        bindings:
          userCreated-out-0:
            producer:
              exchange-name: user.exchange
              routing-key-expression: "'user.created'"
          userCreated-in-0:
            consumer:
              exchange-name: user.exchange
              binding-routing-key: user.created
              queue-name: user.queue
      # Kafka配置
      kafka:
        bindings:
          userCreatedKafka-out-0:
            producer:
              topic: user-created-topic
          userCreatedKafka-in-0:
            consumer:
              topic: user-created-topic
              group: user-group
      # 默认绑定器配置
      default:
        producer:
          use-native-encoding: true
        consumer:
          use-native-encoding: true
```

**最佳实践要点**:

1. **消息可靠性**:
   - 消息持久化
   - 消息确认机制
   - 死信队列

2. **消息幂等性**:
   - 使用消息ID去重
   - 实现幂等性检查
   - 使用乐观锁

3. **消息顺序性**:
   - 单分区保证顺序
   - 消息编号
   - 顺序处理

4. **性能优化**:
   - 批量发送消息
   - 异步发送
   - 消息压缩

### 2. MongoDB文档数据库

**技术选型**: Spring Data MongoDB

**适用场景**:
- 文档数据存储
- 灵活的数据模型
- 快速原型开发

**最佳实践**:

```java
/**
 * MongoDB配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.kev1n.spring4demo.core.repository.mongo")
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/spring4demo");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "spring4demo");
    }
}

/**
 * 用户日志文档
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Document(collection = "user_logs")
@CompoundIndex(name = "user_id_created_at_idx", def = "{'userId': 1, 'createdAt': -1}")
public class UserLog {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String username;

    private String action;

    private String details;

    private String ipAddress;

    private String userAgent;

    @Indexed
    private LocalDateTime createdAt;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

/**
 * 用户日志Repository
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserLogRepository extends MongoRepository<UserLog, String> {

    /**
     * 根据用户ID查询日志
     */
    List<UserLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和动作查询日志
     */
    List<UserLog> findByUserIdAndActionOrderByCreatedAtDesc(Long userId, String action);

    /**
     * 根据时间范围查询日志
     */
    List<UserLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}

/**
 * 用户日志服务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogService {

    private final UserLogRepository userLogRepository;

    /**
     * 记录用户日志
     */
    public void logUserAction(Long userId, String username, String action, String details,
                              String ipAddress, String userAgent) {
        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setCreatedAt(LocalDateTime.now());

        userLogRepository.save(log);
        log.info("记录用户日志: userId={}, action={}", userId, action);
    }

    /**
     * 查询用户日志
     */
    public List<UserLog> getUserLogs(Long userId) {
        return userLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 查询用户日志（分页）
     */
    public Page<UserLog> getUserLogs(Long userId, Pageable pageable) {
        return userLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
```

**最佳实践要点**:

1. **文档设计**:
   - 合理的文档结构
   - 适当的索引设计
   - 嵌入式文档 vs 引用

2. **查询优化**:
   - 使用索引
   - 避免全表扫描
   - 使用投影减少数据传输

3. **性能优化**:
   - 批量操作
   - 使用连接池
   - 读写分离

### 3. Elasticsearch搜索引擎

**技术选型**: Spring Data Elasticsearch

**适用场景**:
- 全文搜索
- 数据检索
- 日志分析

**最佳实践**:

```java
/**
 * Elasticsearch配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.kev1n.spring4demo.core.repository.elasticsearch")
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}

/**
 * 文档文档
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Document(indexName = "documents")
@Setting(settingPath = "elasticsearch/settings.json")
public class DocumentDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long documentId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Object)
    private Author author;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Data
    public static class Author {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword)
        private String username;

        @Field(type = FieldType.Text)
        private String displayName;
    }
}

/**
 * 文档Repository
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface DocumentRepository extends ElasticsearchRepository<DocumentDocument, String> {

    /**
     * 全文搜索
     */
    Page<DocumentDocument> findByTitleOrContent(String title, String content, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<DocumentDocument> findByStatus(String status, Pageable pageable);

    /**
     * 根据分类查询
     */
    Page<DocumentDocument> findByCategory(String category, Pageable pageable);
}

/**
 * 文档搜索服务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentSearchService {

    private final DocumentRepository documentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 索引文档
     */
    public void indexDocument(Document document) {
        DocumentDocument doc = convertToDocumentDocument(document);
        documentRepository.save(doc);
        log.info("索引文档: documentId={}", document.getId());
    }

    /**
     * 批量索引文档
     */
    public void indexDocuments(List<Document> documents) {
        List<DocumentDocument> docs = documents.stream()
                .map(this::convertToDocumentDocument)
                .collect(Collectors.toList());
        documentRepository.saveAll(docs);
        log.info("批量索引文档: count={}", documents.size());
    }

    /**
     * 搜索文档
     */
    public Page<DocumentDocument> searchDocuments(String keyword, Pageable pageable) {
        return documentRepository.findByTitleOrContent(keyword, keyword, pageable);
    }

    /**
     * 高级搜索
     */
    public Page<DocumentDocument> advancedSearch(DocumentSearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(request.getKeyword())
                    .field("title", 2.0f)
                    .field("content", 1.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness("AUTO"));
        }

        // 状态过滤
        if (StringUtils.hasText(request.getStatus())) {
            boolQuery.filter(QueryBuilders.termQuery("status", request.getStatus()));
        }

        // 分类过滤
        if (StringUtils.hasText(request.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", request.getCategory()));
        }

        // 标签过滤
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("tags", request.getTags()));
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(request.getPageable())
                .build();

        return elasticsearchOperations.search(searchQuery, DocumentDocument);
    }

    private DocumentDocument convertToDocumentDocument(Document document) {
        DocumentDocument doc = new DocumentDocument();
        doc.setDocumentId(document.getId());
        doc.setTitle(document.getTitle());
        doc.setContent(document.getContent());
        doc.setStatus(document.getStatus().name());
        doc.setCategory(document.getCategory().getName());
        doc.setTags(document.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        doc.setCreatedAt(document.getCreatedAt());
        doc.setUpdatedAt(document.getUpdatedAt());
        return doc;
    }
}
```

**最佳实践要点**:

1. **索引设计**:
   - 合理的字段类型
   - 适当的分词器
   - 有效的索引映射

2. **查询优化**:
   - 使用复合查询
   - 合理使用过滤
   - 使用聚合

3. **性能优化**:
   - 批量索引
   - 使用索引别名
   - 定期优化索引

### 4. Neo4j图数据库

**技术选型**: Spring Data Neo4j

**适用场景**:
- 图数据存储
- 关系数据
- 社交网络

**最佳实践**:

```java
/**
 * Neo4j配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.kev1n.spring4demo.core.repository.neo4j")
public class Neo4jConfig {

    @Bean
    public org.neo4j.driver.Driver driver() {
        return GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "password"));
    }
}

/**
 * 用户节点
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Node("User")
public class UserNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("userId")
    private Long userId;

    @Property("username")
    private String username;

    @Property("email")
    private String email;

    @Relationship(type = "FRIEND", direction = Relationship.Direction.OUTGOING)
    private List<UserNode> friends;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    private List<UserNode> followers;
}

/**
 * 用户关系Repository
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserRepository extends Neo4jRepository<UserNode, Long> {

    /**
     * 根据用户ID查询
     */
    UserNode findByUserId(Long userId);

    /**
     * 查询朋友
     */
    @Query("MATCH (u:User {userId: $userId})-[:FRIEND]->(f:User) RETURN f")
    List<UserNode> findFriends(@Param("userId") Long userId);

    /**
     * 查询关注者
     */
    @Query("MATCH (u:User {userId: $userId})-[:FOLLOW]->(f:User) RETURN f")
    List<UserNode> findFollowers(@Param("userId") Long userId);
}

/**
 * 用户图服务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserGraphService {

    private final UserRepository userRepository;

    /**
     * 创建用户节点
     */
    public void createUserNode(User user) {
        UserNode node = new UserNode();
        node.setUserId(user.getId());
        node.setUsername(user.getUsername());
        node.setEmail(user.getEmail());
        userRepository.save(node);
        log.info("创建用户节点: userId={}", user.getId());
    }

    /**
     * 添加朋友关系
     */
    public void addFriend(Long userId1, Long userId2) {
        UserNode user1 = userRepository.findByUserId(userId1);
        UserNode user2 = userRepository.findByUserId(userId2);

        if (user1 != null && user2 != null) {
            if (user1.getFriends() == null) {
                user1.setFriends(new ArrayList<>());
            }
            user1.getFriends().add(user2);
            userRepository.save(user1);
            log.info("添加朋友关系: userId1={}, userId2={}", userId1, userId2);
        }
    }

    /**
     * 查询朋友
     */
    public List<UserNode> findFriends(Long userId) {
        return userRepository.findFriends(userId);
    }

    /**
     * 查询朋友的朋友
     */
    @Query("MATCH (u:User {userId: $userId})-[:FRIEND]->(f:User)-[:FRIEND]->(ff:User) RETURN DISTINCT ff")
    public List<UserNode> findFriendsOfFriends(@Param("userId") Long userId) {
        return userRepository.findFriendsOfFriends(userId);
    }
}
```

**最佳实践要点**:

1. **图模型设计**:
   - 合理的节点和关系设计
   - 适当的属性设计
   - 避免过度连接

2. **查询优化**:
   - 使用Cypher查询
   - 使用索引
   - 避免深度遍历

3. **性能优化**:
   - 批量操作
   - 使用缓存
   - 合理使用事务

### 5. InfluxDB时序数据库

**技术选型**: InfluxDB 2.x + InfluxDB Java Client

**适用场景**:
- 时序数据存储
- 监控数据
- IoT数据

**最佳实践**:

```java
/**
 * InfluxDB配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String org;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    @Bean
    public WriteApi writeApi(InfluxDBClient influxDBClient) {
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(1000)
                .flushInterval(1000)
                .build();
        return influxDBClient.makeWriteApi(writeOptions);
    }

    @Bean
    public QueryApi queryApi(InfluxDBClient influxDBClient) {
        return influxDBClient.getQueryApi();
    }
}

/**
 * 系统指标数据
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Measurement(name = "system_metrics")
public class SystemMetrics {

    @Column(name = "host", tag = true)
    private String host;

    @Column(name = "region", tag = true)
    private String region;

    @Column(name = "cpu_usage")
    private Double cpuUsage;

    @Column(name = "memory_usage")
    private Double memoryUsage;

    @Column(name = "disk_usage")
    private Double diskUsage;

    @Column(name = "network_in")
    private Long networkIn;

    @Column(name = "network_out")
    private Long networkOut;

    @Column(timestamp = true)
    private Instant timestamp;
}

/**
 * 系统指标服务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemMetricsService {

    private final WriteApi writeApi;
    private final QueryApi queryApi;

    /**
     * 写入系统指标
     */
    public void writeMetrics(SystemMetrics metrics) {
        writeApi.writeMeasurement(WritePrecision.NS, metrics);
        log.info("写入系统指标: host={}, cpu={}", metrics.getHost(), metrics.getCpuUsage());
    }

    /**
     * 查询系统指标
     */
    public List<SystemMetrics> queryMetrics(String host, Instant start, Instant end) {
        String query = String.format(
                "from(bucket: \"%s\") " +
                "|> range(start: %d, stop: %d) " +
                "|> filter(fn: (r) => r._measurement == \"system_metrics\") " +
                "|> filter(fn: (r) => r.host == \"%s\")",
                "spring4demo",
                start.toEpochMilli(),
                end.toEpochMilli(),
                host
        );

        List<FluxTable> tables = queryApi.query(query);
        return convertToMetrics(tables);
    }

    /**
     * 查询平均CPU使用率
     */
    public Double getAverageCpuUsage(String host, Instant start, Instant end) {
        String query = String.format(
                "from(bucket: \"%s\") " +
                "|> range(start: %d, stop: %d) " +
                "|> filter(fn: (r) => r._measurement == \"system_metrics\") " +
                "|> filter(fn: (r) => r.host == \"%s\") " +
                "|> filter(fn: (r) => r._field == \"cpu_usage\") " +
                "|> mean()",
                "spring4demo",
                start.toEpochMilli(),
                end.toEpochMilli(),
                host
        );

        List<FluxTable> tables = queryApi.query(query);
        if (!tables.isEmpty() && !tables.get(0).getRecords().isEmpty()) {
            return ((Double) tables.get(0).getRecords().get(0).getValue());
        }
        return 0.0;
    }

    private List<SystemMetrics> convertToMetrics(List<FluxTable> tables) {
        List<SystemMetrics> metrics = new ArrayList<>();
        // 转换逻辑
        return metrics;
    }
}
```

**最佳实践要点**:

1. **数据模型设计**:
   - 合理的measurement设计
   - 适当的tag和field设计
   - 合理的数据保留策略

2. **查询优化**:
   - 使用Flux查询语言
   - 合理使用时间范围
   - 使用聚合函数

3. **性能优化**:
   - 批量写入
   - 使用压缩
   - 合理的保留策略

---

### 1. Web层技术实现

#### 1.1 WebFlux 响应式编程

**技术选型**: Spring WebFlux + Reactor

**适用场景**:
- 高并发、低延迟的API接口
- 流式数据处理
- 实时数据推送

**最佳实践**:

```java
/**
 * 用户响应式控制器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/reactive/users")
@RequiredArgsConstructor
@Slf4j
public class UserReactiveController {

    private final UserService userService;

    /**
     * 响应式查询用户列表
     * 
     * 使用Flux处理多个用户数据，支持背压
     */
    @GetMapping
    public Flux<UserVO> listUsers() {
        log.info("响应式查询用户列表");
        return userService.listUsersReactive()
                .map(this::convertToVO)
                .doOnComplete(() -> log.info("用户列表查询完成"));
    }

    /**
     * 响应式查询单个用户
     * 
     * 使用Mono处理单个用户数据
     */
    @GetMapping("/{id}")
    public Mono<UserVO> getUser(@PathVariable Long id) {
        log.info("响应式查询用户: {}", id);
        return userService.getUserByIdReactive(id)
                .map(this::convertToVO)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

    /**
     * 响应式创建用户
     * 
     * 使用Mono处理异步创建操作
     */
    @PostMapping
    public Mono<UserVO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        log.info("响应式创建用户: {}", dto.getUsername());
        return userService.createUserReactive(dto)
                .map(this::convertToVO);
    }

    /**
     * 响应式批量操作
     * 
     * 使用Flux处理批量数据
     */
    @PostMapping("/batch")
    public Flux<UserVO> batchCreate(@Valid @RequestBody List<UserCreateDTO> dtos) {
        log.info("响应式批量创建用户: {}", dtos.size());
        return Flux.fromIterable(dtos)
                .flatMap(userService::createUserReactive)
                .map(this::convertToVO);
    }

    /**
     * 响应式流式数据
     * 
     * 使用ServerSentEvent实现实时数据推送
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UserVO>> streamUsers() {
        log.info("响应式流式推送用户数据");
        return userService.streamUsersReactive()
                .map(user -> ServerSentEvent.<UserVO>builder()
                        .data(convertToVO(user))
                        .id(String.valueOf(user.getId()))
                        .build())
                .delayElements(Duration.ofSeconds(1));
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
```

**Service层实现**:

```java
/**
 * 用户响应式服务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final UserReactiveRepository userReactiveRepository;

    /**
     * 响应式查询用户列表
     */
    public Flux<User> listUsersReactive() {
        return Flux.defer(() -> Flux.fromIterable(userMapper.selectList(null)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 响应式查询单个用户
     */
    public Mono<User> getUserByIdReactive(Long id) {
        return Mono.fromCallable(() -> userMapper.selectById(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 响应式创建用户
     */
    public Mono<User> createUserReactive(UserCreateDTO dto) {
        return Mono.fromCallable(() -> {
            User user = new User();
            BeanUtils.copyProperties(dto, user);
            userMapper.insert(user);
            return user;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 响应式流式数据
     */
    public Flux<User> streamUsersReactive() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> listUsersReactive());
    }
}
```

**最佳实践要点**:

1. **使用Schedulers切换线程**:
   - `subscribeOn(Schedulers.boundedElastic())`: 用于阻塞操作
   - `publishOn(Schedulers.parallel())`: 用于CPU密集型操作

2. **错误处理**:
   ```java
   .onErrorResume(UserNotFoundException.class, e -> Mono.empty())
   .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
   ```

3. **超时控制**:
   ```java
   .timeout(Duration.ofSeconds(5))
   ```

4. **背压处理**:
   ```java
   .onBackpressureBuffer(1000)
   ```

5. **性能优化**:
   - 避免在响应式流中使用阻塞操作
   - 合理使用缓存
   - 使用flatMap而不是map进行异步操作

#### 1.2 WebSocket 实时通信

**技术选型**: Spring WebSocket + STOMP

**适用场景**:
- 实时消息推送
- 在线聊天
- 实时数据监控

**最佳实践**:

```java
/**
 * WebSocket配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
```

```java
/**
 * WebSocket消息处理器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 发送用户状态更新消息
     */
    public void sendUserStatusUpdate(Long userId, String status) {
        UserStatusMessage message = new UserStatusMessage(userId, status);
        messagingTemplate.convertAndSend("/topic/user-status", message);
        log.info("发送用户状态更新: userId={}, status={}", userId, status);
    }

    /**
     * 发送用户专属消息
     */
    public void sendUserPrivateMessage(Long userId, String content) {
        UserMessage message = new UserMessage(userId, content);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                message
        );
        log.info("发送用户专属消息: userId={}", userId);
    }

    /**
     * 广播系统消息
     */
    public void broadcastSystemMessage(String content) {
        SystemMessage message = new SystemMessage(content);
        messagingTemplate.convertAndSend("/topic/system", message);
        log.info("广播系统消息: {}", content);
    }
}
```

```java
/**
 * WebSocket消息监听器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageListener {

    private final UserService userService;

    /**
     * 监听用户订阅
     */
    @SubscribeMapping("/topic/user-status")
    public UserStatusMessage subscribeUserStatus() {
        log.info("用户订阅用户状态");
        return new UserStatusMessage(null, "CONNECTED");
    }

    /**
     * 监听用户消息
     */
    @MessageMapping("/app/user/message")
    @SendTo("/topic/user-messages")
    public UserMessage handleUserMessage(UserMessage message) {
        log.info("收到用户消息: {}", message);
        // 处理消息逻辑
        return message;
    }
}
```

**前端连接示例**:

```javascript
// WebSocket连接
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 订阅用户状态
    stompClient.subscribe('/topic/user-status', function(message) {
        const status = JSON.parse(message.body);
        console.log('User Status:', status);
    });
    
    // 订阅用户专属消息
    stompClient.subscribe('/user/queue/messages', function(message) {
        const msg = JSON.parse(message.body);
        console.log('Private Message:', msg);
    });
    
    // 发送消息
    stompClient.send('/app/user/message', {}, JSON.stringify({
        userId: 1,
        content: 'Hello'
    }));
});
```

**最佳实践要点**:

1. **连接管理**:
   - 实现心跳机制
   - 处理断线重连
   - 限制连接数量

2. **消息安全**:
   - 消息加密
   - 权限验证
   - 防止消息注入

3. **性能优化**:
   - 使用消息压缩
   - 批量发送消息
   - 实现消息缓存

#### 1.3 GraphQL API

**技术选型**: Spring GraphQL + GraphQL Java Tools

**适用场景**:
- 灵活的数据查询
- 减少API调用次数
- 移动端API

**最佳实践**:

```graphql
# Schema定义
type User {
    id: ID!
    username: String!
    email: String!
    phone: String
    realName: String
    avatar: String
    status: Int
    deptId: ID
    createTime: String
    updateTime: String
    role: Role
}

type Role {
    id: ID!
    roleName: String!
    roleCode: String!
    description: String
}

type Query {
    user(id: ID!): User
    users(limit: Int, offset: Int): [User!]!
    userSearch(keyword: String!): [User!]!
}

type Mutation {
    createUser(input: UserCreateInput!): User!
    updateUser(id: ID!, input: UserUpdateInput!): User!
    deleteUser(id: ID!): Boolean!
}

input UserCreateInput {
    username: String!
    password: String!
    email: String!
    phone: String
    realName: String
}

input UserUpdateInput {
    username: String
    email: String
    phone: String
    realName: String
    status: Int
}
```

```java
/**
 * GraphQL查询处理器
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserService userService;

    /**
     * 查询单个用户
     */
    @QueryMapping
    public User user(@Argument String id) {
        return userService.getById(Long.valueOf(id));
    }

    /**
     * 查询用户列表
     */
    @QueryMapping
    public List<User> users(@Argument Integer limit, @Argument Integer offset) {
        return userService.listUsers(limit, offset);
    }

    /**
     * 搜索用户
     */
    @QueryMapping
    public List<User> userSearch(@Argument String keyword) {
        return userService.searchUsers(keyword);
    }

    /**
     * 创建用户
     */
    @MutationMapping
    public User createUser(@Argument UserCreateInput input) {
        return userService.createUser(input);
    }

    /**
     * 更新用户
     */
    @MutationMapping
    public User updateUser(@Argument String id, @Argument UserUpdateInput input) {
        return userService.updateUser(Long.valueOf(id), input);
    }

    /**
     * 删除用户
     */
    @MutationMapping
    public boolean deleteUser(@Argument String id) {
        return userService.deleteUser(Long.valueOf(id));
    }
}
```

**DataFetcher实现**:

```java
/**
 * 用户DataFetcher
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserDataFetcher implements DataFetcher<User> {

    private final UserService userService;

    @Override
    public User get(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id");
        return userService.getById(Long.valueOf(id));
    }
}
```

**最佳实践要点**:

1. **Schema设计**:
   - 合理的粒度划分
   - 使用类型和接口
   - 定义清晰的输入类型

2. **查询优化**:
   - 实现DataLoader解决N+1问题
   - 使用分页
   - 实现缓存

3. **安全控制**:
   - 权限验证
   - 查询深度限制
   - 查询复杂度限制

### 2. 数据库分库分表

**技术选型**: ShardingSphere

**适用场景**:
- 单表数据量超过千万级
- 单库连接数瓶颈
- 需要提高并发性能

**最佳实践**:

```java
/**
 * ShardingSphere配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableSharding
public class ShardingConfig {

    /**
     * 分片规则配置
     */
    @Bean
    public ShardingRuleConfiguration shardingRuleConfig() {
        ShardingRuleConfiguration config = new ShardingRuleConfiguration();
        
        // 用户表分片规则
        TableRuleConfiguration userRule = new TableRuleConfiguration("sys_user", 
                "ds${0..1}.sys_user_${0..1}");
        
        // 分库策略：根据用户ID取模
        userRule.setDatabaseShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("id", "dbShardingAlgorithm"));
        
        // 分表策略：根据用户ID取模
        userRule.setTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("id", "tableShardingAlgorithm"));
        
        config.getTableRuleConfigs().add(userRule);
        
        return config;
    }

    /**
     * 数据源配置
     */
    @Bean
    public DataSource shardingDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds0", createDataSource("jdbc:mysql://localhost:3306/spring4demo_0"));
        dataSourceMap.put("ds1", createDataSource("jdbc:mysql://localhost:3306/spring4demo_1"));
        
        return ShardingDataSourceFactory.createDataSource(
                dataSourceMap, 
                shardingRuleConfig(), 
                new Properties()
        );
    }

    private DataSource createDataSource(String url) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}
```

```java
/**
 * 分库算法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class DatabaseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, 
                             PreciseShardingValue<Long> shardingValue) {
        Long userId = shardingValue.getValue();
        // 根据用户ID的奇偶性分库
        Long dbIndex = userId % 2;
        return "ds" + dbIndex;
    }
}
```

```java
/**
 * 分表算法
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public class TableShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, 
                             PreciseShardingValue<Long> shardingValue) {
        Long userId = shardingValue.getValue();
        // 根据用户ID的奇偶性分表
        Long tableIndex = userId % 2;
        return "sys_user_" + tableIndex;
    }
}
```

**最佳实践要点**:

1. **分片策略**:
   - 合理选择分片键
   - 避免跨分片查询
   - 考虑数据倾斜问题

2. **性能优化**:
   - 使用广播表
   - 合理使用绑定表
   - 实现读写分离

3. **运维管理**:
   - 监控分片性能
   - 实现数据迁移
   - 处理扩容缩容

### 3. 缓存集群

**技术选型**: Caffeine + Redis 双缓存

**适用场景**:
- 高频访问数据
- 降低数据库压力
- 提高响应速度

**最佳实践**:

```java
/**
 * 缓存配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine本地缓存配置
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(1000)
                // 写入后过期时间
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 访问后过期时间
                .expireAfterAccess(5, TimeUnit.MINUTES)
                // 刷新后过期时间
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                // 记录统计信息
                .recordStats());
        return cacheManager;
    }

    /**
     * Redis分布式缓存配置
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // 设置value序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 设置过期时间
                .entryTtl(Duration.ofMinutes(30))
                // 不缓存null值
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * 多级缓存管理器
     */
    @Bean
    @Primary
    public CacheManager multiLevelCacheManager(CacheManager caffeineCacheManager, 
                                               CacheManager redisCacheManager) {
        return new CompositeCacheManager(caffeineCacheManager, redisCacheManager);
    }
}
```

```java
/**
 * 用户缓存服务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final UserMapper userMapper;
    private final Cache caffeineCache;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户（多级缓存）
     */
    public User getUserWithCache(Long userId) {
        String cacheKey = "user:" + userId;
        
        // 1. 先查Caffeine本地缓存
        User user = (User) caffeineCache.get(cacheKey);
        if (user != null) {
            log.info("从Caffeine缓存获取用户: {}", userId);
            return user;
        }
        
        // 2. 再查Redis分布式缓存
        user = (User) redisTemplate.opsForValue().get(cacheKey);
        if (user != null) {
            log.info("从Redis缓存获取用户: {}", userId);
            // 回填到Caffeine缓存
            caffeineCache.put(cacheKey, user);
            return user;
        }
        
        // 3. 最后查数据库
        user = userMapper.selectById(userId);
        if (user != null) {
            log.info("从数据库获取用户: {}", userId);
            // 写入Redis缓存
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
            // 写入Caffeine缓存
            caffeineCache.put(cacheKey, user);
        }
        
        return user;
    }

    /**
     * 更新用户（缓存穿透）
     */
    public void updateUserWithCache(Long userId, User user) {
        // 更新数据库
        userMapper.updateById(user);
        
        String cacheKey = "user:" + userId;
        
        // 删除Caffeine缓存
        caffeineCache.invalidate(cacheKey);
        
        // 删除Redis缓存
        redisTemplate.delete(cacheKey);
        
        log.info("更新用户并清除缓存: {}", userId);
    }

    /**
     * 删除用户（缓存删除）
     */
    public void deleteUserWithCache(Long userId) {
        // 删除数据库
        userMapper.deleteById(userId);
        
        String cacheKey = "user:" + userId;
        
        // 删除Caffeine缓存
        caffeineCache.invalidate(cacheKey);
        
        // 删除Redis缓存
        redisTemplate.delete(cacheKey);
        
        log.info("删除用户并清除缓存: {}", userId);
    }
}
```

**最佳实践要点**:

1. **缓存策略**:
   - Cache-Aside模式
   - 先更新数据库，再删除缓存
   - 避免缓存雪崩和穿透

2. **缓存一致性**:
   - 使用消息队列通知缓存更新
   - 实现缓存预热
   - 监控缓存命中率

3. **性能优化**:
   - 合理设置缓存过期时间
   - 使用布隆过滤器
   - 实现缓存降级

### 4. 消息队列

**技术选型**: RabbitMQ

**适用场景**:
- 异步处理
- 系统解耦
- 流量削峰

**最佳实践**:

```java
/**
 * RabbitMQ配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 用户队列
     */
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable("user.queue")
                .withArgument("x-dead-letter-exchange", "user.dlx")
                .build();
    }

    /**
     * 用户交换机
     */
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange("user.exchange");
    }

    /**
     * 用户绑定
     */
    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(userExchange())
                .with("user.routing.key");
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue userDeadLetterQueue() {
        return QueueBuilder.durable("user.dlx.queue").build();
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange userDeadLetterExchange() {
        return new DirectExchange("user.dlx");
    }

    /**
     * 死信绑定
     */
    @Bean
    public Binding userDeadLetterBinding() {
        return BindingBuilder.bind(userDeadLetterQueue())
                .to(userDeadLetterExchange())
                .with("user.dlx.routing.key");
    }
}
```

```java
/**
 * 用户消息生产者
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送用户创建消息
     */
    public void sendUserCreateMessage(User user) {
        UserCreateMessage message = new UserCreateMessage(user.getId(), user.getUsername());
        rabbitTemplate.convertAndSend("user.exchange", "user.routing.key", message);
        log.info("发送用户创建消息: {}", message);
    }

    /**
     * 发送用户更新消息
     */
    public void sendUserUpdateMessage(User user) {
        UserUpdateMessage message = new UserUpdateMessage(user.getId(), user.getUsername());
        rabbitTemplate.convertAndSend("user.exchange", "user.routing.key", message);
        log.info("发送用户更新消息: {}", message);
    }

    /**
     * 发送用户删除消息
     */
    public void sendUserDeleteMessage(Long userId) {
        UserDeleteMessage message = new UserDeleteMessage(userId);
        rabbitTemplate.convertAndSend("user.exchange", "user.routing.key", message);
        log.info("发送用户删除消息: {}", message);
    }

    /**
     * 发送用户统计消息
     * 
     * TODO: 待实现统计功能后启用
     */
    public void sendUserStatsMessage(UserStatsMessage message) {
        // TODO: 发送用户统计消息
        // rabbitTemplate.convertAndSend("user.exchange", "user.stats.routing.key", message);
        log.info("用户统计消息发送功能待实现: {}", message);
    }
}
```

```java
/**
 * 用户消息消费者
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMessageConsumer {

    private final UserService userService;
    private final UserCacheService userCacheService;

    /**
     * 消费用户创建消息
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserCreateMessage(UserCreateMessage message) {
        log.info("消费用户创建消息: {}", message);
        try {
            // 更新缓存
            User user = userService.getById(message.getUserId());
            if (user != null) {
                userCacheService.getUserWithCache(message.getUserId());
            }
        } catch (Exception e) {
            log.error("处理用户创建消息失败", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    /**
     * 消费用户更新消息
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserUpdateMessage(UserUpdateMessage message) {
        log.info("消费用户更新消息: {}", message);
        try {
            // 清除缓存
            userCacheService.updateUserWithCache(message.getUserId(), null);
        } catch (Exception e) {
            log.error("处理用户更新消息失败", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    /**
     * 消费用户删除消息
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserDeleteMessage(UserDeleteMessage message) {
        log.info("消费用户删除消息: {}", message);
        try {
            // 清除缓存
            userCacheService.deleteUserWithCache(message.getUserId());
        } catch (Exception e) {
            log.error("处理用户删除消息失败", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
```

**最佳实践要点**:

1. **消息可靠性**:
   - 使用消息确认机制
   - 实现死信队列
   - 消息持久化

2. **幂等性处理**:
   - 使用消息ID去重
   - 实现幂等性检查
   - 使用乐观锁

3. **性能优化**:
   - 批量发送消息
   - 合理设置 prefetch
   - 监控队列性能

### 5. 异步处理

**技术选型**: Spring @Async + CompletableFuture

**适用场景**:
- 耗时操作
- 并行处理
- 提高响应速度

**最佳实践**:

```java
/**
 * 异步配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名称前缀
        executor.setThreadNamePrefix("async-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
```

```java
/**
 * 用户异步服务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAsyncService {

    private final UserService userService;
    // TODO: 邮件服务 - 待实现
    // private final EmailService emailService;
    // TODO: 短信服务 - 待实现
    // private final SmsService smsService;

    /**
     * 异步创建用户
     */
    @Async
    public CompletableFuture<User> createUserAsync(UserCreateDTO dto) {
        log.info("异步创建用户: {}", dto.getUsername());
        User user = userService.createUser(dto);
        return CompletableFuture.completedFuture(user);
    }

    /**
     * 异步发送欢迎邮件
     * 
     * TODO: 待实现邮件服务后启用
     */
    @Async
    public CompletableFuture<Void> sendWelcomeEmailAsync(Long userId) {
        log.info("异步发送欢迎邮件: {}", userId);
        User user = userService.getById(userId);
        // TODO: 调用邮件服务发送欢迎邮件
        // emailService.sendWelcomeEmail(user);
        log.info("邮件发送功能待实现: userId={}", userId);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 异步发送欢迎短信
     * 
     * TODO: 待实现短信服务后启用
     */
    @Async
    public CompletableFuture<Void> sendWelcomeSmsAsync(Long userId) {
        log.info("异步发送欢迎短信: {}", userId);
        User user = userService.getById(userId);
        // TODO: 调用短信服务发送欢迎短信
        // smsService.sendWelcomeSms(user);
        log.info("短信发送功能待实现: userId={}", userId);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 并行处理用户注册
     */
    public CompletableFuture<User> registerUserParallel(UserCreateDTO dto) {
        log.info("并行处理用户注册: {}", dto.getUsername());
        
        // 异步创建用户
        CompletableFuture<User> userFuture = createUserAsync(dto);
        
        // 等待用户创建完成
        return userFuture.thenCompose(user -> {
            // 并行发送欢迎邮件和短信
            CompletableFuture<Void> emailFuture = sendWelcomeEmailAsync(user.getId());
            CompletableFuture<Void> smsFuture = sendWelcomeSmsAsync(user.getId());
            
            // 等待所有异步任务完成
            return CompletableFuture.allOf(emailFuture, smsFuture)
                    .thenApply(v -> user);
        });
    }

    /**
     * 批量异步处理用户
     */
    public CompletableFuture<List<User>> batchProcessUsersAsync(List<Long> userIds) {
        log.info("批量异步处理用户: {}", userIds.size());
        
        List<CompletableFuture<User>> futures = userIds.stream()
                .map(userId -> CompletableFuture.supplyAsync(() -> {
                    log.info("异步处理用户: {}", userId);
                    User user = userService.getById(userId);
                    // 处理用户逻辑
                    return user;
                }))
                .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }
}
```

**最佳实践要点**:

1. **线程池管理**:
   - 合理设置线程池参数
   - 监控线程池状态
   - 实现优雅停机

2. **异常处理**:
   - 使用 exceptionally 处理异常
   - 实现重试机制
   - 记录异常日志

3. **性能优化**:
   - 合理使用并行流
   - 避免线程阻塞
   - 实现任务超时控制

### 6. 分布式事务

**技术选型**: Seata

**适用场景**:
- 跨服务事务
- 分布式数据一致性
- 微服务架构

**最佳实践**:

```java
/**
 * Seata配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class SeataConfig {

    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        return new GlobalTransactionScanner("spring4demo", "my_test_tx_group");
    }
}
```

```java
/**
 * 用户分布式事务服务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDistributedService {

    private final UserService userService;
    // TODO: 订单服务 - 待实现
    // private final OrderService orderService;
    private final UserCacheService userCacheService;
    private final UserMessageProducer userMessageProducer;

    /**
     * 创建用户订单（分布式事务）
     * 
     * TODO: 待实现订单服务后启用
     */
    @GlobalTransactional(name = "create-user-order", rollbackFor = Exception.class)
    public void createUserOrder(Long userId, OrderCreateDTO orderDTO) {
        log.info("开始创建用户订单: userId={}", userId);
        
        try {
            // 1. 创建订单
            // Order order = orderService.createOrder(userId, orderDTO);
            // log.info("订单创建成功: orderId={}", order.getId());
            log.info("订单服务待实现");
            
            // 2. 扣减用户余额
            // userService.deductBalance(userId, order.getAmount());
            // log.info("用户余额扣减成功: userId={}, amount={}", userId, order.getAmount());
            
            // 3. 清除用户缓存
            userCacheService.deleteUserWithCache(userId);
            log.info("用户缓存清除成功: userId={}", userId);
            
            // 4. 发送订单创建消息
            userMessageProducer.sendUserCreateMessage(userService.getById(userId));
            log.info("订单创建消息发送成功");
            
        } catch (Exception e) {
            log.error("创建用户订单失败，回滚事务: userId={}", userId, e);
            throw new RuntimeException("创建用户订单失败", e);
        }
    }

    /**
     * 用户注册（分布式事务）
     * 
     * TODO: 待实现订单服务后启用
     */
    @GlobalTransactional(name = "register-user", rollbackFor = Exception.class)
    public User registerUser(UserCreateDTO userDTO, OrderCreateDTO orderDTO) {
        log.info("开始用户注册: username={}", userDTO.getUsername());
        
        try {
            // 1. 创建用户
            User user = userService.createUser(userDTO);
            log.info("用户创建成功: userId={}", user.getId());
            
            // 2. 创建初始订单
            // Order order = orderService.createOrder(user.getId(), orderDTO);
            // log.info("初始订单创建成功: orderId={}", order.getId());
            log.info("订单服务待实现");
            
            // 3. 预热用户缓存
            userCacheService.getUserWithCache(user.getId());
            log.info("用户缓存预热成功: userId={}", user.getId());
            
            // 4. 发送用户注册消息
            userMessageProducer.sendUserCreateMessage(user);
            log.info("用户注册消息发送成功: userId={}", user.getId());
            
            return user;
            
        } catch (Exception e) {
            log.error("用户注册失败，回滚事务: username={}", userDTO.getUsername(), e);
            throw new RuntimeException("用户注册失败", e);
        }
    }
}
```

**最佳实践要点**:

1. **事务模式**:
   - AT模式：默认模式，适合大多数场景
   - TCC模式：适合强一致性场景
   - Saga模式：适合长事务场景

2. **性能优化**:
   - 合理设置事务超时时间
   - 优化锁竞争
   - 实现异步补偿

3. **异常处理**:
   - 正确处理异常
   - 实现重试机制
   - 监控事务状态

### 7. 定时任务

**技术选型**: Spring Task + Quartz

**适用场景**:
- 定时数据清理
- 定时数据统计
- 定时任务调度

**最佳实践**:

```java
/**
 * 定时任务配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    /**
     * 定时任务线程池
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("schedule-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}
```

```java
/**
 * 用户定时任务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserScheduledTask {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final UserMessageProducer userMessageProducer;

    /**
     * 清理过期用户缓存
     * 每小时执行一次
     * 
     * TODO: 需要UserService实现清理过期缓存的方法
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredUserCache() {
        log.info("开始清理过期用户缓存");
        try {
            // TODO: 调用UserService清理过期缓存
            // userService.cleanExpiredCache();
            log.info("过期用户缓存清理功能待实现");
        } catch (Exception e) {
            log.error("清理过期用户缓存失败", e);
        }
    }

    /**
     * 统计活跃用户数
     * 每天凌晨1点执行
     * 
     * TODO: 需要UserService实现统计活跃用户的方法
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void countActiveUsers() {
        log.info("开始统计活跃用户数");
        try {
            // TODO: 调用UserService统计活跃用户
            // long activeCount = userService.countActiveUsers();
            long activeCount = 0;
            log.info("活跃用户数统计完成: {}", activeCount);
            
            // 发送统计消息
            UserStatsMessage message = new UserStatsMessage("active_users", activeCount);
            userMessageProducer.sendUserStatsMessage(message);
        } catch (Exception e) {
            log.error("统计活跃用户数失败", e);
        }
    }

    /**
     * 预热用户缓存
     * 每天凌晨2点执行
     * 
     * TODO: 需要UserService实现获取活跃用户列表的方法
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void warmUpUserCache() {
        log.info("开始预热用户缓存");
        try {
            // TODO: 调用UserService获取活跃用户列表
            // List<User> activeUsers = userService.listActiveUsers();
            List<User> activeUsers = new ArrayList<>();
            activeUsers.forEach(user -> {
                userCacheService.getUserWithCache(user.getId());
            });
            log.info("用户缓存预热完成: {} 个用户", activeUsers.size());
        } catch (Exception e) {
            log.error("预热用户缓存失败", e);
        }
    }

    /**
     * 清理无效用户
     * 每周日凌晨3点执行
     * 
     * TODO: 需要UserService实现清理无效用户的方法
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void cleanInvalidUsers() {
        log.info("开始清理无效用户");
        try {
            // TODO: 调用UserService清理无效用户
            // int count = userService.cleanInvalidUsers();
            int count = 0;
            log.info("无效用户清理完成: {} 个用户", count);
        } catch (Exception e) {
            log.error("清理无效用户失败", e);
        }
    }

    /**
     * 同步用户数据
     * 每5分钟执行一次
     * 
     * TODO: 需要UserService实现数据同步的方法
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncUserData() {
        log.info("开始同步用户数据");
        try {
            // TODO: 调用UserService同步数据
            // userService.syncUserData();
            log.info("用户数据同步功能待实现");
        } catch (Exception e) {
            log.error("同步用户数据失败", e);
        }
    }
}
```

**Quartz任务示例**:

```java
/**
 * Quartz任务配置
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail userStatsJobDetail() {
        return JobBuilder.newJob(UserStatsJob.class)
                .withIdentity("userStatsJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger userStatsJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(userStatsJobDetail())
                .withIdentity("userStatsTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                .build();
    }
}
```

```java
/**
 * 用户统计任务
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatsJob extends QuartzJobBean {

    private final UserService userService;
    private final UserMessageProducer userMessageProducer;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("开始执行用户统计任务");
        try {
            // 统计用户数据
            // TODO: 需要UserService实现统计用户数据的方法
            // UserStats stats = userService.calculateUserStats();
            UserStats stats = new UserStats();
            
            // 发送统计消息
            userMessageProducer.sendUserStatsMessage(stats);
            
            log.info("用户统计任务执行完成: {}", stats);
        } catch (Exception e) {
            log.error("用户统计任务执行失败", e);
        }
    }
}
```

**最佳实践要点**:

1. **任务调度**:
   - 合理设置执行时间
   - 避免任务重叠
   - 实现任务超时控制

2. **异常处理**:
   - 捕获并记录异常
   - 实现任务重试
   - 发送告警通知

3. **性能优化**:
   - 使用分布式锁
   - 实现任务分片
   - 监控任务性能

---

## 📝 总结

本文档提供了Spring4demo项目工程框架搭建阶段的技术架构最佳实践，涵盖了Web层、数据库、缓存、消息队列、异步处理、分布式事务和定时任务等核心技术领域。

### 技术栈总结

| 技术领域 | 选型方案 | 适用场景 |
|---------|---------|---------|
| **WebFlux** | Spring WebFlux + Reactor | 高并发、低延迟、流式数据 |
| **WebSocket** | Spring WebSocket + STOMP | 实时通信、消息推送 |
| **GraphQL** | Spring GraphQL | 灵活查询、减少调用 |
| **分库分表** | ShardingSphere | 大数据量、高并发 |
| **双缓存** | Caffeine + Redis | 高频访问、降低压力 |
| **消息队列** | RabbitMQ | 异步处理、系统解耦 |
| **异步处理** | Spring @Async + CompletableFuture | 耗时操作、并行处理 |
| **分布式事务** | Seata | 跨服务事务、数据一致性 |
| **定时任务** | Spring Task + Quartz | 定时调度、数据清理 |

### 实施建议

1. **优先级排序**:
   - P1: WebFlux、WebSocket、分库分表、双缓存、消息队列、异步处理、分布式事务、定时任务
   - P2: GraphQL

2. **实施步骤**:
   - 第一步：实现基础功能
   - 第二步：完善异常处理
   - 第三步：性能优化
   - 第四步：监控告警

3. **测试验证**:
   - 单元测试
   - 集成测试
   - 性能测试
   - 压力测试

4. **文档维护**:
   - 及时更新文档
   - 记录最佳实践
   - 分享经验教训

---

**文档结束**

*本文档由架构师生成，基于Spring Boot 4.0.1和Java 25技术栈。*
*生成时间: 2026年1月7日*
*文档版本: v3.0.0*
*项目阶段: 工程框架搭建阶段*