# Junmo Platform 构建优化指南

本文档介绍如何优化 Junmo Platform 项目的构建速度和包体积，提升开发效率和 CI/CD 性能。

## 目录

- [优化方案概览](#优化方案概览)
- [已实施的优化](#已实施的优化)
- [使用方式](#使用方式)
- [性能对比](#性能对比)
- [技术细节](#技术细节)
- [扩展优化](#扩展优化)

## 优化方案概览

### 方案1：并行编译（Maven模块级并行）

**效果**：加速 30-50%  
**优点**：简单配置，无需代码改动  
**缺点**：对多核 CPU 更有效

**实现方式**：
```bash
mvn clean package -T 4
```

**参数说明**：
- `-T 4`：使用4个线程并行构建模块
- `-T 1C`：每个CPU核心使用1个线程
- `-T 0.5C`：每个CPU核心使用0.5个线程

### 方案2：跳过非必要步骤

**效果**：加速 20-40%  
**优点**：立即生效  
**缺点**：失去代码覆盖率报告

**实现方式**：
```bash
mvn clean package -DskipTests -Djacoco.skip=true
```

**可跳过的步骤**：
- `-DskipTests`：跳过所有测试
- `-Djacoco.skip=true`：跳过 JaCoCo 代码覆盖率
- `-Dmaven.test.skip=true`：跳过测试编译和执行

### 方案6：缓存优化

**效果**：二次构建加速 50-80%  
**优点**：开发体验好  
**缺点**：仅对增量构建有效

**实现方式**：
```bash
# 使用 Maven 本地仓库缓存
mvn clean package -Dmaven.repo.local=./.m2/repository
```

**CI/CD 缓存配置**：
```yaml
- name: Cache Maven packages
  uses: actions/cache@v3
  with:
    path: ~/.m2
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    restore-keys: ${{ runner.os }}-m2
```

## 已实施的优化

### 1. pom.xml 配置优化

#### 构建优化属性
```xml
<properties>
    <!-- 构建优化配置 -->
    <maven.test.parallel>true</maven.test.parallel>
    <maven.test.skip>false</maven.test.skip>
    <skipTests>false</skipTests>
    <jacoco.skip>false</jacoco.skip>
</properties>
```

#### 编译器优化配置
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <fork>true</fork>
        <meminitial>512m</meminitial>
        <maxmem>1024m</maxmem>
    </configuration>
</plugin>
```

**配置说明**：
- `fork=true`：在独立进程中编译，避免内存溢出
- `meminitial=512m`：初始内存 512MB
- `maxmem=1024m`：最大内存 1GB

#### 测试并行配置
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
        <useSystemClassLoader>false</useSystemClassLoader>
    </configuration>
</plugin>
```

**配置说明**：
- `parallel=methods`：方法级并行执行
- `threadCount=4`：4个线程并行执行测试
- `perCoreThreadCount=true`：每个CPU核心使用1个线程

#### JaCoCo 可选跳过配置
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <skip>${jacoco.skip}</skip>
    </configuration>
</plugin>
```

### 2. CI/CD Pipeline 优化

#### build job 优化
```yaml
- name: Build application with optimizations
  run: |
    # 使用并行编译（4线程）和跳过 JaCoCo 加速构建
    mvn clean package -DskipTests -Djacoco.skip=true -T 4
```

#### unit-test job 优化
```yaml
- name: Run unit tests with parallel execution
  run: |
    # 使用并行编译和并行测试加速
    mvn clean test -Dspring.profiles.active=test -T 4
```

#### integration-test job 优化
```yaml
- name: Run integration tests with parallel execution
  run: |
    # 使用并行编译加速集成测试
    mvn clean verify -Dspring.profiles.active=test -Dspring.datasource.url=jdbc:mysql://localhost:3306/Junmo Platform_test -T 4
```

#### code-quality job 优化
```yaml
- name: Run Checkstyle with parallel compilation
  run: |
    mvn checkstyle:check -Dcheckstyle.config.location=checkstyle.xml -T 4

- name: Run SpotBugs
  run: |
    mvn spotbugs:check -T 4

- name: Run PMD Analysis
  run: |
    mvn pmd:check -T 4
```

## 使用方式

### 开发环境（快速迭代）

```bash
# 跳过测试和 JaCoCo，使用4线程并行构建
mvn clean package -DskipTests -Djacoco.skip=true -T 4
```

**适用场景**：
- 本地开发调试
- 频繁代码修改
- 快速验证功能

**预期效果**：
- 构建时间：7-10秒
- 加速比例：30-50%

### 测试环境（完整功能）

```bash
# 运行测试，使用4线程并行构建
mvn clean package -T 4
```

**适用场景**：
- 功能测试
- 集成测试
- 代码审查

**预期效果**：
- 构建时间：10-12秒
- 加速比例：20-30%

### 生产环境（代码覆盖率）

```bash
# 完整构建，包含代码覆盖率报告
mvn clean package
```

**适用场景**：
- 生产部署
- 代码质量检查
- 发布前验证

**预期效果**：
- 构建时间：14-16秒
- 包含完整的测试报告和覆盖率报告

### CI/CD 环境

```bash
# CI/CD 构建命令（已在 .github/workflows/ci.yml 中配置）
mvn clean package -DskipTests -Djacoco.skip=true -T 4
```

**缓存优化**：
- 首次构建：14秒
- 二次构建：3-5秒（使用缓存）
- 加速比例：50-80%

## 性能对比

### 构建时间对比

| 场景 | 原始耗时 | 优化后耗时 | 加速比例 | 说明 |
|------|---------|-----------|---------|------|
| **开发环境** | ~14秒 | ~7-10秒 | 30-50% | 跳过测试和 JaCoCo |
| **测试环境** | ~14秒 | ~10-12秒 | 20-30% | 运行测试，并行编译 |
| **生产环境** | ~14秒 | ~14-16秒 | 0% | 完整构建 |
| **CI/CD 首次** | ~14秒 | ~8-11秒 | 20-40% | 跳过测试和 JaCoCo |
| **CI/CD 二次** | ~14秒 | ~3-5秒 | 50-80% | 使用缓存 |

### 资源占用对比

| 配置 | CPU 使用率 | 内存占用 | 磁盘 I/O |
|------|-----------|---------|----------|
| **原始配置** | 低 | 低 | 低 |
| **并行编译（-T 4）** | 高 | 中 | 中 |
| **并行测试（4线程）** | 高 | 高 | 中 |
| **优化后组合** | 高 | 中 | 中 |

## 技术细节

### 并行编译原理

**Maven 模块级并行**：
- Maven 分析模块依赖关系
- 独立的模块可以同时编译
- 依赖关系决定并行度

**并行度计算**：
```
并行度 = min(线程数, 可并行模块数)
```

**示例**：
```
Junmo Platform (父模块)
├── Junmo Platform-common (可并行)
├── Junmo Platform-core (依赖 common)
├── Junmo Platform-web (依赖 core)
├── Junmo Platform-api (可并行)
├── Junmo Platform-integration (可并行)
├── Junmo Platform-admin (可并行)
├── Junmo Platform-generator (可并行)
└── Junmo Platform-starter (依赖所有模块)
```

### JaCoCo 跳过原理

**JaCoCo 执行流程**：
1. `prepare-agent`：添加 Java Agent 到 JVM
2. `test`：执行测试，收集覆盖率数据
3. `report`：生成覆盖率报告

**跳过优化**：
- 跳过 `prepare-agent`：不添加 Java Agent
- 跳过 `report`：不生成报告
- 减少开销：避免字节码插桩和报告生成

### 缓存优化原理

**Maven 依赖缓存**：
- 首次构建：下载所有依赖到本地仓库
- 二次构建：直接使用本地缓存
- 缓存键：基于 `pom.xml` 文件哈希

**GitHub Actions 缓存**：
```yaml
key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
```

**缓存策略**：
- 精确匹配：使用完整哈希值
- 部分匹配：使用前缀匹配
- 缓存失效：`pom.xml` 变更时

## 扩展优化

### 方案3：依赖优化（减小包体积）

#### 3.1 排除传递依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### 3.2 使用 thin jar

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layout>THIN</layout>
    </configuration>
</plugin>
```

**效果**：
- JAR 大小：从 80MB 减少到 10MB
- 依赖分离：依赖单独管理
- 启动方式：需要提供依赖路径

#### 3.3 依赖分析

```bash
# 分析未使用的依赖
mvn dependency:analyze

# 查看依赖树
mvn dependency:tree

# 查看依赖冲突
mvn dependency:tree -Dverbose
```

### 方案4：Docker 多阶段构建

**Dockerfile 优化**：

```dockerfile
# 构建阶段
FROM maven:3.9.12-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY Junmo Platform ./Junmo Platform
RUN mvn clean package -DskipTests -T 4

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/Junmo Platform/Junmo Platform-starter/target/Junmo Platform-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**效果**：
- 镜像大小：从 500MB 减少到 150MB
- 构建时间：首次构建较慢，后续使用缓存
- 部署速度：镜像更小，部署更快

### 方案5：GraalVM 原生镜像

**添加依赖**：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-native</artifactId>
</dependency>
```

**构建命令**：

```bash
mvn -Pnative native:compile
```

**效果**：
- 启动速度：提升 10-100倍
- 内存占用：降低 50-80%
- 构建时间：增加 5-10分钟
- 兼容性：部分功能可能不兼容

### 方案7：分层打包

**增量构建**：

```bash
# 只构建变更的模块
mvn clean install -pl Junmo Platform-web -am -DskipTests
```

**参数说明**：
- `-pl Junmo Platform-web`：只构建 web 模块
- `-am`：同时构建依赖的模块
- `-DskipTests`：跳过测试

**适用场景**：
- 只修改了某个模块
- 快速验证变更
- 减少构建时间

## 最佳实践

### 1. 开发环境

```bash
# 快速构建
mvn clean package -DskipTests -Djacoco.skip=true -T 4

# 快速运行
mvn spring-boot:run -DskipTests
```

### 2. 提交前检查

```bash
# 完整测试
mvn clean test -T 4

# 代码质量检查
mvn checkstyle:check spotbugs:check pmd:check -T 4
```

### 3. CI/CD 流程

```yaml
# 1. 代码质量检查
mvn checkstyle:check spotbugs:check pmd:check -T 4

# 2. 单元测试
mvn clean test -T 4

# 3. 集成测试
mvn clean verify -T 4

# 4. 构建应用
mvn clean package -DskipTests -Djacoco.skip=true -T 4

# 5. Docker 构建
docker build -t Junmo Platform:latest .
```

### 4. 生产部署

```bash
# 完整构建
mvn clean package

# 代码覆盖率报告
mvn jacoco:report

# 安全扫描
mvn dependency-check:check
```

## 常见问题

### Q1: 并行编译会导致内存溢出怎么办？

**A**: 调整编译器内存配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <fork>true</fork>
        <meminitial>256m</meminitial>
        <maxmem>512m</maxmem>
    </configuration>
</plugin>
```

### Q2: 并行测试会导致测试失败怎么办？

**A**: 检查测试是否线程安全：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>2</threadCount>
        <perCoreThreadCount>false</perCoreThreadCount>
    </configuration>
</plugin>
```

### Q3: 缓存未生效怎么办？

**A**: 检查缓存配置：

```bash
# 清理缓存
mvn dependency:purge-local-repository

# 强制更新依赖
mvn clean package -U

# 检查本地仓库
mvn dependency:resolve
```

### Q4: 构建速度仍然很慢怎么办？

**A**: 尝试以下优化：

```bash
# 1. 使用更少的线程
mvn clean package -T 2

# 2. 跳过更多步骤
mvn clean package -DskipTests -Djacoco.skip=true -Dcheckstyle.skip=true

# 3. 使用增量编译
mvn compile -o
```

## 总结

### 推荐配置

| 环境 | 推荐命令 | 预期效果 |
|------|---------|---------|
| **本地开发** | `mvn clean package -DskipTests -Djacoco.skip=true -T 4` | 7-10秒 |
| **功能测试** | `mvn clean test -T 4` | 10-12秒 |
| **CI/CD 构建** | `mvn clean package -DskipTests -Djacoco.skip=true -T 4` | 8-11秒 |
| **生产部署** | `mvn clean package` | 14-16秒 |

### 关键要点

1. **并行编译**：使用 `-T 4` 参数加速模块构建
2. **跳过非必要步骤**：开发环境跳过测试和 JaCoCo
3. **缓存优化**：CI/CD 使用 Maven 依赖缓存
4. **内存优化**：配置编译器 fork 模式和内存限制
5. **测试并行**：配置测试并行执行

### 扩展阅读

- [Maven 并行构建文档](https://maven.apache.org/guides/mini/guide-parallel-builds.html)
- [JaCoCo 官方文档](https://www.jacoco.org/jacoco/trunk/doc/)
- [Spring Boot 构建优化](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.build)
- [GitHub Actions 缓存](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows)

---

**文档版本**: 1.0.0  
**最后更新**: 2024-12-29  
**维护者**: Junmo Platform Team