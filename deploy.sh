#!/bin/bash
# 轻瘦App Docker部署脚本

set -e

echo "=================================="
echo "  轻瘦App Docker部署脚本"
echo "=================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker未安装${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose未安装${NC}"
    exit 1
fi

# 显示菜单
echo ""
echo "请选择操作:"
echo "1) 首次部署 (构建并启动所有服务)"
echo "2) 重新构建 (重新构建镜像并重启)"
echo "3) 启动服务"
echo "4) 停止服务"
echo "5) 查看日志"
echo "6) 查看状态"
echo "7) 数据备份"
echo "8) 数据恢复"
echo "q) 退出"
echo ""

read -p "请输入选项 [1-8/q]: " choice

case $choice in
    1)
        echo -e "${YELLOW}>>> 开始首次部署...${NC}"
        echo ""
        
        # 创建必要目录
        mkdir -p logs uploads mysql_backup
        
        # 构建并启动
        docker-compose build --no-cache
        docker-compose up -d
        
        echo ""
        echo -e "${GREEN}>>> 部署完成!${NC}"
        echo ""
        echo "服务地址:"
        echo "  - 后端API: http://localhost:8080"
        echo "  - MySQL: localhost:3306"
        echo "  - Redis: localhost:6379"
        echo ""
        echo "查看日志: docker-compose logs -f"
        ;;
        
    2)
        echo -e "${YELLOW}>>> 重新构建...${NC}"
        docker-compose down
        docker-compose build --no-cache
        docker-compose up -d
        echo -e "${GREEN}>>> 重建完成!${NC}"
        ;;
        
    3)
        echo -e "${YELLOW}>>> 启动服务...${NC}"
        docker-compose up -d
        echo -e "${GREEN}>>> 服务已启动!${NC}"
        ;;
        
    4)
        echo -e "${YELLOW}>>> 停止服务...${NC}"
        docker-compose down
        echo -e "${GREEN}>>> 服务已停止!${NC}"
        ;;
        
    5)
        echo -e "${YELLOW}>>> 查看日志 (按 Ctrl+C 退出)...${NC}"
        docker-compose logs -f
        ;;
        
    6)
        echo -e "${YELLOW}>>> 服务状态:${NC}"
        docker-compose ps
        echo ""
        echo -e "${YELLOW}>>> 资源使用:${NC}"
        docker stats --no-stream
        ;;
        
    7)
        BACKUP_DIR="./mysql_backup/$(date +%Y%m%d_%H%M%S)"
        mkdir -p "$BACKUP_DIR"
        echo -e "${YELLOW}>>> 备份数据到 $BACKUP_DIR ...${NC}"
        docker exec uni-mysql mysqldump -u root -proot123456 uni_db > "$BACKUP_DIR/backup.sql"
        echo -e "${GREEN}>>> 备份完成!${NC}"
        ;;
        
    8)
        echo -e "${YELLOW}>>> 可用的备份:${NC}"
        ls -la mysql_backup/
        echo ""
        read -p "请输入要恢复的备份目录名: " backup_name
        if [ -f "mysql_backup/$backup_name/backup.sql" ]; then
            echo -e "${YELLOW}>>> 恢复数据...${NC}"
            docker exec -i uni-mysql mysql -u root -proot123456 uni_db < "mysql_backup/$backup_name/backup.sql"
            echo -e "${GREEN}>>> 恢复完成!${NC}"
        else
            echo -e "${RED}错误: 备份文件不存在${NC}"
        fi
        ;;
        
    q|Q)
        echo "退出"
        exit 0
        ;;
        
    *)
        echo -e "${RED}无效选项${NC}"
        exit 1
        ;;
esac
