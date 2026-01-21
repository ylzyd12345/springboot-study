# Windows Docker Desktop 快速部署指南

本文档介绍如何在 Windows 系统上使用 Docker Desktop 快速部署 Junmo Platform 项目。

## 📋 前置要求

### 必需软件

1. **Docker Desktop for Windows**
   - 下载地址：https://www.docker.com/products/docker-desktop
   - 版本要求：4.15.0 或更高
   - 确保已启用 WSL 2 后端

2. **Git**（可选，用于克隆项目）
   - 下载地址：https://git-scm.com/download/win

3. **Java 21**（可选，用于本地开发）
   - 下载地址：https://adoptium.net/

### 系统要求

- Windows 10/11 专业版或企业版
- 启用 WSL 2 功能
- 至少 8GB RAM
- 至少 50GB 可用磁盘空间

## 🚀 快速开始

### 方式一：使用脚本一键部署（推荐）

#### 1. 打开 PowerShell 或 CMD

```powershell
# 进入项目目录
cd F:\codes\roadmap\github-project\springboot-study\Junmo Platform

# 运行部署脚本
docker-build.bat up
```

#### 2. 等待服务启动

首次启动需要下载镜像，大约需要 5-10 分钟。

#### 3. 验证服务状态

```powershell
# 查看服务状态
docker-build.bat status

# 或使用 Docker Desktop 查看容器
```

### 方式二：使用 Docker Desktop 图形界面

#### 1. 打开 Docker Desktop

确保 Docker Desktop 已启动并运行。

#### 2. 打开项目目录

```powershell
cd F:\codes\roadmap\github-project\springboot-study\Junmo Platform
```

#### 3. 使用 Docker Compose 启动

```powershell
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f app
```

#### 4. 在 Docker Desktop 中查看

打开 Docker Desktop，点击左侧的 **Containers** 标签，可以看到所有运行的容器。

## 🔧 Docker Desktop 配置

### 1. 启用 WSL 2 集成

1. 打开 Docker Desktop
2. 点击右上角 ⚙️ **Settings**
3. 选择 **General**
4. 勾选 **Use the WSL 2 based engine**
5. 点击 **Apply & Restart**

### 2. 配置资源限制

1. 打开 Docker Desktop Settings
2. 选择 **Resources**
3. **Memory**：建议设置为 8GB 或更高
4. **CPUs**：建议设置为 4 核或更高
5. **Disk image size**：建议设置为 50GB 或更高
6. 点击 **Apply & Restart**

### 3. 配置文件共享

1. 打开 Docker Desktop Settings
2. 选择 **Resources** → **File sharing**
3. 添加项目目录：`F:\codes\roadmap\github-project\springboot-study`
4. 点击 **Apply & Restart**

## 📂 项目关联方式

### 方式一：直接使用 docker-compose.yml

项目已包含 `docker-compose.yml`，直接在项目目录运行即可：

```powershell
cd F:\codes\roadmap\github-project\springboot-study\Junmo Platform
docker-compose up -d
```

### 方式二：使用 Docker Desktop 的 Dev Environments（推荐）

1. 打开 Docker Desktop
2. 点击左侧 **Dev Environments**
3. 点击 **Create**
4. 选择 **Local**
5. 浏览到项目目录：`F:\codes\roadmap\github-project\springboot-study\Junmo Platform`
6. 点击 **Create**

Docker Desktop 会自动识别 `docker-compose.yml` 并创建开发环境。

### 方式三：使用 VS Code + Docker 扩展

1. 安装 VS Code
2. 安装 Docker 扩展
3. 打开项目目录
4. 右键点击 `docker-compose.yml`
5. 选择 **Compose Up**

## 🎯 常用命令

### Windows PowerShell 命令

```powershell
# 进入项目目录
cd F:\codes\roadmap\github-project\springboot-study\Junmo Platform

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f app

# 停止所有服务
docker-compose down

# 重启服务
docker-compose restart app

# 查看容器资源使用
docker stats

# 进入容器
docker exec -it Junmo Platform-app sh

# 重新构建并启动
docker-compose up -d --build app
```

### 使用批处理脚本

```powershell
# 启动服务
docker-build.bat up

# 查看状态
docker-build.bat status

# 查看日志
docker-build.bat logs

# 停止服务
docker-build.bat down

# 重新构建
docker-build.bat rebuild

# 清理所有
docker-build.bat clean
```

## 🌐 访问服务

启动成功后，可以通过以下地址访问服务：

| 服务 | 地址 | 说明 |
|------|------|------|
| **应用** | http://localhost:8080 | Spring Boot 应用 |
| **Prometheus** | http://localhost:9090 | 监控指标 |
| **Grafana** | http://localhost:3000 | 可视化面板 (admin/admin) |
| **Loki** | http://localhost:3100 | 日志存储 API（日志采集查看） |
| **Promtail** | http://localhost:9080 | 日志采集代理 |
| **RabbitMQ** | http://localhost:15672 | 消息队列管理 (admin/admin) |
| **Kafka UI** | http://localhost:8081 | Kafka 管理界面 |
| **Kibana** | http://localhost:5601 | Elasticsearch 可视化（业务文档检索） |
| **Nginx** | http://localhost | 负载均衡 |

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

## 🔍 在 Docker Desktop 中查看

### 1. 查看容器

1. 打开 Docker Desktop
2. 点击左侧 **Containers** 标签
3. 可以看到所有运行的容器
4. 点击容器可以查看日志、资源使用等信息

### 2. 查看日志

1. 在容器列表中点击 `Junmo Platform-app`
2. 点击 **Logs** 标签
3. 可以实时查看应用日志

### 3. 查看资源使用

1. 在容器列表中点击 `Junmo Platform-app`
2. 点击 **Stats** 标签
3. 可以查看 CPU、内存、网络等资源使用情况

### 4. 访问终端

1. 在容器列表中点击 `Junmo Platform-app`
2. 点击 **Console** 标签
3. 点击 **CLI** 按钮可以进入容器终端

## ⚡ 性能优化

### 1. 使用 WSL 2 后端

确保使用 WSL 2 后端，性能比 Hyper-V 更好：

```powershell
# 检查 WSL 版本
wsl --version

# 如果版本过低，更新 WSL
wsl --update
```

### 2. 配置 Docker 资源

在 Docker Desktop Settings 中：
- Memory: 8GB+
- CPUs: 4+
- Disk: 50GB+

### 3. 启用 Docker BuildKit

在 Docker Desktop Settings 中：
1. 打开 **Docker Engine**
2. 添加以下配置：

```json
{
  "features": {
    "buildkit": true
  },
  "builder": {
    "gc": {
      "enabled": true,
      "defaultKeepStorage": "10GB"
    }
  }
}
```

### 4. 使用镜像加速

在中国大陆，建议使用镜像加速：

1. 打开 Docker Desktop Settings
2. 选择 **Docker Engine**
3. 添加以下配置：

```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```

## 🐛 故障排查

### 问题 1：Docker Desktop 无法启动

**解决方案**：
```powershell
# 重启 Docker Desktop
wsl --shutdown
# 然后重新启动 Docker Desktop
```

### 问题 2：容器启动失败

**解决方案**：
```powershell
# 查看详细日志
docker-compose logs app

# 检查容器状态
docker-compose ps

# 重启服务
docker-compose restart
```

### 问题 3：端口冲突

**解决方案**：
```powershell
# 查看端口占用
netstat -ano | findstr :8080

# 修改 docker-compose.yml 中的端口映射
```

### 问题 4：内存不足

**解决方案**：
1. 在 Docker Desktop Settings 中增加内存
2. 或者减少启动的服务数量

```powershell
# 只启动必需服务
docker-compose up -d mysql redis app
```

### 问题 5：文件共享失败

**解决方案**：
1. 检查 Docker Desktop 的文件共享配置
2. 确保项目目录已添加到共享列表
3. 重启 Docker Desktop

## 📦 部署流程

### 开发环境部署

```powershell
# 1. 进入项目目录
cd F:\codes\roadmap\github-project\springboot-study\Junmo Platform

# 2. 启动所有服务
docker-build.bat up

# 3. 等待服务启动完成
# 首次启动需要 5-10 分钟

# 4. 验证服务
docker-build.bat status

# 5. 访问应用
# 浏览器打开 http://localhost:8080
```

### 生产环境部署

```powershell
# 1. 修改配置
# 编辑 docker-compose.yml
# 修改环境变量、密码等

# 2. 构建生产镜像
docker-compose build --no-cache app

# 3. 启动服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app

# 5. 健康检查
curl http://localhost:8080/actuator/health
```

## 🔐 安全配置

### 1. 修改默认密码

编辑 `docker-compose.yml`，修改以下密码：

```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    MYSQL_PASSWORD: ${MYSQL_PASSWORD}

rabbitmq:
  environment:
    RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD}

grafana:
  environment:
    GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
```

### 2. 使用环境变量文件

创建 `.env` 文件：

```env
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_PASSWORD=your_secure_password
RABBITMQ_PASSWORD=your_secure_password
GRAFANA_PASSWORD=your_secure_password
```

修改 `docker-compose.yml`：

```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    MYSQL_PASSWORD: ${MYSQL_PASSWORD}
```

### 3. 限制容器权限

```yaml
services:
  app:
    security_opt:
      - no-new-privileges:true
    read_only: true
    tmpfs:
      - /tmp
```

## 📊 监控和日志

### 1. Prometheus 监控

访问 http://localhost:9090 查看 Prometheus 指标。

### 2. Grafana 可视化

访问 http://localhost:3000 查看 Grafana 仪表板。

### 3. 查看日志

```powershell
# 查看应用日志
docker-compose logs -f app

# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f mysql
docker-compose logs -f redis
```

### 4. 日志文件

应用日志存储在容器内 `/app/logs` 目录，通过 volume 挂载到主机。

## 🔄 更新和重启

### 更新应用代码

```powershell
# 1. 停止服务
docker-compose down

# 2. 重新构建
docker-compose build app

# 3. 启动服务
docker-compose up -d
```

### 快速重启

```powershell
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart app
```

## 📚 相关资源

- [Docker Desktop 官方文档](https://docs.docker.com/desktop/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [WSL 2 文档](https://docs.microsoft.com/en-us/windows/wsl/)
- [项目 Docker 文档](./DOCKER.md)

## 💡 最佳实践

1. **使用 WSL 2**：性能更好，资源占用更少
2. **合理配置资源**：根据实际需求调整内存和 CPU
3. **使用镜像加速**：提高镜像下载速度
4. **定期清理**：清理未使用的镜像和容器
5. **监控资源使用**：定期检查 Docker Desktop 的资源占用
6. **使用环境变量**：避免在配置文件中硬编码敏感信息
7. **备份重要数据**：定期备份数据库和配置文件

## 🆘 获取帮助

如果遇到问题：

1. 查看 [DOCKER.md](./DOCKER.md) 获取详细文档
2. 查看 Docker Desktop 日志
3. 检查容器日志
4. 提交 Issue 到项目仓库

---

**祝部署顺利！** 🎉