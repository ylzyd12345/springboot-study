# Spring4demo 项目上下文

## 项目概述

这是一个基于 Spring Boot 4.0.1 和 Java 25 的企业级单体应用项目，采用前后端分离架构。项目采用 Maven 多模块设计，集成了现代 Java 生态系统中的主流技术栈，包括 Web 开发、数据存储、消息中间件、安全认证、监控运维等多个领域。项目旨在为开发者提供一个全面、实用的 Spring Boot 生态系统参考实现。

**架构定位**：单体Spring Boot应用（非微服务架构），适用于中小规模业务场景。

**项目阶段**：功能完善阶段，技术栈覆盖率约90%，核心组件实现率约95%。

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
- [x] **spring-boot-starter-webflux** - 响应式Web编程（依赖引入，待实现）
- [x] **spring-boot-starter-websocket** - WebSocket支持（待实现）
- [x] **spring-boot-starter-web-services** - Spring Web Services
- [x] **spring-boot-starter-hateoas** - RESTful超媒体支持
- [x] **spring-boot-starter-graphql** - GraphQL应用支持（待实现）

### 💾 数据存储技术栈

#### 关系型数据库
- [x] **MySQL** - MySQL Connector 9.5.0
- [x] **H2** - H2 Database 2.4.240（测试环境）
- [x] **MyBatis-Plus** - MyBatis 增强工具 3.5.9
- [x] **PageHelper** - 分页插件 2.1.1
- [x] **Druid** - 高性能数据库连接池 1.2.20
- [x] **HikariCP** - Spring Boot 默认连接池
- [x] **Dynamic DataSource** - 动态多数据源 4.3.0

#### NoSQL数据库
- [ ] **MongoDB** - MongoDB文档数据库（待实现）
- [x] **Redis** - Redis键值存储 7.0
- [x] **Redisson** - Redis 客户端 3.35.0
- [ ] **Elasticsearch** - Elasticsearch搜索引擎 8.11.4（待实现）
- [x] **Neo4j** - Neo4j图数据库 5.12（已完成）
- [x] **InfluxDB** - InfluxDB时间序列数据库 2.7（已完成）

#### 文件存储
- [x] **RustFS** - 高性能分布式对象存储（兼容 S3 协议）
- [x] **AWS S3 SDK** - 2.29.29

#### 文档预览
- [x] **KKFileView** - 在线文件预览服务

#### 缓存
- [x] **Caffeine** - 本地缓存 3.1.6
- [x] **Guava** - Google工具库 32.1.3（含限流功能）

### 📨 消息中间件技术栈

- [x] **RabbitMQ** - RabbitMQ消息队列 5.17.0（使用Spring Boot AMQP接入）
- [x] **Apache Kafka** - Kafka消息队列 3.6.1（使用Spring Kafka接入）
- [ ] **Apache RocketMQ** - RocketMQ消息队列 5.1.4（不适用于单体应用）
- [x] **spring-boot-starter-integration** - Spring Integration企业集成模式
- [ ] **spring-boot-starter-rsocket** - RSocket客户端和服务端（不适用于单体应用）

### 🔐 安全认证技术栈

- [x] **Sa-Token** - 轻量级 Java 权限认证框架 1.44.0
- [x] **Sa-Token-OAuth2** - OAuth2/OpenID Connect 支持
- [x] **Sa-Token-Redis** - Redis 持久化支持

### 📅 任务调度技术栈

- [x] **Spring Task** - Spring 原生任务调度（简单定时任务）
- [x] **Quartz** - 定时任务框架 2.3.2（复杂调度需求）
- [ ] **Spring Batch** - 批量任务处理 5.1.1（可选，待实现）

### 📊 监控运维技术栈

- [x] **spring-boot-starter-actuator** - 生产就绪监控和管理功能
- [x] **Micrometer** - 指标收集 1.14.0
- [x] **Prometheus** - 指标收集 2.47.0
- [x] **Grafana** - 监控面板 10.1.0
- [x] **Zipkin** - 链路追踪 2.24
- [x] **Loki** - 日志聚合系统 2.9.2
- [x] **Promtail** - 日志采集代理 2.9.2
- [x] **Node Exporter** - 系统监控
- [x] **MySQL Exporter** - MySQL监控
- [x] **Redis Exporter** - Redis监控

### 🌐 负载均衡与网关

- [x] **Nginx** - 负载均衡 1.24-alpine（单体应用使用）
- [ ] **Spring Cloud Gateway** - 微服务API网关（不适用于单体应用）
- [ ] **Nacos** - 服务发现与配置中心（不适用于单体应用）
- [ ] **Sentinel** - 限流熔断（使用Guava限流替代）

### 📝 API文档

- [x] **SpringDoc OpenAPI** - OpenAPI 3 规范 2.7.0
- [x] **Knife4j** - 增强版Swagger UI 4.5.0

### 🛠️ 开发工具和测试

- [x] **Spring Boot DevTools** - 热重载开发工具
- [x] **Spring Boot Configuration Processor** - 配置元数据生成
- [x] **Lombok** - 减少样板代码 1.18.42
- [x] **MapStruct** - Bean映射框架 1.6.3
- [x] **Hutool** - Java工具类库 5.8.40
- [x] **Fastjson2** - JSON处理 2.0.57
- [x] **Testcontainers** - 集成测试容器支持 1.20.0
- [x] **WireMock** - HTTP服务模拟 3.9.0
- [x] **JaCoCo** - 代码覆盖率 0.8.12

### 🎨 前端技术栈

- [x] **Vue.js** - 前端框架 3.3.8
- [x] **TypeScript** - 类型系统 5.3.3
- [x] **Vite** - 构建工具 5.0.10
- [x] **Element Plus** - UI组件库 2.4.4
- [x] **Pinia** - 状态管理 2.1.7
- [x] **Vue Router** - 路由管理 4.2.5
- [x] **Axios** - HTTP客户端 1.6.2
- [x] **ECharts** - 图表库 5.4.3
- [x] **Day.js** - 日期处理 1.11.10
- [x] **Lodash-es** - JavaScript工具库 4.17.21
- [x] **Vue I18n** - 国际化支持 9.8.0
- [x] **Sortable.js** - 拖拽排序 1.15.0
- [x] **Vue Draggable** - Vue 拖拽组件 4.1.0
- [x] **Vitest** - 测试框架 1.0.4
- [x] **@vue/test-utils** - Vue 测试工具 2.4.2

### 🔧 代码质量工具

- [x] **Checkstyle** - 代码风格检查 3.6.0
- [x] **SpotBugs** - Bug检测 4.9.8.2
- [x] **PMD** - 代码质量分析 3.28.0
- [x] **OWASP Dependency-Check** - 安全漏洞扫描 12.1.9

### 🔄 分布式事务

- [x] **Seata** - 分布式事务解决方案 2.5.0（部分实现）
- [x] **Dubbo Seata Filter** - 安全漏洞修复 3.3.1

## 项目结构

### 后端模块结构

```
spring4demo/
├── build-tools/              # 构建工具模块 - 代码质量检查配置
├── spring4demo-common/       # 公共模块 - 通用工具类、常量、基础配置
├── spring4demo-core/         # 核心业务模块 - 业务逻辑、实体类、数据访问
├── spring4demo-web/          # Web应用模块 - Controller层、Web配置
├── spring4demo-api/          # API接口模块 - 对外API定义、DTO
├── spring4demo-admin/        # 管理后台模块 - 后台管理功能
├── spring4demo-integration/  # 集成测试模块 - 集成测试
├── spring4demo-generator/    # 代码生成器模块 - 代码生成工具
└── spring4demo-starter/      # 启动器模块 - 应用启动入口
```

### 前端项目结构

```
spring4demo-ui/
├── src/
│   ├── main.ts               # 应用入口
│   ├── App.vue               # 根组件
│   └── router/               # 路由配置
├── package.json              # 依赖配置
└── vite.config.ts            # Vite配置
```

## 构建和运行命令

### 基本构建命令

```bash
# 清理编译
mvn clean compile

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行应用
mvn spring-boot:run

# 指定环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 测试命令

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn test -Pintegration-test

# 生成测试覆盖率报告
mvn jacoco:report

# 代码质量检查
mvn checkstyle:check
mvn spotbugs:check
mvn pmd:check
mvn dependency-check:check
```

### 前端命令

```bash
# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 代码检查
npm run lint

# 代码格式化
npm run format

# 运行测试
npm run test

# 测试覆盖率
npm run test:coverage
```

### Docker 相关命令

```bash
# 启动所有服务
docker-compose up -d

# 启动特定服务
docker-compose up -d mysql redis

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app

# 重启服务
docker-compose restart app
```

### GraalVM 原生镜像支持

```bash
# 构建原生镜像
mvn -Pnative native:compile

# 运行原生镜像
./target/spring4demo
```

## 配置说明

### 应用配置

- **主配置文件**: `spring4demo-starter/src/main/resources/application.yml`
- **环境配置**:
    - 开发环境: `application-dev.yml`
    - 测试环境: `application-test.yml`
    - 生产环境: `application-prod.yml`
- **应用名称**: spring4demo
- **默认端口**: 8080
- **配置格式**: YAML

### 数据库配置（Docker Compose）

```yaml
# MySQL
- 端口: 3306
- 数据库: spring4demo
- 用户名: spring4demo
- 密码: spring4demo

# Redis
- 端口: 6379
- 无密码

# RabbitMQ
- 端口: 5672 (AMQP), 15672 (管理界面)
- 用户名: admin
- 密码: admin

# Kafka
- 端口: 9092
- ZooKeeper: 2181
- Kafka UI: 8081

# Elasticsearch
- 端口: 9200 (HTTP), 9300 (TCP)
- Kibana: 5601

# Neo4j
- 端口: 7474 (HTTP), 7687 (Bolt)
- 用户名: neo4j
- 密码: password

# InfluxDB
- 端口: 8086
- 用户名: admin
- 密码: password
- 组织: spring4demo
- 存储桶: metrics

# Prometheus
- 端口: 9090

# Grafana
- 端口: 3000
- 用户名: admin
- 密码: admin

# Zipkin
- 端口: 9411

# Loki
- 端口: 3100

# Nginx
- 端口: 80 (HTTP), 443 (HTTPS)

# RustFS
- 端口: 9000
- 访问密钥: admin
- 秘密密钥: admin123

# KKFileView
- 端口: 8012
- 预览接口: /onlinePreview
```

### RustFS 配置

RustFS 是一款基于 Rust 语言开发的高性能分布式对象存储软件，完全兼容 AWS S3 协议。

```yaml
rustfs:
  # RustFS 服务端点地址
  endpoint: http://localhost:9000
  # 访问密钥
  access-key: admin
  # 秘密密钥
  secret-key: admin123
  # 存储桶名称
  bucket-name: spring4demo
  # 区域
  region: us-east-1
  # 是否启用路径风格访问
  path-style-access: true
  # 最大文件大小（MB）
  max-file-size: 10
  # 最大请求大小（MB）
  max-request-size: 100
```

### KKFileView 配置

KKFileView 是一个文件文档在线预览解决方案，支持多种格式的文件预览。

```yaml
kkfileview:
  # KKFileView 服务地址
  server-url: http://localhost:8012
  # 预览接口路径
  preview-path: /onlinePreview
  # 是否使用缓存
  use-cache: false
  # 缓存过期时间（秒）
  cache-expire-time: 3600
  # 当前服务地址（用于生成文件 URL）
  current-server-url: http://localhost:8080
  # 是否强制更新缓存
  force-update-cache: true
```

### Quartz 任务调度配置

Quartz 是一个强大的定时任务调度框架，支持复杂的调度需求。

```yaml
# Quartz 配置
quartz:
  # 是否启用Quartz调度器
  enabled: true
  # 调度器实例名称
  instance-name: QuartzScheduler
  # 调度器实例ID（AUTO表示自动生成）
  instance-id: AUTO
  # 任务存储配置
  job-store:
    # 任务存储类（RAMJobStore用于内存存储，JobStoreTX用于JDBC持久化）
    job-store-class: org.quartz.simpl.RAMJobStore
    # 数据库驱动代理类（使用JDBC持久化时需要配置）
    driver-delegate-class: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
    # 数据库表前缀
    table-prefix: QRTZ_
    # 是否启用集群模式
    is-clustered: false
  # 线程池配置
  thread-pool:
    # 线程池实现类
    thread-pool-class: org.quartz.simpl.SimpleThreadPool
    # 线程数量
    thread-count: 5
    # 线程优先级
    thread-priority: 5
```

### Loki 日志聚合配置

Loki 是一个水平可扩展、高可用的多租户日志聚合系统。

```yaml
loki:
  # Loki 服务地址
  url: http://localhost:3100
  # 是否启用日志推送
  enabled: true
  # 日志标签
  labels:
    app: spring4demo
    env: ${spring.profiles.active:dev}
  # 日志级别
  level: INFO
```

### Promtail 日志采集配置

Promtail 是 Loki 的日志采集代理，用于采集应用日志并推送到 Loki。

```yaml
promtail:
  # Promtail 配置文件路径
  config-path: ./scripts/loki/promtail-config.yml
  # 日志采集路径
  log-path: /var/log/app
  # 是否启用
  enabled: true
```

### Nginx 负载均衡配置

Nginx 用于负载均衡和反向代理。

```yaml
nginx:
  # Nginx 配置文件路径
  config-path: ./scripts/nginx/nginx.conf
  # 监听端口
  http-port: 80
  https-port: 443
  # 是否启用
  enabled: true
```

### Maven Profile配置

```bash
# 开发环境（默认）
mvn spring-boot:run -Pdev

# 测试环境
mvn spring-boot:run -Ptest

# 生产环境
mvn spring-boot:run -Pprod

# 集成测试
mvn test -Pintegration-test

# 清理缓存
mvn clean -Pcache-clean
```

## 开发约定

### 包结构

- **基础包名**: `com.kev1n.spring4demo`
- **common模块**: 通用工具类、常量、基础配置
- **core模块**: 业务逻辑、实体类、数据访问层
- **web模块**: Controller层、Web配置
- **api模块**: 对外API定义、DTO

### 代码风格

- 使用 Lombok 减少样板代码
- 遵循 Spring Boot 最佳实践
- 使用 Spring Boot Configuration Processor 生成配置元数据
- 使用 MapStruct 进行 Bean 映射
- 代码风格检查：Checkstyle
- Bug检测：SpotBugs
- 代码质量分析：PMD

### 测试策略

- 使用 JUnit 5 (Jupiter) 进行单元测试
- 使用 Spring Boot Test 进行集成测试
- 使用 Testcontainers 进行容器化集成测试
- 使用 WireMock 进行HTTP服务模拟
- 使用 Vitest 进行前端测试
- 测试类命名: `{ClassName}Tests`
- 测试覆盖率目标: 85%+

### API设计规范

- RESTful API设计
- 使用 OpenAPI 3 规范
- API版本管理：通过 `X-API-Version` 请求头
- 使用 Knife4j 提供增强的API文档
- 统一响应格式

### 安全规范

- 使用 Sa-Token 进行权限认证
- JWT Token 认证
- OAuth2/OIDC 支持
- API安全最佳实践
- 定期进行安全漏洞扫描（OWASP Dependency-Check）

## 扩展指南

### 添加新功能

1. 在 `com.kev1n.spring4demo` 包下创建新的模块或包
2. 在 `application.yml` 中添加相关配置
3. 如需数据库支持，创建实体类和Mapper
4. 编写对应的测试类
5. 更新API文档

### 代码生成

使用 `spring4demo-generator` 模块生成基础代码：

```bash
cd spring4demo-generator
mvn spring-boot:run
```

生成的代码包括：
- CRUD Controller
- Request/Response DTO
- Controller 测试类

### 依赖管理

- 所有依赖通过 `pom.xml` 管理
- 继承自 Spring Boot 父 POM，版本统一管理
- 使用 Spring Boot BOM 管理第三方依赖版本
- 使用阿里云Maven镜像加速依赖下载

## 常见问题

### 端口冲突

如果遇到端口冲突，请修改 `docker-compose.yml` 中的端口映射或 `application.yml` 中的配置。

### 数据库连接

确保 Docker 服务已启动，并且应用程序配置与 Docker 服务配置匹配。

### 依赖下载慢

项目已配置阿里云Maven镜像，如果仍然慢，可以：
1. 检查网络连接
2. 使用VPN
3. 配置本地Maven仓库

### 原生镜像编译

需要安装 GraalVM 21+ 版本，并确保系统满足原生镜像编译要求。

### 测试失败

1. 检查Docker服务是否正常运行
2. 检查数据库连接配置
3. 查看测试日志获取详细信息

## 访问地址

### 应用访问

- **后端应用**: http://localhost:8080
- **前端应用**: http://localhost:3000
- **健康检查**: http://localhost:8080/actuator/health

### API文档

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Knife4j**: http://localhost:8080/doc.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### 中间件管理界面

- **RabbitMQ管理**: http://localhost:15672 (admin/admin)
- **Kafka UI**: http://localhost:8081
- **Kibana**: http://localhost:5601
- **Neo4j Browser**: http://localhost:7474
- **InfluxDB UI**: http://localhost:8086
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Zipkin**: http://localhost:9411
- **Loki**: http://localhost:3100
- **Nginx**: http://localhost:80
- **RustFS**: http://localhost:9000
- **KKFileView**: http://localhost:8012

## 文件存储与预览

### RustFS 文件存储

RustFS 提供高性能的分布式对象存储服务，兼容 S3 协议。

**API 接口：**
- `POST /api/files/upload` - 上传文件
- `GET /api/files/download/{fileName}` - 下载文件
- `DELETE /api/files/{fileName}` - 删除文件
- `DELETE /api/files/batch` - 批量删除文件
- `GET /api/files` - 列出所有文件
- `GET /api/files/{fileName}/url` - 获取文件访问 URL

**Docker 启动：**
```bash
docker run -p 9000:9000 --name rustfs \
  -e RUSTFS_ACCESS_KEY=admin \
  -e RUSTFS_SECRET_KEY=admin123 \
  -v /data/rustfs:/data \
  -d rustfs/rustfs
```

### KKFileView 文档预览

KKFileView 提供多种格式文件的在线预览功能。

**支持的文件类型：**
- Office 文档: .doc, .docx, .xls, .xlsx, .ppt, .pptx
- PDF: .pdf
- 文本文件: .txt, .xml, .html, .htm, .md, .json, .csv
- 图片: .jpg, .jpeg, .png, .gif, .bmp, .svg, .webp
- 压缩文件: .zip, .rar, .7z, .tar, .gz
- 视频文件: .mp4, .avi, .mkv, .mov, .wmv, .flv
- 音频文件: .mp3, .wav, .ogg, .flac, .aac

**API 接口：**
- `GET /api/preview/{fileName}` - 生成文件预览 URL
- `GET /api/preview/{fileName}/watermark` - 生成带水印的预览 URL
- `GET /api/preview/{fileName}/check` - 检查文件是否支持预览
- `GET /api/preview/supported-types` - 获取支持的文件类型列表
- `GET /api/preview/{fileName}/redirect` - 重定向到预览页面

**Docker 启动：**
```bash
docker run -d -p 8012:8012 --name kkfileview keking/kkfileview
```

## 日志管理

### Loki 日志聚合

Loki 是一个水平可扩展、高可用的多租户日志聚合系统，与 Grafana 集成，提供强大的日志查询和分析功能。

**特性：**
- 高效的日志存储和索引
- 与 Grafana 无缝集成
- 支持多种日志格式
- 分布式架构
- 低资源消耗

**使用方式：**
1. 应用日志通过 Promtail 采集
2. Promtail 将日志推送到 Loki
3. 在 Grafana 中查询和分析日志

### Promtail 日志采集

Promtail 是 Loki 的日志采集代理，用于采集应用日志并推送到 Loki。

**配置文件**: `scripts/loki/promtail-config.yml`

**支持的日志源：**
- 文件日志
- Docker 容器日志
- 系统日志
- 应用日志

## 参考资源

### 官方文档

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/4.0.1/reference/)
- [Spring Framework 官方文档](https://docs.spring.io/spring-framework/reference/)
- [Maven 官方文档](https://maven.apache.org/guides/)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Sa-Token 官方文档](https://sa-token.cc/)
- [Knife4j 官方文档](https://doc.xiaominfo.com/)
- [Loki 官方文档](https://grafana.com/docs/loki/latest/)
- [Promtail 官方文档](https://grafana.com/docs/loki/latest/clients/promtail/)
- [Seata 官方文档](https://seata.io/zh-cn/)
- [Redisson 官方文档](https://redisson.org/)

### 项目文档

- 项目 README.md 文件包含详细的模块说明和架构设计
- HELP.md 文件包含 Spring Boot 各功能模块的参考文档链接
- docs/ 目录包含完整的技术设计文档

### 技术社区

- [Spring 官方论坛](https://community.spring.io/)
- [Stack Overflow - Spring Boot](https://stackoverflow.com/questions/tagged/spring-boot)
- [GitHub Issues](https://github.com/ylzyd12345/springboot-study/issues)

## 架构决策

### 模块职责划分

- **build-tools模块**: 代码质量检查配置（Checkstyle、SpotBugs、PMD）
- **common模块**: 只做公共功能（工具类、常量、基础配置），不包含业务逻辑，不依赖其他业务模块
- **core模块**: 核心业务逻辑，包含实体类、数据访问层、业务服务层
- **web模块**: Web层，包含Controller、Web配置，依赖core模块
- **api模块**: 对外API定义，包含DTO、API接口定义
- **admin模块**: 管理后台功能
- **generator模块**: 代码生成工具
- **starter模块**: 应用启动入口，整合所有模块

### 依赖原则

- common模块不依赖任何其他业务模块
- core模块可以依赖common模块
- web模块依赖core模块和common模块
- 避免循环依赖
- 保持模块职责单一

### 技术选型原则

- 优先选择Spring Boot官方 starter
- 选择社区活跃、文档完善的开源组件
- 考虑性能、可维护性和社区支持
- 定期更新依赖版本，修复安全漏洞

### 架构定位决策

**单体Spring Boot应用**：基于当前业务规模和团队规模，决定采用单体Spring Boot应用架构，而非微服务架构。

**架构调整**：
- 去掉微服务组件：Spring Cloud Gateway、Nacos服务发现、Nacos配置中心、Sentinel限流熔断、RSocket、RocketMQ
- 接入层简化为Nginx负载均衡器
- 限流方案从Sentinel调整为Guava限流
- 消息队列使用Spring Boot原生AMQP和Spring Kafka接入RabbitMQ和Kafka
- 保留MongoDB、Elasticsearch、Neo4j、InfluxDB等数据存储

**演进路径**：单体应用 → 模块化重构 → 可选的微服务拆分（根据业务需求）

### 文件存储与预览架构

- **RustFS**: 使用兼容 S3 协议的高性能分布式对象存储
- **KKFileView**: 使用 KKFileView 实现多种格式文件的在线预览
- **集成方式**: 通过 AWS S3 SDK 访问 RustFS，通过 URL 参数调用 KKFileView 预览接口
- **模块划分**:
    - common 模块: RustFS 和 KKFileView 配置类
    - core 模块: 文件存储服务和文档预览服务
    - web 模块: 文件上传下载控制器和文档预览控制器

### 任务调度架构

- **Spring Task**: 用于简单定时任务，如数据清理、缓存刷新等
- **Quartz**: 用于复杂调度需求，如多任务依赖、动态调度等
- **集成方式**: 通过注解和配置类集成，支持任务持久化和集群模式
- **模块划分**:
    - common 模块: Spring Task 和 Quartz 配置类
    - core 模块: 任务处理器和任务调度服务
    - web 模块: 任务管理 Controller

### 日志管理架构

- **Loki**: 日志聚合系统，用于集中存储和管理日志
- **Promtail**: 日志采集代理，用于采集应用日志并推送到 Loki
- **Grafana**: 可视化平台，用于查询和分析日志
- **集成方式**: 应用日志 → Promtail → Loki → Grafana
- **优势**:
    - 高效的日志存储和检索
    - 与监控系统集成
    - 支持分布式日志聚合
    - 低资源消耗

### 分布式事务架构

- **Seata**: 分布式事务解决方案，支持 AT、TCC、Saga 等模式
- **Dubbo Seata Filter**: 修复安全漏洞，增强 Dubbo 与 Seata 的集成
- **集成方式**: 通过 Seata Starter 集成，支持自动事务代理
- **使用场景**:
    - 跨服务事务
    - 分布式数据一致性
    - 微服务架构

### 负载均衡架构

- **Nginx**: 负载均衡和反向代理
- **集成方式**: 通过 Nginx 配置文件实现负载均衡策略
- **支持特性**:
    - 轮询、加权轮询、IP 哈希等负载均衡算法
    - 健康检查
    - SSL/TLS 支持
    - 静态资源服务

### 限流架构

- **Guava RateLimiter**: 轻量级限流方案，适用于单体应用
- **替代方案**: Redis + Lua脚本、Bucket4j
- **集成方式**: 通过RateLimiterService实现接口限流
- **优势**:
    - 简单易用
    - 性能优秀
    - 适合单体应用场景

### 消息队列架构

- **Spring Boot AMQP**: RabbitMQ 原生集成
- **Spring Kafka**: Kafka 原生集成
- **支持的消息队列**: RabbitMQ、Kafka
- **集成方式**: 通过 Spring Boot 原生 API 接入消息队列
- **优势**:
  - 更轻量级，无额外依赖
  - 更直接的 API，更易调试
  - 更好的性能和资源利用
  - 更灵活的配置和控制
  - 完全不依赖 Spring Cloud 生态
## 文档维护

本文档应随项目演进持续更新，包括：

- 新增技术栈的说明
- 配置变更的记录
- 架构决策的说明
- 常见问题的补充
- 最佳实践的总结

更新时请保持文档的准确性和时效性。包括：
- README.md
- IFLOW.md
- /docs/**/*.md

## 项目规划

### 当前阶段

**工程框架搭建阶段**：技术栈覆盖率约95%，核心组件实现率约100%。

### 实施计划

详见 `projectplans/.architecture_plan.md`，包括：
- Guava限流实施计划（已完成）
- Spring Boot消息队列实施计划（已完成）
- MongoDB、Elasticsearch实施计划（已完成）
- Neo4j、InfluxDB图/时序数据库实施计划（已完成）
- WebFlux、WebSocket实施计划（已完成）
- GraphQL实施计划（待实现）

### 技术栈差异

详见 `REVIEW_DESIGN_CODE.md`，包括：
- 技术栈差异对比表
- 未实现的关键组件清单
- 架构师建议
- 实施路线图

### 最佳实践

详见 `docs/05-最佳实践/TECH_DEMO.md`，包括：
- Guava限流代码示例和最佳实践
- Spring Boot消息队列代码示例和最佳实践
- MongoDB、Elasticsearch、Neo4j、InfluxDB代码示例和最佳实践