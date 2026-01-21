# Junmo Platform 测试代码分析与整体方案

## 一、当前测试现状分析

### 1.1 测试代码分布

| 模块 | 测试类数量 | 测试类型 | 状态 |
|------|-----------|---------|------|
| Junmo Platform-common | 14个 | 配置类测试 | ✅ 已实现 |
| Junmo Platform-core | 5个 | 服务层测试 | ⚠️ 部分实现 |
| Junmo Platform-web | 17个 | 控制器测试 | ⚠️ 部分实现 |
| 其他模块 | 0个 | - | ❌ 未实现 |

**总计**: 36个测试类（目标：70+个）

### 1.2 主要问题清单

#### 🔴 严重问题（P0）

1. **Testcontainers 配置不完整**
   - 仅配置了 MinIO 容器
   - 缺少 MySQL、Redis、RabbitMQ、Kafka 等容器配置
   - 导致集成测试依赖外部服务，测试不稳定

2. **测试配置文件不一致**
   - 3个模块各自维护独立的 `application-test.yml`
   - 配置重复且冲突（H2 vs MySQL）
   - 依赖外部服务而非嵌入式解决方案

3. **SQL 初始化脚本混乱**
   - 表命名不一致：`sys_user` vs `user`
   - 测试数据分散在3个文件中
   - 存在重复的表结构定义

#### 🟡 重要问题（P1）

4. **测试覆盖率不均衡**
   - 配置类测试占比过高（14/36 = 39%）
   - Service 层测试不足（5/36 = 14%）
   - 缺少 Mapper 层测试

5. **集成测试与单元测试混放**
   - `UserControllerIT.java` 放在 controller 包中
   - `integration` 目录为空
   - 缺少明确的测试分层

6. **缺少测试工具类**
   - 无测试数据构建器
   - 无测试断言工具类
   - 无测试数据清理工具

#### 🟢 次要问题（P2）

7. **模块职责不清**
   - BaseTest 在 core 模块，但被 web 模块依赖
   - TestcontainersConfig 在 web 模块，但被所有模块使用
   - 缺少统一的测试基础设施模块

8. **测试命名不统一**
   - 部分测试类缺少 `@DisplayName` 注解
   - 测试方法命名不一致

---

## 二、整体测试方案设计

### 2.1 测试分层架构

```
┌─────────────────────────────────────────────────────────┐
│                   E2E 测试（端到端）                      │
│            使用真实环境 + Testcontainers 全栈容器         │
└─────────────────────────────────────────────────────────┘
                            ↑
┌─────────────────────────────────────────────────────────┐
│                   集成测试（Integration）                  │
│         @SpringBootTest + Testcontainers 选择性容器       │
│   - Controller IT（完整 HTTP 请求 + 数据库 + Redis）       │
│   - Service IT（数据库 + Redis + 消息队列）                │
│   - Mapper IT（真实数据库操作）                           │
└─────────────────────────────────────────────────────────┘
                            ↑
┌─────────────────────────────────────────────────────────┐
│                   单元测试（Unit）                        │
│              Mocktio + 纯内存测试                        │
│   - Controller Test（MockMvc + Mock Service）             │
│   - Service Test（Mock Mapper + Mock 外部服务）            │
│   - Mapper Test（H2 内存数据库）                          │
│   - Config Test（@ConfigurationPropertiesTest）           │
│   - Utils Test（纯函数测试）                              │
└─────────────────────────────────────────────────────────┘
```

### 2.2 测试策略矩阵

| 测试类型 | 测试范围 | 依赖隔离 | 执行速度 | 覆盖率目标 | 使用场景 |
|---------|---------|---------|---------|-----------|---------|
| **单元测试** | 单个类/方法 | 完全隔离 | 快（秒级） | 85%+ | 日常开发、CI 快速检查 |
| **集成测试** | 多个组件协作 | Testcontainers | 中（分钟级） | 75%+ | 关键业务流程验证 |
| **E2E 测试** | 完整系统 | 真实环境 | 慢（10分钟+） | 60%+ | 预发布验证、回归测试 |

### 2.3 测试金字塔

```
        ▲
       / \      E2E 测试（5%）
      /   \     - 完整业务流程
     /-----\    - 跨系统集成
    / 集成 \    集成测试（25%）
   /  测试  \   - Service 层
  /---------\  - Controller 层
 /  单元测试  \ 单元测试（70%）
/_____________\ - Service 层
               - Utils 层
               - Config 层
```

---

## 三、单元测试方案

### 3.1 单元测试原则

1. **隔离性**: 所有外部依赖必须 Mock
2. **快速性**: 测试执行时间 < 1秒/类
3. **可重复性**: 不依赖外部环境
4. **独立性**: 测试之间无依赖关系

### 3.2 单元测试分层

#### 3.2.1 Controller 层单元测试

**测试框架**: `@WebMvcTest` + `MockMvc`

**测试内容**:
- HTTP 请求/响应序列化/反序列化
- 参数校验（`@Valid`、`@Validated`）
- 异常处理
- 路由规则

**示例**:
```java
@WebMvcTest(UserController.class)
@Import({SaTokenSecurityConfig.class, WebMvcConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("获取用户列表 - 成功")
    void listUsers_Success() {
        // Given
        when(userService.listUsers(any())).thenReturn(buildUserPage());

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(10));
    }
}
```

#### 3.2.2 Service 层单元测试

**测试框架**: `@ExtendWith(MockitoExtension.class)`

**测试内容**:
- 业务逻辑
- 事务管理
- 异常处理
- 调用外部服务的参数和返回值

**示例**:
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("创建用户 - 成功")
    void createUser_Success() {
        // Given
        CreateUserRequest request = buildCreateUserRequest();
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");

        // When
        Long userId = userService.createUser(request);

        // Then
        assertThat(userId).isNotNull();
        verify(userMapper).insert(any(User.class));
    }
}
```

#### 3.2.3 Mapper 层单元测试

**测试框架**: `@SpringBootTest` + H2 内存数据库

**测试内容**:
- CRUD 操作
- 条件查询
- 分页查询
- 逻辑删除
- 乐观锁

**示例**:
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1"
})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("插入用户 - 成功")
    void insert_Success() {
        // Given
        User user = buildUser();

        // When
        int result = userMapper.insert(user);

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(user.getId()).isNotNull();
    }
}
```

#### 3.2.4 配置类单元测试

**测试框架**: `@ConfigurationPropertiesTest`

**测试内容**:
- 配置绑定
- 默认值
- 配置验证

**示例**:
```java
@ConfigurationPropertiesTest
class RustFSPropertiesTest {

    @Autowired
    private RustFSProperties properties;

    @Test
    @DisplayName("配置绑定 - 成功")
    void bind_Success() {
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:9000");
        assertThat(properties.getAccessKey()).isEqualTo("admin");
    }
}
```

### 3.3 单元测试命名规范

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| 测试类 | `{ClassName}Test.java` | `UserServiceImplTest.java` |
| 测试方法 | `{methodName}_{scenario}_{expectedResult}` | `createUser_Success()` |
| 测试包 | 与源代码包结构一致 | `com.junmo.Junmo Platform.core.service.impl` |

---

## 四、集成测试方案

### 4.1 集成测试原则

1. **真实性**: 使用真实数据库和中间件（Testcontainers）
2. **选择性**: 只启动必要的组件（`@SpringBootTest` 限制扫描范围）
3. **隔离性**: 每个测试类独立容器，测试间数据隔离
4. **可重复性**: 使用 `@Transactional` 自动回滚

### 4.2 Testcontainers 配置方案

#### 4.2.1 创建统一 Testcontainers 配置

**位置**: `Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/config/TestcontainersConfig.java`

```java
public class TestcontainersConfig {

    // MySQL 容器
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Redis 容器
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    // MinIO 容器
    @Container
    static MinIOContainer minio = new MinIOContainer("minio/minio:latest")
            .withUserName("admin")
            .withPassword("admin123");

    // RabbitMQ 容器
    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.12")
            .withAdminPassword("admin");

    // Kafka 容器
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL 配置
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // Redis 配置
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        // MinIO 配置
        registry.add("rustfs.endpoint", minio::getS3URL);
        registry.add("rustfs.access-key", minio::getUserName);
        registry.add("rustfs.secret-key", minio::getPassword);

        // RabbitMQ 配置
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);

        // Kafka 配置
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

#### 4.2.2 按需启用容器

**方案 A**: 全局启用（所有集成测试共享）
```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestcontainersConfig.configureProperties(registry);
    }
}
```

**方案 B**: 按需启用（每个测试类独立）
```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserServiceIT {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
    }
}
```

**推荐**: 方案 A（全局启用），减少容器启动时间

### 4.3 集成测试分层

#### 4.3.1 Controller 集成测试

**测试框架**: `@SpringBootTest` + `@AutoConfigureMockMvc` + Testcontainers

**测试内容**:
- 完整 HTTP 请求流程
- 数据库操作
- Redis 缓存
- 消息队列（如需要）
- 权限认证

**示例**:
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("创建用户 - 完整流程")
    void createUser_FullFlow() {
        // Given
        CreateUserRequest request = buildCreateUserRequest();

        // When
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        // Then
        ApiResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            ApiResponse.class
        );
        assertThat(response.getData()).isNotNull();

        // 验证数据库
        User user = userMapper.selectById((Long) response.getData());
        assertThat(user).isNotNull();
    }
}
```

#### 4.3.2 Service 集成测试

**测试框架**: `@SpringBootTest` + Testcontainers

**测试内容**:
- 完整业务流程
- 数据库操作
- Redis 缓存
- 消息队列
- 分布式锁

**示例**:
```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserServiceIT extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("创建用户 - 完整流程")
    void createUser_FullFlow() {
        // Given
        CreateUserRequest request = buildCreateUserRequest();

        // When
        Long userId = userService.createUser(request);

        // Then
        assertThat(userId).isNotNull();

        // 验证数据库
        User user = userMapper.selectById(userId);
        assertThat(user.getUsername()).isEqualTo(request.getUsername());

        // 验证 Redis 缓存
        User cachedUser = redisTemplate.opsForValue().get("user:" + userId);
        assertThat(cachedUser).isNotNull();
    }
}
```

#### 4.3.3 Mapper 集成测试

**测试框架**: `@SpringBootTest` + Testcontainers MySQL

**测试内容**:
- 真实数据库操作
- 事务管理
- 并发场景（乐观锁）
- 性能测试

**示例**:
```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserMapperIT extends BaseIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("乐观锁 - 并发更新")
    void optimisticLock_ConcurrentUpdate() {
        // Given
        User user = buildUser();
        userMapper.insert(user);

        // When - 模拟并发更新
        User user1 = userMapper.selectById(user.getId());
        User user2 = userMapper.selectById(user.getId());

        user1.setEmail("user1@example.com");
        user2.setEmail("user2@example.com");

        // Then - 第一个更新成功，第二个失败
        int result1 = userMapper.updateById(user1);
        int result2 = userMapper.updateById(user2);

        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(0);
    }
}
```

### 4.4 集成测试命名规范

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| 测试类 | `{ClassName}IT.java` | `UserServiceIT.java` |
| 测试方法 | `{methodName}_{scenario}_{expectedResult}` | `createUser_FullFlow()` |
| 测试包 | `src/test/java/.../integration/` | `com.junmo.Junmo Platform.core.service.integration` |

---

## 五、测试基础设施重构方案

### 5.1 统一测试配置文件

**目标**: 消除配置重复，统一测试环境

**方案**: 在 `Junmo Platform-web` 模块维护唯一的 `application-test.yml`

**内容**:
```yaml
spring:
  profiles:
    active: test

  # 数据源配置（Testcontainers 动态配置）
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${spring.datasource.url:jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1}
    username: ${spring.datasource.username:sa}
    password: ${spring.datasource.password:}

  # Redis 配置（Testcontainers 动态配置）
  data:
    redis:
      host: ${spring.data.redis.host:localhost}
      port: ${spring.data.redis.port:6379}

  # RabbitMQ 配置（Testcontainers 动态配置）
  rabbitmq:
    host: ${spring.rabbitmq.host:localhost}
    port: ${spring.rabbitmq.port:5672}
    username: ${spring.rabbitmq.username:guest}
    password: ${spring.rabbitmq.password:guest}

  # Kafka 配置（Testcontainers 动态配置）
  kafka:
    bootstrap-servers: ${spring.kafka.bootstrap-servers:localhost:9092}

  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false

  # SQL 初始化
  sql:
    init:
      mode: always
      schema-locations: classpath:sql/schema.sql
      data-locations: classpath:sql/data.sql

# 日志配置
logging:
  level:
    com.junmo.Junmo Platform: DEBUG
    org.springframework.test: INFO
```

### 5.2 统一 SQL 初始化脚本

**目标**: 统一表结构，消除重复

**方案**:
1. 删除 `Junmo Platform-core/src/test/resources/db/schema.sql`
2. 保留 `Junmo Platform-web/src/test/resources/sql/schema.sql`
3. 统一表命名为 `sys_user`

**内容**:
```sql
-- Junmo Platform-web/src/test/resources/sql/schema.sql
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

**测试数据**:
```sql
-- Junmo Platform-web/src/test/resources/sql/data.sql
INSERT INTO `sys_user` (`username`, `password`, `email`, `phone`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '13800138000', 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'user1@example.com', '13800138001', 1),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'user2@example.com', '13800138002', 1);
```

### 5.3 统一测试基类

**目标**: 减少重复代码，提供统一测试工具

**方案**:
1. 在 `Junmo Platform-web` 模块创建 `BaseTest` 和 `BaseWebTest`
2. 在 `Junmo Platform-core` 模块删除 `BaseTest`

**BaseTest**:
```java
// Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/BaseTest.java
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseTest {

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    protected void mockLogin(Long userId) {
        StpUtil.login(userId);
    }

    protected void mockAdminLogin() {
        mockLogin(1L);
    }

    protected void mockUserLogin() {
        mockLogin(2L);
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**BaseWebTest**:
```java
// Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/BaseWebTest.java
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseWebTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String performGet(String url) throws Exception {
        return mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    protected String performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    protected String performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    protected String performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }
}
```

### 5.4 创建测试工具类

**目标**: 提供测试数据构建和断言工具

**TestDataBuilder**:
```java
// Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/util/TestDataBuilder.java
public class TestDataBuilder {

    public static User buildUser() {
        return User.builder()
            .username("test_user")
            .password("encoded_password")
            .email("test@example.com")
            .phone("13800138000")
            .status(1)
            .build();
    }

    public static CreateUserRequest buildCreateUserRequest() {
        return CreateUserRequest.builder()
            .username("test_user")
            .password("password123")
            .email("test@example.com")
            .phone("13800138000")
            .build();
    }

    public static UpdateUserRequest buildUpdateUserRequest() {
        return UpdateUserRequest.builder()
            .email("updated@example.com")
            .phone("13900139000")
            .build();
    }
}
```

**TestAssertions**:
```java
// Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/util/TestAssertions.java
public class TestAssertions {

    public static void assertUserEquals(User expected, User actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhone()).isEqualTo(expected.getPhone());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
    }

    public static void assertApiResponse(ApiResponse response, Integer code, String message) {
        assertThat(response.getCode()).isEqualTo(code);
        assertThat(response.getMessage()).isEqualTo(message);
    }
}
```

---

## 六、测试执行策略

### 6.1 Maven Profile 配置

**目标**: 支持不同测试场景的快速切换

**配置**: 在 `Junmo Platform/pom.xml` 中添加 Profile

```xml
<profiles>
    <!-- 单元测试（默认） -->
    <profile>
        <id>unit-test</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <test.type>unit</test.type>
        </properties>
    </profile>

    <!-- 集成测试 -->
    <profile>
        <id>integration-test</id>
        <properties>
            <test.type>integration</test.type>
        </properties>
    </profile>

    <!-- 完整测试 -->
    <profile>
        <id>full-test</id>
        <properties>
            <test.type>full</test.type>
        </properties>
    </profile>
</profiles>
```

### 6.2 测试分类执行

**单元测试**:
```bash
mvn test -Punit-test -Dtest="*Test"
```

**集成测试**:
```bash
mvn test -Pintegration-test -Dtest="*IT"
```

**完整测试**:
```bash
mvn test -Pfull-test
```

### 6.3 CI/CD 集成

**GitHub Actions 示例**:
```yaml
name: Test

on: [push, pull_request]

jobs:
  unit-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 25
        uses: actions/setup-java@v3
        with:
          java-version: '25'
          distribution: 'temurin'
      - name: Run unit tests
        run: mvn test -Punit-test

  integration-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 25
        uses: actions/setup-java@v3
        with:
          java-version: '25'
          distribution: 'temurin'
      - name: Run integration tests
        run: mvn test -Pintegration-test
```

---

## 七、测试覆盖率目标

### 7.1 覆盖率指标

| 指标 | 目标值 | 说明 |
|------|-------|------|
| 代码行覆盖率 | 85% | 所有代码行的执行覆盖率 |
| 分支覆盖率 | 75% | 所有条件分支的执行覆盖率 |
| 方法覆盖率 | 90% | 所有方法的执行覆盖率 |
| 类覆盖率 | 95% | 所有类的执行覆盖率 |

### 7.2 覆盖率检查

**配置**: 在 `Junmo Platform/pom.xml` 中配置 JaCoCo

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
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
        </execution>
    </executions>
</plugin>
```

---

## 八、实施建议与决策点

### 8.1 优先级排序

| 优先级 | 任务 | 预计工作量 | 影响 |
|-------|------|-----------|------|
| **P0** | 完善 Testcontainers 配置 | 2小时 | 🔴 严重 |
| **P0** | 统一测试配置文件 | 1小时 | 🔴 严重 |
| **P0** | 统一 SQL 初始化脚本 | 1小时 | 🔴 严重 |
| **P1** | 重构测试基类 | 2小时 | 🟡 重要 |
| **P1** | 创建测试工具类 | 2小时 | 🟡 重要 |
| **P1** | 补充 Service 层单元测试 | 8小时 | 🟡 重要 |
| **P1** | 补充 Mapper 层测试 | 6小时 | 🟡 重要 |
| **P2** | 补充 Controller 集成测试 | 10小时 | 🟢 次要 |
| **P2** | 配置 Maven Profile | 1小时 | 🟢 次要 |
| **P2** | 配置 JaCoCo 覆盖率检查 | 1小时 | 🟢 次要 |

### 8.2 实施方案选择

#### 方案 A：渐进式重构（推荐）

**优点**:
- 风险低，可逐步验证
- 不影响现有功能
- 可以边开发边重构

**步骤**:
1. 第一阶段：修复 P0 问题（Testcontainers + 配置统一）
2. 第二阶段：补充核心测试（Service + Mapper）
3. 第三阶段：完善集成测试（Controller IT）
4. 第四阶段：优化测试工具（工具类 + 覆盖率）

**预计时间**: 2-3周

#### 方案 B：一次性重构

**优点**:
- 一步到位，测试架构统一
- 避免重复工作

**缺点**:
- 风险高，影响面大
- 需要暂停新功能开发
- 测试失败排查困难

**预计时间**: 1周

### 8.3 需要决策的问题

**问题 1**: Testcontainers 容器启动方式
- **选项 A**: 全局启用（所有集成测试共享容器）
  - 优点：启动快，资源占用少
  - 缺点：测试间可能相互影响
- **选项 B**: 按需启用（每个测试类独立容器）
  - 优点：测试隔离性好
  - 缺点：启动慢，资源占用多

**问题 2**: 集成测试存放位置
- **选项 A**: 放在 `integration` 包中
  - 优点：结构清晰，易于管理
  - 缺点：需要移动现有测试类
- **选项 B**: 放在原有包中，通过命名区分
  - 优点：无需移动文件
  - 缺点：结构不清晰

**问题 3**: 测试数据清理策略
- **选项 A**: 使用 `@Transactional` 自动回滚
  - 优点：简单，无需手动清理
  - 缺点：无法测试事务提交
- **选项 B**: 手动清理测试数据
  - 优点：灵活，可测试事务提交
  - 缺点：需要编写清理代码

**问题 4**: 是否创建独立的测试基础设施模块
- **选项 A**: 创建 `Junmo Platform-test` 模块
  - 优点：职责清晰，可复用
  - 缺点：增加模块复杂度
- **选项 B**: 继续放在 `Junmo Platform-web` 模块
  - 优点：简单，无需额外模块
  - 缺点：职责不清晰

---

## 九、总结与建议

### 9.1 核心问题总结

1. **Testcontainers 配置不完整** - 导致集成测试依赖外部服务
2. **测试配置文件不一致** - 导致测试环境混乱
3. **SQL 初始化脚本混乱** - 导致测试数据不一致
4. **测试覆盖率不均衡** - 配置类测试过多，业务测试不足
5. **集成测试与单元测试混放** - 导致测试结构不清晰

### 9.2 推荐实施方案

**采用方案 A（渐进式重构）**，按以下顺序执行：

**第一阶段（P0 - 必须立即执行）**:
1. 完善 Testcontainers 配置（MySQL、Redis、RabbitMQ、Kafka）
2. 统一测试配置文件（删除重复配置，使用 Testcontainers 动态配置）
3. 统一 SQL 初始化脚本（统一表命名，合并测试数据）

**第二阶段（P1 - 1-2周内完成）**:
4. 重构测试基类（统一 BaseTest 和 BaseWebTest）
5. 创建测试工具类（TestDataBuilder、TestAssertions）
6. 补充 Service 层单元测试（目标：85% 覆盖率）
7. 补充 Mapper 层测试（单元测试 + 集成测试）

**第三阶段（P2 - 2-3周内完成）**:
8. 补充 Controller 集成测试（目标：75% 覆盖率）
9. 配置 Maven Profile（支持单元测试、集成测试、完整测试）
10. 配置 JaCoCo 覆盖率检查（目标：85% 行覆盖率，75% 分支覆盖率）

### 9.3 预期效果

完成重构后，项目将具备：
- ✅ 完整的 Testcontainers 配置，集成测试不依赖外部服务
- ✅ 统一的测试配置文件，测试环境一致
- ✅ 清晰的测试分层（单元测试 + 集成测试）
- ✅ 完整的测试覆盖（目标：70+ 测试类）
- ✅ 快速的测试执行（单元测试 < 5分钟，集成测试 < 15分钟）
- ✅ 可靠的 CI/CD 集成（自动化测试 + 覆盖率检查）

---

## 附录：测试代码结构探索报告

### 1. 测试类分布统计

| 模块 | 测试类数量 | 测试类型 | 主要测试内容 |
|------|-----------|---------|-------------|
| **Junmo Platform-common** | 14个 | 配置类测试 | Quartz、Redisson、RustFS、Seata、动态数据源等配置 |
| **Junmo Platform-core** | 5个 | 服务层测试 | UserService、FileStorageService 的单元测试和集成测试 |
| **Junmo Platform-web** | 17个 | 控制器测试 | Auth、User、FileStorage、DocumentPreview 等控制器 |
| **Junmo Platform-starter** | 0个 | - | 仅有测试配置文件，无测试类 |
| **Junmo Platform-admin** | 0个 | - | 无测试类 |
| **Junmo Platform-api** | 0个 | - | 无测试类 |
| **Junmo Platform-generator** | 0个 | - | 无测试类 |
| **Junmo Platform-integration** | 0个 | - | 无测试类 |

**总计**: 36个测试类

### 2. 测试类命名规范分析

**发现的命名规范**:
- **单元测试**: `{ClassName}Test.java` (例如: `UserServiceImplTest.java`)
- **集成测试**: `{ClassName}IT.java` (例如: `UserServiceIT.java`, `AuthControllerIT.java`)
- **配置测试**: `{ConfigName}Test.java` (例如: `QuartzConfigTest.java`)

**命名一致性**: ✅ 良好，遵循了标准的测试命名规范

### 3. 包结构分析

```
com.junmo.Junmo Platform
├── common
│   └── src/test/java/com/kev1n/Junmo Platform/common
│       └── config/                    # 配置类测试
│           ├── CustomSeataPropertiesTest.java
│           ├── DynamicDataSourceConfigTest.java
│           ├── DynamicDataSourcePropertiesTest.java
│           ├── KKFileViewPropertiesTest.java
│           ├── PasswordSecurityConfigTest.java
│           ├── QuartzConfigTest.java
│           ├── QuartzPropertiesTest.java
│           ├── RedissonConfigTest.java
│           ├── RedissonPropertiesTest.java
│           ├── RustFSPropertiesTest.java
│           ├── S3ClientConfigTest.java
│           ├── ScheduledTaskConfigTest.java
│           └── SeataConfigTest.java
│
├── core
│   └── src/test/java/com/kev1n/Junmo Platform/core
│       ├── BaseTest.java              # 核心层测试基类
│       ├── TestApplication.java       # 测试应用配置
│       ├── config/
│       │   └── MybatisPlusConfigTest.java
│       └── service/
│           ├── UserServiceIT.java     # 用户服务集成测试
│           └── impl/
│               ├── UserServiceImplTest.java      # 用户服务单元测试
│               └── FileStorageServiceImplTest.java  # 文件存储服务单元测试
│
└── web
    └── src/test/java/com/kev1n/Junmo Platform/web
        ├── BaseWebTest.java           # Web层测试基类
        ├── TestApplication.java       # 测试应用配置
        ├── config/
        │   ├── SaTokenSecurityConfigTest.java
        │   ├── SwaggerConfigTest.java
        │   ├── WebMvcConfigTest.java
        │   ├── WebMvcTestConfig.java
        │   └── TestcontainersConfig.java  # Testcontainers配置
        ├── controller/
        │   ├── ApiVersionControllerTest.java
        │   ├── AuthControllerTest.java           # 认证控制器单元测试
        │   ├── AuthControllerIT.java             # 认证控制器集成测试
        │   ├── DocumentPreviewControllerTest.java
        │   ├── FileStorageControllerTest.java
        │   ├── FileStorageControllerIT.java       # 文件存储控制器集成测试
        │   ├── JobScheduleControllerTest.java
        │   ├── RedissonAndDataSourceControllerTest.java
        │   ├── SeataControllerTest.java
        │   └── UserControllerIT.java              # 用户控制器集成测试
        └── integration/              # 集成测试目录（空）
```

### 4. 测试基类详细分析

**BaseTest** (`Junmo Platform-core/src/test/java/com/kev1n/Junmo Platform/core/BaseTest.java`)

```java
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseTest {
    // 提供通用测试配置和生命周期管理
    // 包含 mockLogin()、mockAdminLogin()、mockUserLogin() 等工具方法
}
```

**功能特点**:
- ✅ 使用 `@Transactional` 自动回滚事务
- ✅ 提供 Sa-Token 登录模拟方法
- ✅ 提供 `setupTest()` 和 `cleanupTest()` 钩子方法
- ✅ 提供 `sleep()` 方法用于异步测试

**BaseWebTest** (`Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/BaseWebTest.java`)

```java
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseWebTest {
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestcontainersConfig.configureProperties(registry);
    }

    @Autowired
    protected MockMvc mockMvc;

    // 提供 HTTP 请求工具方法：performGet、performPost、performPut、performDelete 等
}
```

**功能特点**:
- ✅ 集成 MockMvc 进行 Web 层测试
- ✅ 提供 REST API 请求工具方法
- ✅ 支持 Sa-Token 认证
- ✅ 集成 Testcontainers 动态配置

### 5. Testcontainers 配置

**TestcontainersConfig** (`Junmo Platform-web/src/test/java/com/kev1n/Junmo Platform/web/config/TestcontainersConfig.java`)

```java
public class TestcontainersConfig {
    @Container
    static MinIOContainer minio = new MinIOContainer(
            DockerImageName.parse("minio/minio:latest")
    )
            .withUserName("admin")
            .withPassword("admin123")
            .withCommand("server /data");

    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("rustfs.endpoint", minio::getS3URL);
        registry.add("rustfs.access-key", minio::getUserName);
        registry.add("rustfs.secret-key", minio::getPassword);
    }
}
```

**配置特点**:
- ✅ 使用 MinIO 容器提供 S3 兼容的文件存储服务
- ✅ 动态配置 RustFS 属性
- ⚠️ **仅配置了 MinIO，缺少 MySQL、Redis 等其他容器的配置**

### 6. 测试配置文件分析

**Junmo Platform-starter/application-test.yml** (最完整的配置)

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672
  kafka:
    bootstrap-servers: localhost:9092
  cache:
    type: caffeine
```

**配置特点**:
- ✅ 使用 H2 内存数据库
- ✅ 配置了 Redis、RabbitMQ、Kafka 等中间件
- ⚠️ **依赖外部服务（Redis、RabbitMQ、Kafka），未使用嵌入式解决方案**
- ⚠️ **与 Testcontainers 配置不一致**

**Junmo Platform-web/application-test.yml**

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  data:
    redis:
      host: localhost
      port: 6379
  h2:
    console:
      enabled: true
```

**配置特点**:
- ✅ 使用 H2 内存数据库
- ✅ 启用 H2 控制台
- ⚠️ **依赖外部 Redis 服务**

**Junmo Platform-core/application-test.yml**

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE
  sql:
    init:
      schema-locations: classpath:db/schema.sql
  data:
    redis:
      host: localhost
      port: 6379
```

**配置特点**:
- ✅ 自动加载 schema.sql 初始化脚本
- ⚠️ **依赖外部 Redis 服务**

### 7. 测试资源文件

**SQL 初始化脚本**:

1. **Junmo Platform-core/src/test/resources/db/schema.sql**
   - 创建 `sys_user` 表
   - 包含索引创建语句

2. **Junmo Platform-web/src/test/resources/sql/init-test.sql**
   - 创建 `user` 表
   - 插入测试数据（3条用户记录）

3. **Junmo Platform-web/src/test/resources/test-data.sql**
   - 插入测试用户数据（4条用户记录）
   - 包含部门数据的注释（未执行）

**问题**: ⚠️ **存在重复的表结构定义（`sys_user` 和 `user`），命名不一致**

---

*文档创建时间: 2026-01-06*
*最后更新: 2026-01-06*