# 使用多阶段构建
FROM maven:3.9.12-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /app

# 设置 Maven 仓库缓存目录
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# 复制 pom 文件（先复制根 pom 和所有模块 pom）
COPY pom.xml .
COPY junmo-common/pom.xml ./junmo-common/
COPY junmo-core/pom.xml ./junmo-core/
COPY junmo-starter/pom.xml ./junmo-starter/
COPY junmo-api/pom.xml ./junmo-api/
COPY junmo-admin-starter/pom.xml ./junmo-admin-starter/
COPY junmo-integration/pom.xml ./junmo-integration/
COPY junmo-generator/pom.xml ./junmo-generator/
COPY junmo-base/pom.xml ./junmo-base/
COPY junmo-model/pom.xml ./junmo-model/
COPY junmo-test-support/pom.xml ./junmo-test-support/

# 下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B -DskipTests

# 复制源代码
COPY junmo-common/src ./junmo-common/src
COPY junmo-core/src ./junmo-core/src
COPY junmo-starter/src ./junmo-starter/src
COPY junmo-api/src ./junmo-api/src
COPY junmo-admin-starter/src ./junmo-admin-starter/src
COPY junmo-integration/src ./junmo-integration/src
COPY junmo-generator/src ./junmo-generator/src
COPY junmo-base/src ./junmo-base/src
COPY junmo-model/src ./junmo-model/src
COPY junmo-test-support/src ./junmo-test-support/src

# 构建应用（跳过测试，使用并行构建）
RUN mvn clean package -DskipTests -Djacoco.skip=true -T 4

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

# 安装必要的工具
RUN apk add --no-cache \
    curl \
    tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 创建应用用户
RUN addgroup -S appuser && adduser -S -G appuser appuser

# 设置工作目录
WORKDIR /app

# 复制 jar 文件（从 starter 模块）
COPY --from=builder /app/junmo-starter/target/junmo-platform-*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# 切换到应用用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数（优化容器环境）
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+PrintGCDetails -XX:+PrintGCDateStamps"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]