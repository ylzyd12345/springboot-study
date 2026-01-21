# Docker 部署指南

本文档介绍如何使用 Docker 部署 Junmo Platform 应用。

## 前置要求

- Docker 20.0+
- Docker Compose 2.0+
- 至少 4GB 可用内存
- 至少 20GB 可用磁盘空间

## 快速开始

### Windows 用户

```bash
# 1. 启动所有服务
docker-build.bat up

# 2. 查看服务状态
docker-build.bat status

# 3. 查看应用日志
docker-build.bat logs

# 4. 停止服务
docker-build.bat down
```

### Linux/Mac 用户

```bash
# 1. 赋予脚本执行权限
chmod +x docker-build.sh

# 2. 启动所有服务
./docker-build.sh up

# 3. 查看服务状态
./docker-build.sh status

# 4. 查看应用日志
./docker-build.sh logs

# 5. 停止服务
./docker-build.sh down
```

## 服务列表

| 服务名称 | 端口 | 访问地址 | 说明 |
|---------|------|---------|------|
| **应用服务** | | | |
| app | 8080 | http://localhost:8080 | Spring Boot 应用 |
| **数据库** | | | |
| mysql | 3306 | localhost:3306 | MySQL 8.0 数据库 |
| redis | 6379 | localhost:6379 | Redis 7.0 缓存 |
| elasticsearch | 9200 | http://localhost:9200 | Elasticsearch 8.11 |
| neo4j | 7474, 7687 | http://localhost:7474 | Neo4j 5.12 图数据库 |
| influxdb | 8086 | http://localhost:8086 | InfluxDB 2.7 时序数据库 |
| **消息队列** | | | |
| rabbitmq | 5672, 15672 | http://localhost:15672 | RabbitMQ 3.12 (admin/admin) |
| kafka | 9092 | - | Apache Kafka 7.4 |
| kafka-ui | 8081 | http://localhost:8081 | Kafka UI 管理界面 |
| **监控运维** | | | |
| prometheus | 9090 | http://localhost:9090 | Prometheus 监控 |
| grafana | 3000 | http://localhost:3000 | Grafana 可视化 (admin/admin) |
| node-exporter | 9100 | http://localhost:9100 | 系统监控 |
| mysql-exporter | 9104 | http://localhost:9104 | MySQL 监控 |
| redis-exporter | 9121 | http://localhost:9121 | Redis 监控 |
| zipkin | 9411 | http://localhost:9411 | Zipkin 链路追踪 |
| **可视化** | | | |
| kibana | 5601 | http://localhost:5601 | Kibana 日志分析 |
| nginx | 80, 443 | http://localhost | Nginx 负载均衡 |

## Docker 命令

### 构建镜像

```bash
# 仅构建应用镜像
docker-build.bat build

# 重新构建并启动
docker-build.bat rebuild
```

### 服务管理

```bash
# 启动所有服务
docker-build.bat up

# 停止所有服务
docker-build.bat down

# 重启所有服务
docker-build.bat restart

# 查看服务状态
docker-build.bat status

# 查看运行的容器
docker-build.bat ps
```

### 日志查看

```bash
# 查看应用日志
docker-build.bat logs

# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f mysql
docker-compose logs -f redis
docker-compose logs -f rabbitmq
```

### 清理资源

```bash
# 清理所有容器和数据卷（会删除所有数据）
docker-build.bat clean

# 仅删除容器（保留数据卷）
docker-compose down

# 删除容器和未使用的镜像
docker-compose down --rmi all
```

## 配置说明

### 环境变量

主要环境变量在 `docker-compose.yml` 中配置：

```yaml
environment:
  # Spring 配置
  SPRING_PROFILES_ACTIVE: docker
  JAVA_OPTS: "-Xms512m -Xmx1024m"
  
  # 数据库配置
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/Junmo Platform
  SPRING_DATASOURCE_USERNAME: Junmo Platform
  SPRING_DATASOURCE_PASSWORD: Junmo Platform
  
  # Redis 配置
  SPRING_DATA_REDIS_HOST: redis
  SPRING_DATA_REDIS_PORT: 6379
  
  # RabbitMQ 配置
  SPRING_RABBITMQ_HOST: rabbitmq
  SPRING_RABBITMQ_USERNAME: admin
  SPRING_RABBITMQ_PASSWORD: admin
  
  # Kafka 配置
  SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
  
  # Elasticsearch 配置
  SPRING_ELASTICSEARCH_URIS: http://elasticsearch:9200
```

### 自定义配置

1. **修改端口**：编辑 `docker-compose.yml` 中的 `ports` 映射
2. **修改资源限制**：编辑 `docker-compose.yml` 中的 `deploy.resources`
3. **修改环境变量**：编辑 `docker-compose.yml` 中的 `environment` 部分

## 数据持久化

所有数据卷在 `docker-compose.yml` 中定义：

```yaml
volumes:
  mysql_data:        # MySQL 数据
  redis_data:        # Redis 数据
  rabbitmq_data:     # RabbitMQ 数据
  kafka_data:        # Kafka 数据
  elasticsearch_data: # Elasticsearch 数据
  neo4j_data:        # Neo4j 数据
  influxdb_data:     # InfluxDB 数据
  prometheus_data:   # Prometheus 数据
  grafana_data:      # Grafana 数据
  app_logs:          # 应用日志
```

## 性能优化

### 构建优化

1. **使用 BuildKit 缓存**：Dockerfile 已启用 BuildKit
2. **多阶段构建**：减小最终镜像大小
3. **依赖缓存**：利用 Docker 层缓存

### 运行时优化

1. **JVM 参数**：已配置容器友好的 JVM 参数
   ```yaml
   JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
   ```

2. **健康检查**：所有服务都配置了健康检查

3. **资源限制**：可根据需要添加资源限制

## 故障排查

### 应用无法启动

```bash
# 查看应用日志
docker-compose logs app

# 检查服务依赖
docker-compose ps

# 检查网络连接
docker network inspect Junmo Platform_Junmo Platform-network
```

### 数据库连接失败

```bash
# 检查 MySQL 状态
docker-compose ps mysql
docker-compose logs mysql

# 测试连接
docker exec -it Junmo Platform-mysql mysql -u Junmo Platform -p
```

### 内存不足

```bash
# 查看容器资源使用
docker stats

# 减少启动的服务数量
# 编辑 docker-compose.yml，注释掉不需要的服务
```

## 生产环境部署

### 1. 修改配置

```yaml
# 使用生产环境配置
environment:
  SPRING_PROFILES_ACTIVE: prod
  
# 修改默认密码
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  MYSQL_PASSWORD: ${MYSQL_PASSWORD}
  RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD}
```

### 2. 使用 secrets 管理敏感信息

```yaml
secrets:
  mysql_password:
    file: ./secrets/mysql_password.txt
  rabbitmq_password:
    file: ./secrets/rabbitmq_password.txt

services:
  mysql:
    secrets:
      - mysql_password
```

### 3. 配置日志收集

```yaml
services:
  app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 4. 配置资源限制

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

## 备份与恢复

### 备份数据

```bash
# 备份 MySQL
docker exec Junmo Platform-mysql mysqldump -u Junmo Platform -p Junmo Platform > backup.sql

# 备份 Redis
docker exec Junmo Platform-redis redis-cli BGSAVE

# 备份 Elasticsearch
docker exec Junmo Platform-elasticsearch curl -X POST "localhost:9200/_snapshot/backup/snapshot_1"
```

### 恢复数据

```bash
# 恢复 MySQL
docker exec -i Junmo Platform-mysql mysql -u Junmo Platform -p Junmo Platform < backup.sql

# 恢复 Redis
docker cp backup.rdb Junmo Platform-redis:/data/dump.rdb
docker-compose restart redis
```

## 常见问题

### Q: 如何只启动必需的服务？

A: 编辑 `docker-compose.yml`，注释掉不需要的服务，然后运行：

```bash
docker-compose up -d mysql redis app
```

### Q: 如何修改 JVM 参数？

A: 在 `docker-compose.yml` 中修改 `JAVA_OPTS` 环境变量：

```yaml
environment:
  JAVA_OPTS: "-Xms1g -Xmx2g -XX:+UseG1GC"
```

### Q: 如何查看容器内部？

A: 使用 `docker exec` 命令：

```bash
# 进入应用容器
docker exec -it Junmo Platform-app sh

# 进入 MySQL 容器
docker exec -it Junmo Platform-mysql bash
```

### Q: 如何更新应用？

A: 重新构建并启动：

```bash
docker-build.bat rebuild
```

## 相关文档

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 支持](https://docs.spring.io/spring-boot/docs/current/reference/html/docker.html)

## 联系方式

如有问题，请提交 Issue 或联系项目维护者。