# Spring4demo 项目上下文

## 项目概述

这是一个基于 Spring Boot 4.0.1 和 Java 25 的学习项目，旨在展示 Spring Boot 最佳实践和常见集成模式。项目采用 Maven 构建系统，使用标准的 Spring Boot 项目结构。

## 技术栈

- **框架**: Spring Boot 4.0.1
- **Java版本**: Java 25
- **构建工具**: Maven 3.9.12
- **数据库支持**: MySQL, Neo4j, Redis, Elasticsearch
- **容器化**: Docker Compose

## 项目结构

```
F:\codes\local_git\spring4demo\
├───src/
│   ├───main/
│   │   ├───java/com/kev1n/spring4demo/
│   │   │   └───Spring4demoApplication.java  # 主应用程序入口
│   │   └───resources/
│   │       ├───application.yaml             # 主配置文件
│   │       └───db/migration/                # 数据库迁移脚本目录
│   └───test/
│       └───java/com/kev1n/spring4demo/
│           └───Spring4demoApplicationTests.java  # 主测试类
├───compose.yaml                    # Docker Compose 配置
├───pom.xml                         # Maven 配置文件
├───README.md                       # 项目详细说明
└───HELP.md                         # Spring Boot 参考文档
```

## 构建和运行命令

### 基本构建命令
```bash
# 清理并编译项目
mvn clean compile

# 打包项目
mvn clean package

# 运行项目
mvn spring-boot:run

# 运行测试
mvn test
```

### Docker 相关命令
```bash
# 启动所有依赖服务（MySQL, Redis, Elasticsearch, Neo4j）
docker-compose up -d

# 停止所有服务
docker-compose down
```

### GraalVM 原生镜像支持
```bash
# 使用 Cloud Native Buildpacks 创建轻量级容器
./mvnw spring-boot:build-image -Pnative

# 使用 Native Build Tools 创建可执行文件
./mvnw native:compile -Pnative

# 在原生镜像中运行测试
./mvnw test -PnativeTest
```

## 配置说明

### 应用配置
- **主配置文件**: `src/main/resources/application.yaml`
- **应用名称**: spring4demo
- **配置格式**: YAML（推荐）

### 数据库配置（Docker Compose）
- **MySQL**: 端口 3306，数据库名 mydatabase，用户 myuser
- **Redis**: 端口 6379
- **Elasticsearch**: 端口 9200/9300，单节点模式
- **Neo4j**: 端口 7687，认证 neo4j/notverysecret

## 开发约定

### 包结构
- 基础包名: `com.kev1n.spring4demo`
- 主应用类: `Spring4demoApplication.java`
- 测试包名: `com.kev1n.spring4demo`（与主包相同）

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
1. 在 `com.kev1n.spring4demo` 包下创建新的类
2. 在 `application.yaml` 中添加相关配置
3. 如需数据库支持，在 `db/migration` 目录添加迁移脚本
4. 编写对应的测试类

### 依赖管理
- 所有依赖通过 `pom.xml` 管理
- 继承自 Spring Boot 父 POM，版本统一管理
- 建议使用 Spring Boot BOM 管理第三方依赖版本

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