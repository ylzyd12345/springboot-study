#!/bin/bash

# Junmo Platform Docker 构建和部署脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目名称
PROJECT_NAME="junmo-platform"

# 显示帮助信息
show_help() {
    echo "Junmo Platform Docker 管理脚本"
    echo ""
    echo "用法: ./docker-build.sh [命令]"
    echo ""
    echo "命令:"
    echo "  build        构建应用镜像"
    echo "  up           启动所有服务"
    echo "  down         停止所有服务"
    echo "  restart      重启所有服务"
    echo "  logs         查看应用日志"
    echo "  clean        清理所有容器和卷"
    echo "  rebuild      重新构建并启动"
    echo "  status       查看服务状态"
    echo "  ps           查看运行的容器"
    echo "  help         显示此帮助信息"
    echo ""
}

# 构建镜像
build_image() {
    echo -e "${GREEN}构建应用镜像...${NC}"
    docker-compose build app
    echo -e "${GREEN}构建完成！${NC}"
}

# 启动服务
start_services() {
    echo -e "${GREEN}启动所有服务...${NC}"
    docker-compose up -d
    echo -e "${GREEN}服务已启动！${NC}"
    echo ""
    echo -e "${YELLOW}访问地址:${NC}"
    echo "  - 应用: http://localhost:8080"
    echo "  - Prometheus: http://localhost:9090"
    echo "  - Grafana: http://localhost:3000 (admin/admin)"
    echo "  - Loki: http://localhost:3100"
    echo "  - RabbitMQ: http://localhost:15672 (admin/admin)"
    echo "  - Kafka UI: http://localhost:8081"
    echo "  - Kibana: http://localhost:5601"
    echo "  - Nginx: http://localhost"
    echo ""
}

# 停止服务
stop_services() {
    echo -e "${YELLOW}停止所有服务...${NC}"
    docker-compose down
    echo -e "${GREEN}服务已停止！${NC}"
}

# 重启服务
restart_services() {
    echo -e "${YELLOW}重启所有服务...${NC}"
    docker-compose restart
    echo -e "${GREEN}服务已重启！${NC}"
}

# 查看日志
view_logs() {
    docker-compose logs -f app
}

# 清理所有
clean_all() {
    echo -e "${RED}清理所有容器和卷...${NC}"
    read -p "确定要删除所有容器和数据卷吗？(y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]
    then
        docker-compose down -v
        echo -e "${GREEN}清理完成！${NC}"
    else
        echo -e "${YELLOW}取消清理操作${NC}"
    fi
}

# 重新构建并启动
rebuild_and_start() {
    echo -e "${GREEN}重新构建并启动...${NC}"
    docker-compose up -d --build app
    echo -e "${GREEN}重新构建完成！${NC}"
}

# 查看服务状态
show_status() {
    echo -e "${GREEN}服务状态:${NC}"
    docker-compose ps
}

# 查看运行的容器
show_ps() {
    echo -e "${GREEN}运行的容器:${NC}"
    docker ps --filter "name=${PROJECT_NAME}"
}

# 主函数
main() {
    case "$1" in
        build)
            build_image
            ;;
        up)
            start_services
            ;;
        down)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        logs)
            view_logs
            ;;
        clean)
            clean_all
            ;;
        rebuild)
            rebuild_and_start
            ;;
        status)
            show_status
            ;;
        ps)
            show_ps
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            echo -e "${RED}未知命令: $1${NC}"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"