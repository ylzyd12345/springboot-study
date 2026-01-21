# Junmo Platform 企业级智能管理平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.8-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3.3-3178C6.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 基于 Spring Boot 4.0.1 + Vue 3 + TypeScript 的企业级生态环境集成项目

## 📋 项目概述

Junmo Platform 是一个全面的企业级智能管理平台，采用前后端分离架构。项目采用 Maven 多模块设计，集成了现代 Java 生态系统中的主流技术栈，为企业提供一站式数字化管理解决方案。

**架构定位**：单体 Spring Boot 应用（非微服务架构），适用于中小规模业务场景。

**项目阶段**：功能完善阶段，技术栈覆盖率 100%，核心组件实现率 97.2%。

**文档完成度**：100%（23 个设计文档全部完成）

## ✨ 核心特性

### 🏗️ 完整的技术栈集成
- **Web 开发**：Spring MVC、WebFlux、WebSocket、GraphQL
- **数据存储**：MySQL、MongoDB、Redis、Elasticsearch、Neo4j、InfluxDB
- **消息队列**：RabbitMQ、Apache Kafka
- **分布式组件**：Redisson、Dynamic DataSource、Seata、ShardingSphere
- **文件存储**：RustFS（S3 协议）、KKFileView（在线预览）
- **任务调度**：Spring Task、Quartz
- **监控运维**：Prometheus、Grafana、Loki、Zipkin

### 🎨 现代化前端
- **框架**：Vue 3 + TypeScript + Vite
- **UI 组件**：Element Plus
- **状态管理**：Pinia
- **图表可视化**：ECharts
- **国际化**：Vue I18n

### 🧪 完善的测试支持
- **Testcontainers**：容器化集成测试
- **WireMock**：HTTP 服务模拟
- **JaCoCo**：代码覆盖率
- **Vitest**：前端单元测试

## 🚀 快速开始

### 环境要求

- **Java**: JDK 25+
- **Node.js**: 18.0.0+
- **Maven**: 3.9.0+
- **Docker**: 20.0+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/junmo/junmo-platform.git
cd junmo-platform

# 2. 启动基础服务
cd junmo-platform
docker-compose up -d mysql redis rabbitmq

# 3. 启动后端服务
mvn spring-boot:run -pl junmo-starter

# 4. 启动前端服务
cd junmo-ui
npm install
npm run dev
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| **前端应用** | http://localhost:3000 | Vue 3 前端应用 |
| **后端 API** | http://localhost:8080 | Spring Boot 应用 |
| **API 文档** | http://localhost:8080/swagger-ui.html | Swagger UI |
| **API 文档** | http://localhost:8080/doc.html | Knife4j |
| **健康检查** | http://localhost:8080/actuator/health | 应用健康状态 |

### 中间件管理界面

| 服务 | 地址 | 账号密码 |
|------|------|----------|
| **RabbitMQ** | http://localhost:15672 | admin/admin |
| **Kafka UI** | http://localhost:8081 | - |
| **Kibana** | http://localhost:5601 | - |
| **Neo4j** | http://localhost:7474 | neo4j/password |
| **InfluxDB** | http://localhost:8181 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Zipkin** | http://localhost:9411 | - |
| **Loki** | http://localhost:3100 | - |

## 📁 项目结构

```
junmo-platform/
├── junmo-platform/                 # 后端项目
│   ├── junmo-base/                 # 基础模块
│   ├── junmo-common/               # 公共模块
│   ├── junmo-core/                 # 核心业务模块
│   ├── junmo-api/                  # API接口模块
│   ├── junmo-model/                # 模型模块
│   ├── junmo-admin-starter/        # 管理后台启动模块
│   ├── junmo-starter/              # 前端启动模块
│   ├── junmo-test-support/         # 测试支持模块
│   ├── junmo-build-tools/          # 构建工具模块
│   ├── junmo-generator/            # 代码生成器模块
│   ├── junmo-integration/          # 集成测试模块
│   ├── scripts/                    # 脚本目录
│   ├── docker-compose.yml          # Docker 编排文件
│   └── pom.xml                     # Maven 父 POM
├── junmo-ui/                       # 前端项目
│   ├── src/                        # 源代码
│   ├── package.json                # 依赖配置
│   └── vite.config.ts              # Vite 配置
├── docs/                           # 项目文档
│   ├── 01-规划设计层/               # 规划设计文档
│   ├── 02-系统设计层/               # 系统设计文档
│   ├── 03-实施支撑层/               # 实施支撑文档
│   ├── 04-运维支持层/               # 运维支持文档
│   ├── 05-最佳实践/                 # 最佳实践文档
│   └── DOCS_README.md              # 文档目录
├── scripts/                        # 脚本文件
│   ├── grafana/                    # Grafana 配置
│   ├── prometheus/                 # Prometheus 配置
│   └── loki/                       # Loki 配置
├── assets/                         # 资源文件
├── IFLOW.md                        # iFlow 项目上下文
├── SKILL.md                        # 技能配置
└── README.md                       # 项目说明
```

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 应用框架核心 |
| Java | 25 | 编程语言 |
| Sa-Token | 1.44.0 | 轻量级权限认证框架 |
| MyBatis-Plus | 3.5.9 | 数据访问层框架 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.0 | 缓存和会话存储 |
| Redisson | 3.35.0 | Redis 客户端 |
| Dynamic DataSource | 4.3.0 | 动态多数据源 |
| Seata | 2.5.0 | 分布式事务解决方案 |
| RabbitMQ | 5.17.0 | 消息队列 |
| Elasticsearch | 8.11.4 | 搜索引擎 |
| Neo4j | 5.12.0 | 图数据库 |
| InfluxDB | 3.0 | 时序数据库 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.3.8 | 前端框架 |
| TypeScript | 5.3.3 | 类型系统 |
| Element Plus | 2.4.4 | UI 组件库 |
| Vite | 5.0.10 | 构建工具 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.2.5 | 路由管理 |
| Axios | 1.6.2 | HTTP 客户端 |
| ECharts | 5.4.3 | 图表库 |

## 🐳 Docker 部署

### 启动所有服务

```bash
cd junmo-platform
docker-compose up -d
```

### 查看服务状态

```bash
docker-compose ps
```

### 查看应用日志

```bash
docker-compose logs -f app
```

### 停止所有服务

```bash
docker-compose down
```

### 服务说明

项目集成了完整的监控和日志系统：

**Elasticsearch + Kibana**：
- 📄 **用途**：业务文档检索和全文搜索
- 🔍 **场景**：产品信息、用户数据、订单记录等业务数据的搜索
- 💾 **特点**：强大的全文检索能力，适合结构化业务数据

**Loki + Grafana**：
- 📊 **用途**：应用日志采集和查看
- 🔍 **场景**：应用运行日志、错误日志、调试日志
- 💾 **特点**：轻量高效，与 Prometheus 技术栈统一

## 🧪 测试

### 后端测试

```bash
# 单元测试
mvn test

# 集成测试
mvn test -Pintegration-test

# 测试覆盖率
mvn jacoco:report
```

### 前端测试

```bash
# 运行测试
npm run test

# 测试覆盖率
npm run test:coverage

# 测试 UI
npm run test:ui
```

## 📖 文档

项目包含完整的技术设计文档，共 23 个文档文件。

### 文档导航

详见 [docs/DOCS_README.md](docs/DOCS_README.md) 获取完整的文档目录和导航。

### 核心文档

- **[产品架构设计](docs/01-规划设计层/01-产品架构设计.md)** - 产品定位和商业价值
- **[技术架构设计](docs/01-规划设计层/04-技术架构设计.md)** - 技术选型和架构决策
- **[系统概要设计](docs/02-系统设计层/06-概要设计.md)** - 系统架构和接口设计
- **[开发指南](docs/04-运维支持层/22-开发指南.md)** - 环境搭建和编码规范
- **[运维手册](docs/04-运维支持层/20-运维手册.md)** - 部署指南和故障处理

### 文档分类

#### 规划设计层（5个）
1. 产品架构设计
2. 项目管理
3. 总体设计
4. 技术架构设计
5. 项目结构设计

#### 系统设计层（7个）
6. 概要设计
7. 详细设计
8. 业务架构设计
9. 功能分解设计
10. 应用交互设计
11. 数据架构设计
12. 接口设计

#### 实施支撑层（3个）
13. 非功能性设计
14. 部署架构设计
15. 实施方案设计

#### 运维支持层（8个）
16. API文档规范
17. 数据库详细设计
18. 安全设计详细文档
19. 测试设计文档
20. 运维手册
21. 用户手册
22. 开发指南
23. 版本发布计划

## 📊 项目状态

### 代码质量评分

| 维度 | 得分 | 满分 | 状态 |
|------|------|------|------|
| 测试覆盖率 | 0 | 85 | ❌ 严重 |
| 代码规范 | 80 | 100 | ✅ 良好 |
| 代码质量 | 70 | 100 | ⚠️ 需改进 |
| 安全性 | 60 | 100 | ⚠️ 需改进 |
| 最佳实践 | 75 | 100 | ⚠️ 需改进 |
| 文档完整性 | 100 | 100 | ✅ 优秀 |

### 优先改进任务

#### P0（立即执行）
1. 添加单元测试（目标覆盖率 ≥ 80%）
2. 移除硬编码密码
3. 启用质量检查工具

#### P1（2-4周）
1. 完成 TODO 功能
2. 重构重复代码
3. 拆分过长方法

#### P2（1-2个月）
1. 性能优化
2. 安全加固
3. 监控完善

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码审查要求

在创建 Pull Request 之前，请确保：

1. 所有测试通过
2. 代码符合项目规范
3. 添加了必要的测试用例
4. 更新了相关文档
5. 没有引入安全漏洞

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- **GitHub Issues**: https://github.com/junmo/junmo-platform/issues
- **Email**: support@junmo-platform.com

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！