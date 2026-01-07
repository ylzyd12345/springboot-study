# Spring4demo 项目架构设计参考文档

## 📋 文档信息

| 项目 | 内容 |
|------|------|
| **文档名称** | Spring4demo 项目架构设计参考文档 |
| **版本号** | v3.1.0 |
| **生成日期** | 2026-01-07 |
| **更新日期** | 2026-01-07 |
| **文档类型** | 架构师视角技术架构参考 |
| **项目阶段** | 工程框架搭建阶段 |

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
| **WebFlux** | P1 | 🔄 待实现 |
| **WebSocket** | P1 | 🔄 待实现 |
| **GraphQL** | P2 | 🔄 待实现 |
| **数据库分库分表** | P1 | 🔄 待实现 |
| **Caffeine+Redis双缓存** | P1 | 🔄 待实现 |
| **MQ消息队列** | P1 | 🔄 待实现 |
| **异步处理** | P1 | 🔄 待实现 |
| **分布式事务** | P1 | 🔄 待实现 |
| **定时任务** | P1 | 🔄 待实现 |

---

## 📚 技术架构最佳实践

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