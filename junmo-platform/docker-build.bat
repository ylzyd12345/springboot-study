@echo off
REM Junmo Platform Docker 构建和部署脚本 (Windows)

setlocal enabledelayedexpansion

set PROJECT_NAME=junmo-platform

if "%1"=="" goto help
if "%1"=="help" goto help
if "%1"=="--help" goto help
if "%1"=="-h" goto help

if "%1"=="build" goto build
if "%1"=="up" goto up
if "%1"=="down" goto down
if "%1"=="restart" goto restart
if "%1"=="logs" goto logs
if "%1"=="clean" goto clean
if "%1"=="rebuild" goto rebuild
if "%1"=="status" goto status
if "%1"=="ps" goto ps

echo [错误] 未知命令: %1
echo.
goto help

:build
echo [构建] 构建应用镜像...
docker-compose build app
echo [完成] 构建完成！
goto end

:up
echo [启动] 启动所有服务...
docker-compose up -d
echo [完成] 服务已启动！
echo.
echo [访问地址]
echo   - 应用: http://localhost:8080
echo   - Prometheus: http://localhost:9090
echo   - Grafana: http://localhost:3000 (admin/admin)
echo   - Loki: http://localhost:3100
echo   - RabbitMQ: http://localhost:15672 (admin/admin)
echo   - Kafka UI: http://localhost:8081
echo   - Kibana: http://localhost:5601
echo   - Nginx: http://localhost
echo.
goto end

:down
echo [停止] 停止所有服务...
docker-compose down
echo [完成] 服务已停止！
goto end

:restart
echo [重启] 重启所有服务...
docker-compose restart
echo [完成] 服务已重启！
goto end

:logs
docker-compose logs -f app
goto end

:clean
echo [警告] 清理所有容器和卷...
set /p confirm="确定要删除所有容器和数据卷吗？(y/n): "
if /i "%confirm%"=="y" (
    docker-compose down -v
    echo [完成] 清理完成！
) else (
    echo [取消] 取消清理操作
)
goto end

:rebuild
echo [重建] 重新构建并启动...
docker-compose up -d --build app
echo [完成] 重新构建完成！
goto end

:status
echo [状态] 服务状态:
docker-compose ps
goto end

:ps
echo [容器] 运行的容器:
docker ps --filter "name=%PROJECT_NAME%"
goto end

:help
echo Junmo Platform Docker 管理脚本 (Windows)
echo.
echo 用法: docker-build.bat [命令]
echo.
echo 命令:
echo   build        构建应用镜像
echo   up           启动所有服务
echo   down         停止所有服务
echo   restart      重启所有服务
echo   logs         查看应用日志
echo   clean        清理所有容器和卷
echo   rebuild      重新构建并启动
echo   status       查看服务状态
echo   ps           查看运行的容器
echo   help         显示此帮助信息
echo.
goto end

:end
endlocal