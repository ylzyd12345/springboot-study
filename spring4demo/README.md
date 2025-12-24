# Spring4demo 企业级智能管理平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3.3-3178C6.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 基于 Spring Boot 4.0.1 + Vue 3 + TypeScript 的企业级生态环境集成项目

## 📋 项目概述

Spring4demo 是一个全面的企业级智能管理平台，采用前后端分离架构，集成了现代Java生态系统中的主流技术栈。项目旨在为企业提供一站式数字化管理解决方案，涵盖用户管理、权限控制、内容管理、数据分析、流程引擎等核心功能。

### 🎯 核心特性

- ✅ **多模块架构** - Maven多模块设计，清晰的分层结构
- ✅ **完整技术栈** - 集成50+热门技术，覆盖Web、数据、消息、监控等
- ✅ **前后端分离** - Vue 3 + TypeScript + Element Plus + Spring Boot
- ✅ **微服务就绪** - 预留微服务演进路径，支持服务拆分
- ✅ **容器化部署** - Docker + Docker Compose 一键部署
- ✅ **监控运维** - 完整的监控、日志、链路追踪体系
- ✅ **安全认证** - JWT + OAuth2 + RBAC权限模型
- ✅ **高性能** - Redis缓存 + Caffeine本地缓存 + 数据库优化

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 应用框架核心 |
| Spring Framework | 6.x | 依赖注入、AOP等核心功能 |
| Java | 25 | 编程语言 |
| Maven | 3.9.12 | 构建工具 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.0 | 缓存和会话存储 |
| RabbitMQ | 3.12 | 消息队列 |
| Kafka | 3.5 | 流处理平台 |
| Elasticsearch | 8.11 | 搜索引擎 |
| Neo4j | 5.12 | 图数据库 |
| InfluxDB | 2.7 | 时序数据库 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.3.8 | 前端框架 |
| TypeScript | 5.3.3 | 类型系统 |
| Element Plus | 2.4.4 | UI组件库 |
| Vite | 5.0.10 | 构建工具 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| ECharts | 5.4.3 | 图表库 |

### 监控运维栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Prometheus | 2.47.0 | 指标收集 |
| Grafana | 10.1.0 | 监控面板 |
| Zipkin | 2.24 | 链路追踪 |
| Kibana | 8.11.0 | 日志分析 |
| Nginx | 1.24 | 负载均衡 |

## 📁 项目结构

```
spring4demo/
├── spring4demo/                             # 后端项目
│   ├── spring4demo-common/                  # 公共模块
│   ├── spring4demo-core/                    # 核心业务模块
│   ├── spring4demo-web/                     # Web应用模块
│   ├── spring4demo-api/                     # API接口模块
│   ├── spring4demo-admin/                   # 管理后台模块
│   ├── spring4demo-integration/             # 集成测试模块
│   ├── scripts/                             # 脚本文件
│   │   ├── mysql/                           # 数据库脚本
│   │   ├── redis/                           # Redis配置
│   │   ├── rabbitmq/                        # RabbitMQ配置
│   │   ├── nginx/                           # Nginx配置
│   │   ├── prometheus/                      # Prometheus配置
│   │   └── grafana/                         # Grafana配置
│   ├── docker-compose.yml                   # Docker编排文件
│   ├── Dockerfile                           # Docker镜像构建文件
│   └── pom.xml                              # Maven父POM
├── spring4demo-ui/                          # 前端项目
│   ├── src/                                 # 源代码
│   │   ├── components/                      # 公共组件
│   │   ├── views/                           # 页面组件
│   │   ├── router/                          # 路由配置
│   │   ├── stores/                          # 状态管理
│   │   ├── api/                             # API接口
│   │   ├── utils/                           # 工具函数
│   │   └── styles/                          # 样式文件
│   ├── package.json                         # 依赖配置
│   └── vite.config.ts                       # Vite配置
└── docs/                                    # 项目文档
    ├── 01-规划设计层/                        # 规划设计文档
    ├── 02-系统设计层/                        # 系统设计文档
    ├── 03-实施支撑层/                        # 实施支撑文档
    └── 04-运维支持层/                        # 运维支持文档
```

## 🚀 快速开始

### 环境要求

- **Java**: JDK 25+
- **Node.js**: 18.0.0+
- **Maven**: 3.9.0+
- **Docker**: 20.0+
- **Docker Compose**: 2.0+

### 1. 克隆项目

```bash
git clone https://github.com/your-username/springboot-study.git
cd springboot-study
```

### 2. 启动基础服务

```bash
cd spring4demo
docker-compose up -d mysql redis rabbitmq kafka elasticsearch
```

### 3. 启动后端服务

```bash
# 编译项目
mvn clean compile

# 启动Web应用
cd spring4demo-web
mvn spring-boot:run

# 或者启动管理后台
cd ../spring4demo-admin
mvn spring-boot:run
```

### 4. 启动前端服务

```bash
cd spring4demo-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 5. 访问应用

- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **管理后台**: http://localhost:8081
- **监控面板**: http://localhost:3000 (Grafana)
- **日志分析**: http://localhost:5601 (Kibana)

### 6. 默认账号

- **管理员账号**: admin / admin123
- **普通用户**: user / user123

## 🐳 Docker 部署

### 完整环境部署

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app
```

### 生产环境部署

```bash
# 使用生产配置
docker-compose -f docker-compose.prod.yml up -d

# 构建应用镜像
docker build -t spring4demo:latest .

# 运行应用容器
docker run -d \
  --name spring4demo-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  spring4demo:latest
```

## 📊 监控面板

### Prometheus指标

- **应用指标**: http://localhost:9090
- **系统指标**: CPU、内存、磁盘、网络
- **业务指标**: 请求量、响应时间、错误率

### Grafana仪表板

- **系统概览**: 整体系统状态监控
- **应用性能**: JVM性能、接口响应时间
- **数据库监控**: MySQL、Redis、Elasticsearch
- **消息队列**: RabbitMQ、Kafka状态

### 链路追踪

- **Zipkin**: http://localhost:9411
- **请求链路**: 完整的请求调用链
- **性能分析**: 接口性能瓶颈分析

## 🧪 测试

### 运行测试

```bash
# 单元测试
mvn test

# 集成测试
mvn test -Pintegration-test

# 前端测试
cd spring4demo-ui
npm run test

# 测试覆盖率
mvn jacoco:report
npm run test:coverage
```

### 测试环境

- **H2内存数据库**: 快速测试
- **Testcontainers**: 集成测试
- **Mock数据**: 前端测试

## 📖 API文档

### Swagger文档

访问 http://localhost:8080/swagger-ui.html 查看完整的API文档。

### 主要API

- **认证接口**: `/api/auth/**`
- **用户管理**: `/api/users/**`
- **角色权限**: `/api/roles/**`
- **文件管理**: `/api/files/**`
- **系统监控**: `/actuator/**`

## 🔧 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring4demo
    username: spring4demo
    password: password
```

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:
```

### RabbitMQ配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 🚀 性能优化

### 后端优化

- **连接池优化**: HikariCP配置调优
- **缓存策略**: Redis + Caffeine多级缓存
- **JVM调优**: G1GC + 内存参数优化
- **数据库优化**: 索引优化 + 查询优化

### 前端优化

- **代码分割**: 路由级代码分割
- **资源压缩**: Gzip + Brotli压缩
- **缓存策略**: 浏览器缓存 + CDN
- **懒加载**: 图片懒加载 + 组件懒加载

## 📝 开发规范

### 代码规范

- **Java**: 遵循阿里巴巴Java开发手册
- **TypeScript**: 使用ESLint + Prettier
- **Git**: 使用Conventional Commits规范
- **文档**: 使用Markdown + Mermaid图表

### 提交规范

```bash
# 提交格式
<type>(<scope>): <description>

# 示例
feat(user): 添加用户管理功能
fix(auth): 修复登录验证bug
docs(api): 更新API文档
```

## 🛠️ 故障排除

### 常见问题

1. **端口冲突**: 修改docker-compose.yml中的端口映射
2. **内存不足**: 调整JVM参数和Docker内存限制
3. **数据库连接失败**: 检查数据库服务状态和连接配置
4. **前端构建失败**: 清除node_modules重新安装依赖

### 日志查看

```bash
# 应用日志
docker-compose logs -f app

# 数据库日志
docker-compose logs -f mysql

# 消息队列日志
docker-compose logs -f rabbitmq
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系我们

- **项目维护者**: Spring4demo Team
- **邮箱**: support@spring4demo.com
- **文档**: [项目文档](../docs/)

---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！