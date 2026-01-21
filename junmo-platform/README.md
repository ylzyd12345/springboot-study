# Junmo Platform

> Junmo Platform - 企业级 Spring Boot 应用平台

## 项目简介

Junmo Platform 是一个基于 Spring Boot 4.0.1 和 Java 25 的企业级单体应用平台，采用前后端分离架构。项目采用 Maven 多模块设计，集成了现代 Java 生态系统中的主流技术栈，包括 Web 开发、数据存储、消息中间件、安全认证、监控运维等多个领域。

## 技术栈

- **Java**: 25
- **Spring Boot**: 4.0.1
- **Spring Framework**: 7.0.2
- **Maven**: 3.9.12

## 模块说明

### 核心模块

| 模块名称 | 说明 | 依赖关系 |
|---------|------|---------|
| **junmo-build-tools** | 构建工具模块 - 代码质量检查配置 | 无依赖 |
| **junmo-base** | 基础模块 - 最底层，无依赖，纯基础、无业务、public | 无依赖 |
| **junmo-common** | 公共模块 - 仅依赖 junmo-base，纯对内业务、default权限 | junmo-base |
| **junmo-api** | API 模块 - 仅依赖 junmo-base，纯对外契约层 | junmo-base |
| **junmo-model** | 模型模块 - 仅依赖 junmo-base，纯对内数据层 | junmo-base |
| **junmo-core** | 核心业务模块 - 依赖 junmo-api + junmo-model + junmo-base + junmo-common | junmo-api, junmo-model, junmo-base, junmo-common |
| **junmo-test-support** | 测试支持模块 - 依赖 junmo-base + junmo-common | junmo-base, junmo-common |
| **junmo-integration** | 集成测试模块 - 依赖 junmo-core + junmo-test-support | junmo-core, junmo-test-support |
| **junmo-generator** | 代码生成器模块 - 依赖 junmo-base + junmo-common | junmo-base, junmo-common |
| **junmo-starter** | 启动模块 - 仅依赖 junmo-core（front 端） | junmo-core |
| **junmo-admin-starter** | 管理后台启动模块 - 仅依赖 junmo-core（admin 端） | junmo-core |

### 依赖关系图

```
junmo-build-tools (无依赖)
    ↑
junmo-base (无依赖)
    ↑
junmo-common → 依赖 junmo-base
junmo-api → 依赖 junmo-base
junmo-model → 依赖 junmo-base
    ↑
junmo-core → 依赖 junmo-api + junmo-model + junmo-base + junmo-common
    ↑
junmo-starter → 依赖 junmo-core
junmo-admin-starter → 依赖 junmo-core

junmo-test-support → 依赖 junmo-base + junmo-common
junmo-integration → 依赖 junmo-core + junmo-test-support
junmo-generator → 依赖 junmo-base + junmo-common
```

## 快速开始

### 环境要求

- **Java**: JDK 25+
- **Maven**: 3.9.0+
- **Docker**: 20.0+ (用于运行中间件)

### 编译项目

```bash
# 清理编译
mvn clean compile

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行应用
mvn spring-boot:run -pl junmo-starter

# 运行管理后台
mvn spring-boot:run -pl junmo-admin-starter
```

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn jacoco:report
```

### 代码质量检查

```bash
# Checkstyle 检查
mvn checkstyle:check

# SpotBugs 检查
mvn spotbugs:check

# PMD 检查
mvn pmd:check

# 安全漏洞扫描
mvn dependency-check:check
```

## 访问地址

### 应用访问

- **前端应用**: http://localhost:8080
- **管理后台**: http://localhost:8081
- **健康检查**: http://localhost:8080/actuator/health

### API 文档

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Knife4j**: http://localhost:8080/doc.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 包结构

所有模块的包名使用 `com.junmo.platform.{module-name}` 格式：

- `com.junmo.platform.base` - 基础模块
- `com.junmo.platform.common` - 公共模块
- `com.junmo.platform.api` - API 模块
- `com.junmo.platform.model` - 模型模块
- `com.junmo.platform.core` - 核心业务模块
- `com.junmo.platform.test` - 测试支持模块
- `com.junmo.platform.integration` - 集成测试模块
- `com.junmo.platform.generator` - 代码生成器模块
- `com.junmo.platform.starter` - 启动模块
- `com.junmo.platform.admin.starter` - 管理后台启动模块

## 开发规范

### 命名规范

- 统一使用英文驼峰/蛇式，禁止拼音
- 常量全大写加下划线
- 类名使用大驼峰（PascalCase）
- 方法名和变量名使用小驼峰（camelCase）

### 代码风格

- 使用 Lombok 减少样板代码
- 遵循 Spring Boot 最佳实践
- 使用 MapStruct 进行 Bean 映射
- 代码风格检查：Checkstyle
- Bug 检测：SpotBugs
- 代码质量分析：PMD

### 测试规范

- 使用 JUnit 5 (Jupiter) 进行单元测试
- 使用 Spring Boot Test 进行集成测试
- 使用 Testcontainers 进行容器化集成测试
- 测试类命名: `{ClassName}Tests`
- 测试覆盖率目标: ≥ 85%

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请通过以下方式联系：

- GitHub Issues: https://github.com/junmo/junmo-platform/issues
- Email: support@junmo-platform.com

---

*最后更新时间：2026年1月16日*