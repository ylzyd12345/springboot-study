# Spring4demo 企业级智能管理平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3.3-3178C6.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 基于 Spring Boot 3.2.10 + Vue 3 + TypeScript 的企业级生态环境集成项目

## 📋 项目概述

Spring4demo 是一个全面的企业级智能管理平台，采用前后端分离架构。项目采用 Maven 多模块设计，集成了现代 Java 生态系统中的主流技术栈，为企业提供一站式数字化管理解决方案。

## 📚 详细文档

本项目包含完整的技术设计文档，请参考 [docs/DOCS_README.md](../docs/DOCS_README.md) 获取详细的文档目录和导航。

### 核心文档

- **[产品架构设计](../docs/01-规划设计层/01-产品架构设计.md)** - 产品定位和商业价值
- **[技术架构设计](../docs/01-规划设计层/04-技术架构设计.md)** - 技术选型和架构决策
- **[系统概要设计](../docs/02-系统设计层/06-概要设计.md)** - 系统架构和接口设计
- **[开发指南](../docs/04-运维支持层/22-开发指南.md)** - 环境搭建和编码规范
- **[运维手册](../docs/04-运维支持层/20-运维手册.md)** - 部署指南和故障处理

## 🏗️ 技术栈

### 后端技术
- **Spring Boot 3.2.10** - 应用框架核心
- **Java 21** - 编程语言
- **Sa-Token** - 轻量级权限认证框架
- **MyBatis-Plus** - 数据访问层框架
- **MySQL 8.0** - 主数据库
- **Redis 7.0** - 缓存和会话存储
- **RabbitMQ 3.12** - 消息队列
- **Elasticsearch 8.11** - 搜索引擎

### 前端技术
- **Vue.js 3.3.8** - 前端框架
- **TypeScript 5.3.3** - 类型系统
- **Element Plus 2.4.4** - UI组件库
- **Vite 5.0.10** - 构建工具

## 🚀 快速开始

### 环境要求
- **Java**: JDK 21+
- **Node.js**: 18.0.0+
- **Maven**: 3.9.0+
- **Docker**: 20.0+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/your-username/springboot-study.git
cd springboot-study

# 2. 启动基础服务
cd spring4demo
docker-compose up -d mysql redis rabbitmq

# 3. 启动后端服务
cd spring4demo-web
mvn spring-boot:run

# 4. 启动前端服务
cd ../../spring4demo-ui
npm install
npm run dev
```

### 访问地址
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html

## 📁 项目结构

```
spring4demo/
├── spring4demo/                    # 后端项目
│   ├── spring4demo-common/         # 公共模块
│   ├── spring4demo-core/           # 核心业务模块
│   ├── spring4demo-web/            # Web应用模块
│   ├── spring4demo-api/            # API接口模块
│   ├── spring4demo-admin/          # 管理后台模块
│   └── pom.xml                     # Maven父POM
├── spring4demo-ui/                 # 前端项目
└── docs/                           # 项目文档
```

## 🐳 Docker 部署

```bash
# 启动所有服务（包括日志系统）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f app
```

### 服务说明

项目集成了完整的监控和日志系统：

| 服务 | 地址 | 用途 |
|------|------|------|
| **应用服务** | | |
| app | http://localhost:8080 | Spring Boot 应用 |
| **数据库** | | |
| mysql | localhost:3306 | MySQL 8.0 数据库 |
| redis | localhost:6379 | Redis 7.0 缓存 |
| elasticsearch | http://localhost:9200 | Elasticsearch 8.11（业务文档检索） |
| neo4j | http://localhost:7474 | Neo4j 5.12 图数据库 |
| influxdb | http://localhost:8086 | InfluxDB 2.7 时序数据库 |
| **消息队列** | | |
| rabbitmq | http://localhost:15672 | RabbitMQ 3.12 (admin/admin) |
| kafka | localhost:9092 | Apache Kafka 7.4 |
| kafka-ui | http://localhost:8081 | Kafka UI 管理界面 |
| **监控运维** | | |
| prometheus | http://localhost:9090 | Prometheus 监控 |
| grafana | http://localhost:3000 | Grafana 可视化 (admin/admin) |
| loki | http://localhost:3100 | Loki 日志存储（日志采集查看） |
| promtail | http://localhost:9080 | Promtail 日志采集 |
| **可视化** | | |
| kibana | http://localhost:5601 | Kibana 日志分析（Elasticsearch） |
| nginx | http://localhost | Nginx 负载均衡 |

### 系统职责说明

**Elasticsearch + Kibana**：
- 📄 **用途**：业务文档检索和全文搜索
- 🔍 **场景**：产品信息、用户数据、订单记录等业务数据的搜索
- 💾 **特点**：强大的全文检索能力，适合结构化业务数据

**Loki + Grafana**：
- 📊 **用途**：应用日志采集和查看
- 🔍 **场景**：应用运行日志、错误日志、调试日志
- 💾 **特点**：轻量高效，与 Prometheus 技术栈统一

**两者不冲突**：
- Elasticsearch 处理业务数据的存储和检索
- Loki 处理应用日志的采集和查询
- 各司其职，互不干扰

## 🧪 测试

```bash
# 单元测试
mvn test

# 集成测试
mvn test -Pintegration-test

# 测试覆盖率
mvn jacoco:report
```

## 📖 更多信息

- **[完整文档目录](../docs/DOCS_README.md)** - 查看所有设计文档
- **[API文档规范](../docs/04-运维支持层/16-API文档规范.md)** - 接口设计规范
- **[用户手册](../docs/04-运维支持层/21-用户手册.md)** - 功能使用指南
- **[版本发布计划](../docs/04-运维支持层/23-版本发布计划.md)** - 版本规划

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！