# 测试方案设计文档

## 概述

本文档详细说明了 spring4demo 项目的完整测试方案，包括框架层面、数据存储、消息中间件、分布式组件等各模块的可测试点和推荐测试方案。

## 一、框架层面测试点

### 1. Spring Boot 核心框架

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **配置加载** | `@ConfigurationProperties` 配置绑定 | 单元测试 + `@ConfigurationPropertiesTest` |
| **条件装配** | `@ConditionalOnProperty` 等条件注解 | 单元测试 + 不同 Profile |
| **Bean 生命周期** | `@PostConstruct`、`@PreDestroy` | 单元测试 + `@SpringBootTest` |
| **AOP 切面** | 切面逻辑、拦截器 | 单元测试 + `@SpringBootTest` |
| **事件机制** | `ApplicationEvent` 发布/监听 | 单元测试 + `@SpringBootTest` |
| **Bean 验证** | `@Valid`、`@Validated` 参数校验 | 单元测试 + `@SpringBootTest` |

### 2. Web 框架

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **Controller** | 请求/响应、参数绑定、异常处理 | `MockMvc` + `@WebMvcTest` |
| **RestTemplate/WebClient** | HTTP 客户端调用 | WireMock + Testcontainers |
| **WebSocket** | 消息推送、连接管理 | `@SpringBootTest` + WebSocket 客户端 |
| **GraphQL** | 查询、变更、订阅 | `@SpringBootTest` + GraphQL 测试工具 |
| **WebFlux** | 响应式流 | `@WebFluxTest` + StepVerifier |

---

## 二、数据存储测试点

### 1. MySQL

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **MyBatis-Plus Mapper** | CRUD、条件查询、分页 | H2 内存库（单元测试）<br>Testcontainers MySQL（集成测试） |
| **动态数据源** | 数据源切换、事务管理 | H2 多数据源（单元测试）<br>Testcontainers MySQL（集成测试） |
| **Druid 连接池** | 连接池配置、监控 | H2 内存库 + Mock 监控 |
| **事务管理** | `@Transactional`、事务传播 | H2 内存库 + `@Transactional` |

#### Testcontainers MySQL 示例

```java
@Testcontainers
class UserRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    void testSaveUser() {
        // 测试代码
    }
}
```

### 2. Redis

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **基础操作** | String、Hash、List、Set、ZSet | Testcontainers Redis |
| **Redisson** | 分布式锁、限流、布隆过滤器 | Testcontainers Redis |
| **缓存注解** | `@Cacheable`、`@CacheEvict` | Testcontainers Redis |
| **Session 共享** | Spring Session + Redis | Testcontainers Redis |
| **Pub/Sub** | 消息发布/订阅 | Testcontainers Redis |

#### Testcontainers Redis 示例

```java
@Testcontainers
class RedisServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void testRedisOperations() {
        // 测试代码
    }
}
```

### 3. Elasticsearch

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **索引管理** | 创建、删除、更新索引 | Testcontainers Elasticsearch |
| **文档操作** | CRUD、批量操作 | Testcontainers Elasticsearch |
| **复杂查询** | 搜索、聚合、高亮 | Testcontainers Elasticsearch |
| **同步机制** | MySQL → ES 同步 | Testcontainers MySQL + ES |

#### Testcontainers Elasticsearch 示例

```java
@Testcontainers
class ElasticsearchTest {

    @Container
    static ElasticsearchContainer es = new ElasticsearchContainer(
        "docker.elastic.co/elasticsearch/elasticsearch:8.11.4"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", es::getHttpHostAddress);
    }
}
```

### 4. MongoDB

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **文档操作** | CRUD、聚合查询 | Testcontainers MongoDB |
| **索引管理** | 创建、删除索引 | Testcontainers MongoDB |
| **GridFS** | 文件存储 | Testcontainers MongoDB |

### 5. Neo4j

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **节点/关系操作** | CRUD、Cypher 查询 | Testcontainers Neo4j |
| **图遍历** | 路径查找、最短路径 | Testcontainers Neo4j |

### 6. InfluxDB

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **时序数据写入** | Point 写入、批量写入 | Testcontainers InfluxDB |
| **Flux 查询** | 时序数据查询 | Testcontainers InfluxDB |

### 7. H2 内存数据库

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **单元测试数据源** | 替代 MySQL 进行快速测试 | H2 内存库 |

#### H2 配置示例

```yaml
# application-test.yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
```

---

## 三、消息中间件测试点

### 1. RabbitMQ

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **消息发送** | `RabbitTemplate` 发送消息 | Testcontainers RabbitMQ |
| **消息消费** | `@RabbitListener` 监听 | Testcontainers RabbitMQ |
| **消息确认** | ACK/NACK 机制 | Testcontainers RabbitMQ |
| **死信队列** | DLX、DLK 配置 | Testcontainers RabbitMQ |
| **延迟队列** | 延迟消息投递 | Testcontainers RabbitMQ |

#### Testcontainers RabbitMQ 示例

```java
@Testcontainers
class RabbitMQTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12")
            .withAdminPassword("admin");

    @Test
    void testSendMessage() {
        // 使用 @Autowired RabbitTemplate 发送消息
        // 使用 @RabbitListener 监听消息
    }
}
```

### 2. Kafka

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **消息发送** | `KafkaTemplate` 发送 | Testcontainers Kafka |
| **消息消费** | `@KafkaListener` 监听 | Testcontainers Kafka |
| **分区策略** | 自定义分区器 | Testcontainers Kafka |
| **消费者组** | 组内消费、Rebalance | Testcontainers Kafka |
| **事务消息** | 事务性发送/消费 | Testcontainers Kafka |

#### Testcontainers Kafka 示例

```java
@Testcontainers
class KafkaTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:3.6.1")
    );

    @Test
    void testProduceAndConsume() {
        // 使用 @Autowired KafkaTemplate 发送消息
        // 使用 @KafkaListener 监听消息
    }
}
```

### 3. RocketMQ

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **消息发送** | 同步/异步/单向发送 | Testcontainers RocketMQ |
| **消息消费** | Push/Pull 模式 | Testcontainers RocketMQ |
| **顺序消息** | 分区有序消息 | Testcontainers RocketMQ |
| **事务消息** | 两阶段提交 | Testcontainers RocketMQ |

### 4. Spring Integration

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **消息通道** | DirectChannel、PublishSubscribeChannel | 单元测试 + Mock |
| **消息转换器** | Payload 转换 | 单元测试 + `@SpringBootTest` |
| **消息端点** | Service Activator、Router | 单元测试 + `@SpringBootTest` |

### 5. RSocket

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **请求/响应** | 双向通信 | `@SpringBootTest` + RSocket 客户端 |
| **Fire-and-Forget** | 单向通信 | `@SpringBootTest` + RSocket 客户端 |
| **请求/流** | 流式响应 | `@SpringBootTest` + RSocket 客户端 |

---

## 四、安全认证测试点

### 1. Sa-Token

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **登录认证** | `StpUtil.login()`、Token 生成 | 单元测试 + Mock |
| **权限校验** | `@SaCheckPermission`、`@SaCheckRole` | 单元测试 + Mock |
| **Token 刷新** | 自动刷新、手动刷新 | 单元测试 + Mock |
| **会话管理** | 会话存储、踢人下线 | Testcontainers Redis |
| **OAuth2/OIDC** | 授权码模式、隐式模式 | Testcontainers Redis + Mock OAuth2 |

### 2. 密码加密

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **BCrypt 加密** | 密码加密、验证 | 单元测试 |
| **密码强度** | 正则校验 | 单元测试 |

---

## 五、任务调度测试点

### 1. XXL-Job

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **任务注册** | 执行器注册、心跳检测 | Mock XXL-Job Admin |
| **任务执行** | `@XxlJob` 注解方法 | 单元测试 + Mock |
| **任务日志** | 日志上报 | 单元测试 + Mock |
| **分片广播** | 分片参数、分片执行 | 单元测试 + Mock |

### 2. Spring Task

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **定时任务** | `@Scheduled`、Cron 表达式 | 单元测试 + `@SpringBootTest` |
| **异步任务** | `@Async`、线程池配置 | 单元测试 + `@SpringBootTest` |

### 3. Quartz

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **Job/Trigger** | 任务定义、触发器 | H2 内存库 + `@SpringBootTest` |
| **持久化** | 数据库存储 | Testcontainers MySQL + `@SpringBootTest` |

---

## 六、分布式组件测试点

### 1. Seata 分布式事务

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **全局事务** | `@GlobalTransactional` | Testcontainers MySQL + Seata Server |
| **分支事务** | 数据源代理、回滚机制 | Testcontainers MySQL + Seata Server |
| **事务超时** | 超时回滚 | Testcontainers MySQL + Seata Server |
| **Saga 模式** | 长事务 | Testcontainers MySQL + Seata Server |

#### Testcontainers Seata 示例

```java
@Testcontainers
class SeataTransactionTest {

    @Container
    static GenericContainer<?> seata = new GenericContainer<>("seataio/seata-server:2.0.0")
            .withExposedPorts(8091);

    @Test
    @GlobalTransactional
    void testDistributedTransaction() {
        // 测试分布式事务逻辑
    }
}
```

### 2. Redisson

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **分布式锁** | `RLock`、可重入锁 | Testcontainers Redis |
| **读写锁** | `ReadWriteLock` | Testcontainers Redis |
| **限流器** | `RRateLimiter` | Testcontainers Redis |
| **布隆过滤器** | `RBloomFilter` | Testcontainers Redis |
| **分布式集合** | `RMap`、`RSet`、`RList` | Testcontainers Redis |

### 3. 动态数据源

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **数据源切换** | `@DS` 注解、手动切换 | Testcontainers MySQL（多实例） |
| **事务绑定** | 事务与数据源绑定 | Testcontainers MySQL（多实例） |

---

## 七、文件存储测试点

### 1. RustFS (S3 兼容)

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **文件上传** | 单文件、分片上传 | Testcontainers MinIO（S3 兼容） |
| **文件下载** | 流式下载、断点续传 | Testcontainers MinIO |
| **文件删除** | 单文件、批量删除 | Testcontainers MinIO |
| **预签名 URL** | 临时访问链接 | Testcontainers MinIO |
| **生命周期** | 过期删除、版本管理 | Testcontainers MinIO |

#### Testcontainers MinIO 示例

```java
@Testcontainers
class FileStorageTest {

    @Container
    static MinIOContainer minio = new MinIOContainer("minio/minio:latest")
            .withUserName("admin")
            .withPassword("admin123");

    @Test
    void testFileUpload() {
        // 测试文件上传逻辑
    }
}
```

### 2. KKFileView 文档预览

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **预览生成** | 生成预览 URL | Mock KKFileView 服务 |
| **水印处理** | 添加水印 | Mock KKFileView 服务 |
| **缓存机制** | 预览缓存 | Mock KKFileView 服务 |

---

## 八、缓存测试点

### 1. Caffeine 本地缓存

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **缓存操作** | put/get/evict | 单元测试 |
| **过期策略** | 基于时间、基于大小 | 单元测试 |
| **异步加载** | `CacheLoader` | 单元测试 |

### 2. Guava Cache

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **缓存操作** | put/get/invalidate | 单元测试 |
| **刷新机制** | 自动刷新 | 单元测试 |

---

## 九、监控运维测试点

### 1. Spring Boot Actuator

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **健康检查** | `/actuator/health` | `@SpringBootTest` + MockMvc |
| **指标收集** | `/actuator/metrics` | `@SpringBootTest` + MockMvc |
| **环境信息** | `/actuator/env` | `@SpringBootTest` + MockMvc |
| **日志配置** | `/actuator/loggers` | `@SpringBootTest` + MockMvc |

### 2. Micrometer + Prometheus

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **自定义指标** | Counter、Gauge、Timer | 单元测试 + MeterRegistry |
| **指标导出** | Prometheus 格式 | `@SpringBootTest` + MockMvc |

### 3. Zipkin 链路追踪

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **Span 生成** | HTTP 调用、数据库调用 | Testcontainers Zipkin |
| **链路传递** | B3 Header 传递 | Testcontainers Zipkin |

#### Testcontainers Zipkin 示例

```java
@Testcontainers
class ZipkinTest {

    @Container
    static GenericContainer<?> zipkin = new GenericContainer<>("openzipkin/zipkin:2.24")
            .withExposedPorts(9411);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.zipkin.base-url", () -> "http://" + zipkin.getHost() + ":" + zipkin.getMappedPort(9411));
    }
}
```

---

## 十、API 文档测试点

### 1. SpringDoc OpenAPI

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **文档生成** | OpenAPI JSON 规范 | `@SpringBootTest` + MockMvc |
| **Schema 定义** | DTO 验证 | 单元测试 |
| **安全定义** | JWT、OAuth2 | 单元测试 |

### 2. Knife4j

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **UI 渲染** | Swagger UI | `@SpringBootTest` + MockMvc |
| **增强功能** | 文档排序、分组 | `@SpringBootTest` + MockMvc |

---

## 十一、业务模块测试点

### 1. User 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **用户 CRUD** | 创建、查询、更新、删除 | H2 内存库（单元测试）<br>Testcontainers MySQL（集成测试） |
| **用户查询** | 按用户名/邮箱/状态查询 | H2 内存库 + MyBatis-Plus |
| **分页查询** | PageHelper 分页 | H2 内存库 + PageHelper |
| **逻辑删除** | MyBatis-Plus 逻辑删除 | H2 内存库 + MyBatis-Plus |

### 2. Auth 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **登录认证** | 用户名密码登录 | H2 内存库 + Mock Sa-Token |
| **Token 管理** | 生成、刷新、失效 | Mock Sa-Token |
| **权限校验** | 接口权限控制 | Mock Sa-Token |

### 3. FileStorage 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **文件上传** | 单文件、多文件上传 | Testcontainers MinIO |
| **文件下载** | 流式下载 | Testcontainers MinIO |
| **文件删除** | 单文件、批量删除 | Testcontainers MinIO |

### 4. DocumentPreview 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **预览生成** | 生成预览 URL | Mock KKFileView |
| **支持格式** | 格式检查 | 单元测试 |

### 5. JobSchedule 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **任务注册** | XXL-Job 任务注册 | Mock XXL-Job Admin |
| **任务执行** | `@XxlJob` 方法执行 | 单元测试 + Mock |

### 6. Seata 模块

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **全局事务** | 跨库事务 | Testcontainers MySQL + Seata Server |
| **回滚测试** | 异常回滚 | Testcontainers MySQL + Seata Server |

---

## 十二、通用测试点

### 1. 工具类

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **字符串工具** | Hutool、自定义工具 | 单元测试 |
| **日期时间** | 日期格式化、计算 | 单元测试 |
| **JSON 处理** | Fastjson2 序列化/反序列化 | 单元测试 |
| **加密解密** | AES、RSA、MD5 | 单元测试 |

### 2. 异常处理

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **全局异常** | `@ControllerAdvice` | `@SpringBootTest` + MockMvc |
| **自定义异常** | 业务异常、系统异常 | 单元测试 |

### 3. 拦截器

| 测试点 | 测试内容 | 推荐方案 |
|--------|---------|---------|
| **日志拦截** | 请求日志记录 | `@SpringBootTest` + MockMvc |
| **权限拦截** | Token 验证 | `@SpringBootTest` + MockMvc |

---

## 十三、Testcontainers 支持的组件清单

| 组件 | Testcontainers 模块 | Docker 镜像 |
|------|---------------------|-----------|
| **MySQL** | `testcontainers-mysql` | `mysql:8.0` |
| **PostgreSQL** | `testcontainers-postgresql` | `postgres:16` |
| **Redis** | `testcontainers-redis` | `redis:7-alpine` |
| **MongoDB** | `testcontainers-mongodb` | `mongo:7` |
| **Elasticsearch** | `testcontainers-elasticsearch` | `elasticsearch:8.11.4` |
| **Neo4j** | `testcontainers-neo4j` | `neo4j:5.12` |
| **RabbitMQ** | `testcontainers-rabbitmq` | `rabbitmq:3.12` |
| **Kafka** | `testcontainers-kafka` | `confluentinc/cp-kafka:3.6.1` |
| **InfluxDB** | `testcontainers-influxdb` | `influxdb:2.7` |
| **MinIO** | `testcontainers-minio` | `minio/minio:latest` |
| **Zipkin** | `testcontainers` | `openzipkin/zipkin:2.24` |
| **Seata** | `testcontainers` | `seataio/seata-server:2.0.0` |
| **XXL-Job** | `testcontainers` | `xuxueli/xxl-job-admin:2.4.0` |

---

## 十四、推荐的测试分层策略

```
┌─────────────────────────────────────────────────────────┐
│  E2E 测试 (spring4demo-integration)                      │
│  - 完整业务流程                                          │
│  - Testcontainers 真实环境                               │
└─────────────────────────────────────────────────────────┘
                          ↑
┌─────────────────────────────────────────────────────────┐
│  集成测试 (IT)                                           │
│  - Controller + Service + Mapper                         │
│  - Testcontainers 或 H2                                  │
└─────────────────────────────────────────────────────────┘
                          ↑
┌─────────────────────────────────────────────────────────┐
│  单元测试 (Test)                                         │
│  - Service 层 Mock Mapper                               │
│  - Mapper 层 H2 内存库                                  │
│  - 工具类纯单元测试                                      │
└─────────────────────────────────────────────────────────┘
```

### 测试分层说明

#### 1. 单元测试 (Test)
- **目标**：测试单个类或方法的逻辑
- **特点**：执行速度快、隔离性好
- **外部依赖**：使用 Mock 模拟
- **数据源**：H2 内存数据库
- **示例**：`UserServiceImplTest.java`

#### 2. 集成测试 (IT)
- **目标**：测试多个组件之间的交互
- **特点**：测试组件协作、使用真实或模拟的外部依赖
- **外部依赖**：Testcontainers 或 Mock
- **数据源**：Testcontainers MySQL 或 H2
- **示例**：`UserControllerIT.java`

#### 3. E2E 测试
- **目标**：测试完整的业务流程
- **特点**：模拟真实用户操作、使用真实环境
- **外部依赖**：Testcontainers 真实环境
- **数据源**：Testcontainers 所有组件
- **示例**：`spring4demo-integration` 模块

---

## 十五、测试依赖补充建议

需要在 `pom.xml` 中补充的 Testcontainers 依赖：

```xml
<!-- ==================== Testcontainers 依赖 ==================== -->

<!-- Testcontainers 核心模块 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- MySQL 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- PostgreSQL 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- Redis 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- MongoDB 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mongodb</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- Elasticsearch 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- RabbitMQ 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>rabbitmq</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- Kafka 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- Neo4j 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>neo4j</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>

<!-- InfluxDB 容器支持 -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>influxdb</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
```

---

## 十六、测试基类使用

### Core 模块基类

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseTest {

    @BeforeEach
    void setUp() {
        setupTest();
    }

    protected void setupTest() {
        // 子类覆盖
    }
}
```

### Web 模块基类

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseWebTest {

    @Autowired
    protected MockMvc mockMvc;

    protected void mockLogin(Long userId) {
        StpUtil.login(userId);
    }
}
```

---

## 十七、MyBatis-Plus 测试示例

### Mapper 层测试

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserMapperTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("MyBatis-Plus 插入测试")
    void testInsert() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@example.com");

        int result = userMapper.insert(user);
        assertThat(result).isEqualTo(1);
        assertThat(user.getId()).isNotNull();
    }

    @Test
    @DisplayName("MyBatis-Plus 条件查询测试")
    void testSelectByWrapper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "test");

        List<User> users = userMapper.selectList(wrapper);
        assertThat(users).isNotEmpty();
    }
}
```

### Service 层测试 (Mock Mapper)

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSaveUser() {
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User user = new User();
        user.setUsername("test");
        boolean result = userService.save(user);

        assertThat(result).isTrue();
        verify(userMapper, times(1)).insert(any(User.class));
    }
}
```

---

## 十八、测试执行命令

### 基本命令

```bash
# 运行所有单元测试
mvn test

# 运行所有集成测试
mvn verify

# 运行指定类的测试
mvn test -Dtest=UserServiceImplTest

# 运行指定方法的测试
mvn test -Dtest=UserServiceImplTest#findByUsername_Success

# 跳过测试打包
mvn clean package -DskipTests
```

### 覆盖率相关

```bash
# 运行测试并生成覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

### 并行测试

```bash
# 并行运行测试（已在 pom.xml 中配置）
mvn test -Dparallel=methods -DthreadCount=4
```

---

## 十九、测试最佳实践

### 1. AAA 模式

使用 Given-When-Then 结构组织测试代码：

```java
@Test
void testMethod() {
    // Given - 准备测试数据
    when(userMapper.findByUsername("test")).thenReturn(Optional.of(user));

    // When - 执行测试方法
    Optional<User> result = userService.findByUsername("test");

    // Then - 验证结果
    assertThat(result).isPresent();
}
```

### 2. 测试命名规范

- 使用 `@DisplayName` 注解提供清晰的测试名称
- 测试方法名应描述测试场景和预期结果
- 使用下划线分隔单词

```java
@Test
@DisplayName("根据用户名查询用户 - 成功")
void findByUsername_Success() { }

@Test
@DisplayName("根据用户名查询用户 - 用户不存在")
void findByUsername_NotFound() { }
```

### 3. 断言使用

优先使用 AssertJ 的流式断言：

```java
// 推荐
assertThat(result).isNotNull();
assertThat(result.getUsername()).isEqualTo("testuser");
assertThat(result.getStatus()).isGreaterThan(0);

// 避免
assertEquals("testuser", result.getUsername());
assertTrue(result.getStatus() > 0);
```

### 4. Mock 使用

- 只 Mock 外部依赖
- 不要 Mock 被测试的类
- 验证 Mock 对象的调用

```java
@Mock
private UserMapper userMapper;

@InjectMocks
private UserServiceImpl userService;

@Test
void testMethod() {
    when(userMapper.findByUsername("test")).thenReturn(Optional.of(user));

    userService.findByUsername("test");

    verify(userMapper, times(1)).findByUsername("test");
}
```

### 5. 测试隔离

- 每个测试方法应独立
- 使用 `@Transactional` 确保测试后回滚
- 在 `@BeforeEach` 中初始化测试数据

```java
@SpringBootTest
@Transactional
class UserServiceTest {
    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }
}
```

### 6. 参数化测试

使用 `@ParameterizedTest` 减少重复代码：

```java
@ParameterizedTest
@ValueSource(strings = {"admin", "user", "guest"})
void testWithDifferentUsernames(String username) {
    when(userMapper.findByUsername(username)).thenReturn(Optional.of(user));

    Optional<User> result = userService.findByUsername(username);

    assertThat(result).isPresent();
}
```

---

## 二十、测试覆盖率目标

| 指标 | 目标值 |
|------|--------|
| 代码行覆盖率 | 85% |
| 分支覆盖率 | 75% |

### JaCoCo 配置

已在 `pom.xml` 中配置：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.85</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.75</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

---

## 二十一、常见问题

### 1. 测试失败常见原因

1. **Mock 配置错误**：检查 Mock 对象的返回值设置
2. **数据库连接失败**：确认测试配置文件正确
3. **事务回滚失败**：检查 `@Transactional` 注解
4. **依赖注入失败**：确认 `@MockBean` 或 `@Autowired` 正确使用

### 2. 性能优化

1. 使用 `@DirtiesContext` 控制应用上下文生命周期
2. 合理使用并行测试
3. 避免在 `@BeforeAll` 中执行耗时操作

### 3. Testcontainers 注意事项

1. 确保本地已安装 Docker
2. 首次运行会下载镜像，可能较慢
3. 使用 `@Testcontainers` 共享容器实例
4. 合理设置容器资源限制

---

## 二十二、参考资料

- [JUnit 5 官方文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 官方文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ 官方文档](https://assertj.github.io/doc/)
- [Spring Boot 测试文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers 文档](https://www.testcontainers.org/)
- [JaCoCo 文档](https://www.jacoco.org/jacoco/trunk/doc/)
- [MyBatis-Plus 文档](https://baomidou.com/)
- [Sa-Token 文档](https://sa-token.cc/)