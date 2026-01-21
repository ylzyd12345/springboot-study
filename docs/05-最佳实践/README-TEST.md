# 测试框架使用指南

## 📋 测试框架概述

本项目已搭建完整的测试框架，支持单元测试、集成测试、Mock测试等多种测试类型。

## 🧪 测试基础设施

### 1. 测试基类

#### BaseTestContainer
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/BaseTestContainer.java`
- **用途**: 提供Testcontainers集成测试的基础设施
- **支持容器**: MySQL、Redis、RabbitMQ、Kafka、Elasticsearch

#### IntegrationTestBase
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/IntegrationTestBase.java`
- **用途**: 集成测试基类，继承BaseTestContainer
- **特性**: MockMvc支持、事务回滚

#### ServiceTestBase
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/ServiceTestBase.java`
- **用途**: Service层测试基类
- **特性**: 事务回滚、测试环境配置

#### WebTestBase
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/WebTestBase.java`
- **用途**: Web层测试基类
- **特性**: MockMvc配置、JSON转换工具

#### MockTestBase
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/MockTestBase.java`
- **用途**: Mock测试基类
- **特性**: Mockito初始化、Mock重置

### 2. 测试工具

#### TestDataFactory
- **位置**: `Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/TestDataFactory.java`
- **用途**: 测试数据生成工具
- **功能**: 生成测试用户、随机数据等

## 🔧 测试配置

### 1. 测试环境配置
- **文件**: `Junmo Platform-starter/src/test/resources/application-test.yml`
- **特性**: 
  - H2内存数据库
  - 测试专用Redis数据库
  - 禁用生产环境特性
  - 详细的日志配置

### 2. 测试数据
- **文件**: `Junmo Platform-web/src/test/resources/test-data.sql`
- **用途**: 集成测试的初始数据

## 📝 测试编写指南

### 1. 单元测试示例

```java
@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest extends BaseTestContainer {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("应该能够保存用户")
    void shouldSaveUser() {
        // Given
        User user = TestDataFactory.createTestUser();
        
        // When
        User savedUser = userRepository.save(user);
        
        // Then
        assertThat(savedUser.getId()).isNotNull();
    }
}
```

### 2. 集成测试示例

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest extends IntegrationTestBase {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("应该能够创建用户")
    void shouldCreateUser() throws Exception {
        // Given
        User user = TestDataFactory.createTestUser();
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated());
    }
}
```

### 3. Mock测试示例

```java
class UserServiceTest extends MockTestBase {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("应该能够查找用户")
    void shouldFindUser() {
        // Given
        User user = TestDataFactory.createTestUser();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        
        // When
        Optional<User> result = userService.findById("1");
        
        // Then
        assertThat(result).isPresent();
    }
}
```

## 🚀 运行测试

### 1. 运行所有测试
```bash
mvn test
```

### 2. 运行特定模块测试
```bash
mvn test -pl Junmo Platform-core
```

### 3. 运行集成测试
```bash
mvn verify
```

### 4. 运行特定测试类
```bash
mvn test -Dtest=UserControllerIntegrationTest
```

### 5. 跳过测试
```bash
mvn install -DskipTests
```

## 📊 测试覆盖率

项目已集成JaCoCo代码覆盖率工具：

### 1. 生成覆盖率报告
```bash
mvn jacoco:report
```

### 2. 查看报告
- **HTML报告**: `target/site/jacoco/index.html`
- **XML报告**: `target/site/jacoco/jacoco.xml`

## 🐛 调试测试

### 1. 启用调试日志
在`application-test.yml`中设置：
```yaml
logging:
  level:
    com.junmo.Junmo Platform: debug
    org.springframework.web: debug
```

### 2. 运行单个测试方法
```bash
mvn test -Dtest=UserControllerTest#shouldCreateUser
```

## 📋 测试最佳实践

1. **命名规范**: 测试方法名应该描述测试意图
2. **AAA模式**: Arrange-Act-Assert结构
3. **独立性**: 测试之间不应该相互依赖
4. **可重复**: 测试结果应该是一致的
5. **快速**: 单元测试应该快速执行
6. **清晰**: 测试代码应该易于理解

## 🔍 测试类型

| 类型 | 用途 | 基类 | 示例 |
|------|------|------|------|
| 单元测试 | 测试单个组件 | MockTestBase | Service层测试 |
| 集成测试 | 测试组件间交互 | IntegrationTestBase | Controller测试 |
| 端到端测试 | 测试完整流程 | BaseTestContainer | API集成测试 |
| 性能测试 | 测试性能指标 | 自定义基类 | 压力测试 |

## 🛠️ 扩展测试框架

### 1. 添加新的测试基类
在`Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/`目录下创建新的基类。

### 2. 添加测试工具
在`Junmo Platform-common/src/test/java/com/kev1n/Junmo Platform/common/test/`目录下添加工具类。

### 3. 配置新的Testcontainer
在`BaseTestContainer`中添加新的容器配置。