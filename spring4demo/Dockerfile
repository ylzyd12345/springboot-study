# 使用多阶段构建
FROM maven:3.9.12-openjdk-21-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制pom文件
COPY pom.xml .
COPY spring4demo-common/pom.xml ./spring4demo-common/
COPY spring4demo-core/pom.xml ./spring4demo-core/
COPY spring4demo-web/pom.xml ./spring4demo-web/
COPY spring4demo-api/pom.xml ./spring4demo-api/
COPY spring4demo-admin/pom.xml ./spring4demo-admin/
COPY spring4demo-integration/pom.xml ./spring4demo-integration/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY spring4demo-common/src ./spring4demo-common/src
COPY spring4demo-core/src ./spring4demo-core/src
COPY spring4demo-web/src ./spring4demo-web/src
COPY spring4demo-api/src ./spring4demo-api/src
COPY spring4demo-admin/src ./spring4demo-admin/src
COPY spring4demo-integration/src ./spring4demo-integration/src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:21-jre-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 设置工作目录
WORKDIR /app

# 复制jar文件
COPY --from=builder /app/spring4demo-web/target/spring4demo-web-*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# 切换到应用用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]