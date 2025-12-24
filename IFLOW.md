# Spring4demo 项目上下文

## 项目概述

这是一个基于 Spring Boot 4.0.1 和 Java 25 的学习项目，旨在展示 Spring Boot 最佳实践和常见集成模式。项目采用 Maven 构建系统，使用标准的 Spring Boot 项目结构。

## 技术栈

### 🔧 核心技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 应用框架核心 |
| Spring Framework | 6.x | 依赖注入、AOP等核心功能 |
| Java | 25 | 编程语言 |
| Maven | 3.9.12 | 构建工具 |

### 🌐 Web技术栈

- [x] **spring-boot-starter-web** - Spring MVC Web应用（默认Tomcat）
- [x] **spring-boot-starter-webflux** - 响应式Web编程
- [x] **spring-boot-starter-websocket** - WebSocket支持
- [x] **spring-boot-starter-web-services** - Spring Web Services
- [x] **spring-boot-starter-groovy-templates** - Groovy模板引擎
- [x] **spring-boot-starter-hateoas** - RESTful超媒体支持
- [x] **spring-boot-starter-graphql** - GraphQL应用支持

### 💾 数据存储技术栈

#### 关系型数据库
- [x] **spring-boot-starter-data-jpa** - JPA数据访问（Hibernate）
- [x] **spring-boot-starter-data-jdbc** - Spring Data JDBC
- [x] **spring-boot-starter-jdbc** - JDBC（HikariCP连接池）
- [x] **spring-boot-starter-Mybatis** - Mybatis(Druid连接池)
- [x] **spring-boot-starter-Mybatis-Plus** - Mybatis-Plus访问层

#### NoSQL数据库
- [x] **spring-boot-starter-data-mongodb** - MongoDB文档数据库
- [x] **spring-boot-starter-data-mongodb-reactive** - MongoDB响应式支持
- [x] **spring-boot-starter-data-redis** - Redis键值存储
- [x] **spring-boot-starter-data-redis-reactive** - Redis响应式支持
- [x] **spring-boot-starter-data-elasticsearch** - Elasticsearch搜索引擎
- [x] **spring-boot-starter-data-neo4j** - Neo4j图数据库
- [x] **spring-boot-starter-data-r2dbc** - R2DBC响应式数据库访问
- [x] **spring-boot-starter-data-influxdb** - InfluxDB时间序列数据库

### 📨 消息中间件技术栈

- [x] **spring-boot-starter-amqp** - Spring AMQP和RabbitMQ
- [x] **spring-boot-starter-rocketmq** - Apache RocketMQ消息队列
- [x] **spring-boot-starter-kafka** - Apache Kafka消息队列
- [x] **spring-boot-starter-integration** - Spring Integration企业集成模式
- [x] **spring-boot-starter-rsocket** - RSocket客户端和服务端

### 🔐 安全认证技术栈

- [x] **spring-boot-starter-security** - Spring Security安全框架
- [x] **spring-boot-starter-oauth2-client** - OAuth2/OpenID Connect客户端
- [x] **spring-boot-starter-oauth2-resource-server** - OAuth2资源服务器

### 📊 监控运维技术栈

- [x] **spring-boot-starter-actuator** - 生产就绪监控和管理功能
- [x] **spring-boot-starter-metrics** - Micrometer指标收集
- [x] **OpenTelemetry** - 遥测数据导出
- [x] **Zipkin** - 链路追踪
- [x] **Prometheus** - 指标收集
- [x] **Grafana** - 监控面板

### 🛠️ 开发工具和测试

- [x] **Spring Boot DevTools** - 热重载开发工具
- [x] **Spring Boot Configuration Processor** - 配置元数据生成
- [x] **Lombok** - 减少样板代码
- [x] **MapStruct** - Bean映射框架
- [x] **Testcontainers** - 集成测试容器支持

## 项目结构

## 构建和运行命令

### 基本构建命令

### Docker 相关命令

### GraalVM 原生镜像支持

## 配置说明

### 应用配置
- **主配置文件**: `src/main/resources/application.yaml`
- **应用名称**: spring4demo
- **配置格式**: YAML（推荐）

### 数据库配置（Docker Compose）

## 开发约定

### 包结构
- 基础包名: `com.kev1n.spring4demo`

### 代码风格
- 使用 Lombok 减少样板代码
- 遵循 Spring Boot 最佳实践
- 使用 Spring Boot Configuration Processor 生成配置元数据

### 测试策略
- 使用 JUnit 5 (Jupiter) 进行单元测试
- 使用 Spring Boot Test 进行集成测试
- 测试类命名: `{ClassName}Tests`

## 扩展指南

### 添加新功能
1. 在 `com.kev1n.spring4demo` 包下创建新的module
2. 在 `application.yaml` 中添加相关配置
3. 如需数据库支持，在 `db/migration` 目录添加迁移脚本
4. 编写对应的测试类

### 依赖管理
- 所有依赖通过 `pom.xml` 管理
- 继承自 Spring Boot 父 POM，版本统一管理
- 使用 Spring Boot BOM 管理第三方依赖版本

## 常见问题

### 端口冲突
如果遇到端口冲突，请修改 `compose.yaml` 中的端口映射。

### 数据库连接
确保 Docker 服务已启动，并且应用程序配置与 Docker 服务配置匹配。

### 原生镜像编译
需要安装 GraalVM 25+ 版本，并确保系统满足原生镜像编译要求。

## 参考资源

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/4.0.1/reference/)
- [Maven 官方文档](https://maven.apache.org/guides/)
- 项目 README.md 文件包含详细的模块说明和架构设计
- HELP.md 文件包含 Spring Boot 各功能模块的参考文档链接