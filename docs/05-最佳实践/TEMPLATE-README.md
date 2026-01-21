# Junmo Platform 框架模板

## 📋 模板概述

这是一个基于 Spring Boot 3.2.10 + Sa-Token + MyBatis-Plus 的企业级开发框架模板，提供了完整的认证授权、数据访问、API管理等基础功能。

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 6.0+

### 项目结构
```
Junmo Platform/
├── Junmo Platform-common/         # 公共模块
├── Junmo Platform-core/           # 核心业务模块
├── Junmo Platform-web/            # Web应用模块
├── Junmo Platform-admin/          # 管理后台模块
├── Junmo Platform-api/            # API接口模块
├── Junmo Platform-integration/    # 集成测试模块
├── Junmo Platform-generator/      # 代码生成器模块
└── Junmo Platform-starter/        # 启动模块
```

### 启动步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd Junmo Platform
```

2. **配置数据库**
```yaml
# Junmo Platform-starter/src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Junmo Platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. **启动应用**
```bash
cd Junmo Platform-starter
mvn spring-boot:run
```

4. **访问应用**
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/actuator/health

## 🛠️ 技术栈

### 核心框架
- **Spring Boot 3.2.10** - 应用框架
- **Java 21** - 编程语言
- **Maven 3.9** - 构建工具

### 安全认证
- **Sa-Token 1.37.0** - 轻量级权限认证框架
- **Sa-Token Redis** - Redis持久化支持

### 数据访问
- **MyBatis-Plus 3.5.7** - 数据访问层增强框架
- **Druid 1.2.20** - 高性能数据库连接池
- **MySQL 9.5.0** - 关系型数据库

### 缓存和消息
- **Redis 7.0** - 缓存和会话存储
- **RabbitMQ 3.17.0** - 消息队列
- **Caffeine 3.1.6** - 本地缓存

### 监控和文档
- **Spring Boot Actuator** - 应用监控
- **Micrometer** - 指标收集
- **SpringDoc OpenAPI** - API文档

## 📝 开发规范

### 代码规范
- 使用 Lombok 减少样板代码
- 遵循 RESTful API 设计原则
- 统一使用 ApiResponse 作为返回格式
- 完善的异常处理和日志记录

### 测试规范
- 单元测试覆盖率 > 80%
- 使用 Mockito 进行 Mock 测试
- 集成测试使用 Testcontainers
- API 测试使用 MockMvc

### 数据库规范
- 使用 MyBatis-Plus 简化 CRUD 操作
- 统一的实体类设计（包含通用字段）
- 逻辑删除使用 @TableLogic
- 乐观锁使用 @Version

## 🔧 自定义配置

### 修改包名
1. 全局搜索替换 `com.junmo.Junmo Platform` 为你的包名
2. 修改 `Junmo Platform-starter/src/main/resources/application.yml` 中的应用名称

### 添加新功能模块
1. 在相应模块下创建包结构
2. 实现对应的 Service、Controller 等
3. 编写单元测试和集成测试
4. 更新 API 文档

### 数据库配置
```yaml
# 多数据源配置
spring:
  datasource:
    master:
      url: jdbc:mysql://localhost:3306/master
      username: root
      password: password
    slave:
      url: jdbc:mysql://localhost:3306/slave
      username: root
      password: password
```

### Redis 配置
```yaml
# Redis 集群配置
spring:
  data:
    redis:
      cluster:
        nodes: 
          - 192.168.1.100:7000
          - 192.168.1.101:7000
          - 192.168.1.102:7000
```

## 🔐 安全配置

### Sa-Token 配置
```yaml
sa-token:
  token-name: Authorization
  timeout: 2592000  # 30天
  active-timeout: -1
  token-style: uuid
  is-read-header: true
  token-prefix: Bearer
```

### 权限注解使用
```java
// 检查登录状态
@SaCheckLogin
public ResponseEntity<?> getUserInfo() {
    // 业务逻辑
}

// 检查角色
@SaCheckRole("ADMIN")
public ResponseEntity<?> deleteUser() {
    // 业务逻辑
}

// 检查权限
@SaCheckPermission("user:update")
public ResponseEntity<?> updateUser() {
    // 业务逻辑
}
```

## 📊 监控配置

### Actuator 端点
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

### 自定义指标
```java
@Component
public class CustomMetrics {
    private final MeterRegistry meterRegistry;
    
    public void recordApiRequest(String endpoint, String method) {
        Counter.builder("api.request.count")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .increment();
    }
}
```

## 🧪 测试指南

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn test -Pintegration-test
```

### 生成测试报告
```bash
mvn jacoco:report
```

## 📚 API 文档

启动应用后访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🚀 部署指南

### Docker 部署
```bash
# 构建镜像
docker build -t Junmo Platform:latest .

# 运行容器
docker run -p 8080:8080 Junmo Platform:latest
```

### 生产环境配置
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/Junmo Platform
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

logging:
  level:
    com.junmo.Junmo Platform: INFO
    org.springframework.security: WARN
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🆘 常见问题

### Q: 如何添加新的数据源？
A: 在 `application.yml` 中配置多数据源，使用 `@DS` 注解切换数据源。

### Q: 如何实现分布式锁？
A: 使用 Redisson 或 Redis 的 SET NX EX 命令实现。

### Q: 如何添加限流功能？
A: 集成 Sentinel 或 Resilience4j 实现限流熔断。

### Q: 如何实现分布式事务？
A: 集成 Seata 或使用本地消息表实现最终一致性。

---

**🎉 恭喜！您已经成功配置了 Junmo Platform 框架模板！**