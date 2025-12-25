# CI/CD 流水线使用指南

## 🔄 CI/CD 概述

本项目配置了完整的CI/CD流水线，支持自动化构建、测试、安全扫描、部署和发布。

## 📋 流水线工作流

### 1. 主流水线 (CI/CD Pipeline)
**文件**: `.github/workflows/ci.yml`

**触发条件**:
- 推送到 `main` 或 `develop` 分支
- 创建Pull Request到 `main` 或 `develop` 分支

**执行阶段**:
1. **代码质量检查**: Checkstyle、SpotBugs、PMD分析
2. **单元测试**: 运行所有单元测试，生成覆盖率报告
3. **集成测试**: 使用Testcontainers运行集成测试
4. **安全扫描**: Trivy、OWASP依赖检查
5. **构建应用**: Maven打包
6. **Docker构建**: 构建并推送Docker镜像
7. **部署**: 根据分支部署到对应环境
8. **通知**: 发送构建结果通知

### 2. 代码质量流水线 (Code Quality)
**文件**: `.github/workflows/code-quality.yml`

**检查内容**:
- 代码格式检查 (Google Java Format)
- 静态代码分析 (SonarCloud、SpotBugs)
- 性能测试 (JMeter)
- 代码覆盖率检查

### 3. 安全扫描流水线 (Security Scan)
**文件**: `.github/workflows/security.yml`

**安全检查**:
- 依赖安全扫描 (OWASP Dependency Check、Snyk)
- 容器安全扫描 (Trivy、Grype)
- 代码安全分析 (CodeQL、Semgrep)
- 密钥泄露检测 (Gitleaks、TruffleHog)

### 4. 发布流水线 (Release)
**文件**: `.github/workflows/release.yml`

**发布流程**:
- 创建GitHub Release
- 构建和测试
- 构建Docker镜像
- 上传发布资产
- 部署到生产环境
- 冒烟测试和健康检查

### 5. 依赖更新流水线 (Dependency Update)
**文件**: `.github/workflows/dependency-update.yml`

**自动化更新**:
- 每周一自动检查Maven依赖更新
- 检查安全漏洞
- 更新Docker基础镜像
- 创建Pull Request

## 🔧 配置说明

### 环境变量配置

在GitHub仓库设置中配置以下Secrets:

| Secret名称 | 说明 |
|-----------|------|
| `GITHUB_TOKEN` | GitHub访问令牌 (自动提供) |
| `SONAR_TOKEN` | SonarCloud访问令牌 |
| `SNYK_TOKEN` | Snyk安全扫描令牌 |

### 环境配置

配置以下环境:

| 环境名称 | 用途 | 分支 |
|---------|------|------|
| `development` | 开发环境 | `develop` |
| `production` | 生产环境 | `main` |

## 🚀 使用指南

### 1. 触发构建

**自动触发**:
- 推送代码到 `main` 或 `develop` 分支
- 创建Pull Request
- 创建版本标签

**手动触发**:
- 在GitHub Actions页面手动运行工作流

### 2. 查看构建状态

在GitHub仓库的Actions页面查看:
- 工作流执行状态
- 构建日志
- 测试结果
- 代码覆盖率报告

### 3. 代码质量检查

**本地运行**:
```bash
# 代码格式检查
mvn fmt:check

# 代码格式化
mvn fmt:format

# Checkstyle检查
mvn checkstyle:check

# SpotBugs检查
mvn spotbugs:check

# PMD检查
mvn pmd:check

# 依赖安全检查
mvn dependency-check:check
```

### 4. 发布新版本

**步骤**:
1. 更新版本号
2. 创建版本标签:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
3. 自动触发发布流水线
4. 查看发布结果

### 5. 依赖更新

**手动更新**:
```bash
# 更新依赖版本
mvn versions:use-latest-releases

# 更新插件版本
mvn versions:use-latest-plugins

# 提交更新
git add pom.xml
git commit -m "Update dependencies"
git push
```

## 📊 报告和指标

### 1. 测试报告
- **单元测试**: Maven Surefire报告
- **集成测试**: Maven Failsafe报告
- **覆盖率**: JaCoCo HTML报告

### 2. 代码质量报告
- **SonarCloud**: 代码质量分析
- **SpotBugs**: 潜在bug检查
- **PMD**: 代码规范检查

### 3. 安全报告
- **OWASP**: 依赖漏洞报告
- **Trivy**: 容器安全报告
- **Gitleaks**: 密钥泄露报告

## 🔍 故障排查

### 1. 构建失败
- 检查构建日志
- 验证代码格式
- 确认测试通过
- 检查依赖版本

### 2. 测试失败
- 查看测试报告
- 检查测试环境
- 验证测试数据
- 确认测试配置

### 3. 安全扫描失败
- 查看安全报告
- 更新有漏洞的依赖
- 修复安全配置
- 重新运行扫描

### 4. 部署失败
- 检查部署日志
- 验证环境配置
- 确认服务可用性
- 检查网络连接

## 🛠️ 自定义配置

### 1. 修改构建配置
编辑 `.github/workflows/ci.yml` 文件:
- 修改构建步骤
- 调整环境变量
- 更新Docker配置

### 2. 添加新的检查
在相应工作流文件中添加新步骤:
- 代码检查工具
- 测试类型
- 安全扫描工具

### 3. 配置通知
在工作流中添加通知步骤:
- Slack通知
- 邮件通知
- 钉钉通知

## 📈 最佳实践

1. **保持构建快速**: 优化构建步骤，使用缓存
2. **并行执行**: 独立任务并行运行
3. **失败快速反馈**: 早期失败，快速反馈
4. **环境隔离**: 不同环境独立配置
5. **安全性**: 使用Secrets保护敏感信息
6. **监控**: 监控构建性能和成功率
7. **文档**: 保持文档更新

## 🔗 相关链接

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [SonarCloud](https://sonarcloud.io/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [Trivy](https://github.com/aquasecurity/trivy)