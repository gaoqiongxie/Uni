# 轻瘦App Docker部署脚本 (PowerShell)

$ErrorActionPreference = "Stop"

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  轻瘦App Docker部署脚本" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# 检查Docker是否安装
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "错误: Docker未安装" -ForegroundColor Red
    exit 1
}

if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Host "错误: Docker Compose未安装" -ForegroundColor Red
    exit 1
}

# 显示菜单
Write-Host ""
Write-Host "请选择操作:"
Write-Host "1) 首次部署 (构建并启动所有服务)"
Write-Host "2) 重新构建 (重新构建镜像并重启)"
Write-Host "3) 启动服务"
Write-Host "4) 停止服务"
Write-Host "5) 查看日志"
Write-Host "6) 查看状态"
Write-Host "7) 数据备份"
Write-Host "8) 数据恢复"
Write-Host "q) 退出"
Write-Host ""

$choice = Read-Host "请输入选项 [1-8/q]"

switch ($choice) {
    "1" {
        Write-Host ">>> 开始首次部署..." -ForegroundColor Yellow
        Write-Host ""
        
        # 创建必要目录
        New-Item -ItemType Directory -Force -Path "logs", "uploads", "mysql_backup" | Out-Null
        
        # 构建并启动
        docker-compose build --no-cache
        docker-compose up -d
        
        Write-Host ""
        Write-Host ">>> 部署完成!" -ForegroundColor Green
        Write-Host ""
        Write-Host "服务地址:"
        Write-Host "  - 后端API: http://localhost:8080"
        Write-Host "  - MySQL: localhost:3306"
        Write-Host "  - Redis: localhost:6379"
        Write-Host ""
        Write-Host "查看日志: docker-compose logs -f"
    }
    
    "2" {
        Write-Host ">>> 重新构建..." -ForegroundColor Yellow
        docker-compose down
        docker-compose build --no-cache
        docker-compose up -d
        Write-Host ">>> 重建完成!" -ForegroundColor Green
    }
    
    "3" {
        Write-Host ">>> 启动服务..." -ForegroundColor Yellow
        docker-compose up -d
        Write-Host ">>> 服务已启动!" -ForegroundColor Green
    }
    
    "4" {
        Write-Host ">>> 停止服务..." -ForegroundColor Yellow
        docker-compose down
        Write-Host ">>> 服务已停止!" -ForegroundColor Green
    }
    
    "5" {
        Write-Host ">>> 查看日志 (按 Ctrl+C 退出)..." -ForegroundColor Yellow
        docker-compose logs -f
    }
    
    "6" {
        Write-Host ">>> 服务状态:" -ForegroundColor Yellow
        docker-compose ps
        Write-Host ""
        Write-Host ">>> 资源使用:" -ForegroundColor Yellow
        docker stats --no-stream
    }
    
    "7" {
        $backupDir = "./mysql_backup/$(Get-Date -Format 'yyyyMMdd_HHmmss')"
        New-Item -ItemType Directory -Force -Path $backupDir | Out-Null
        Write-Host ">>> 备份数据到 $backupDir ..." -ForegroundColor Yellow
        docker exec uni-mysql mysqldump -u root -proot123456 uni_db > "$backupDir/backup.sql"
        Write-Host ">>> 备份完成!" -ForegroundColor Green
    }
    
    "8" {
        Write-Host ">>> 可用的备份:" -ForegroundColor Yellow
        Get-ChildItem mysql_backup/ | Select-Object Name, LastWriteTime
        Write-Host ""
        $backupName = Read-Host "请输入要恢复的备份目录名"
        $backupFile = "mysql_backup/$backupName/backup.sql"
        if (Test-Path $backupFile) {
            Write-Host ">>> 恢复数据..." -ForegroundColor Yellow
            Get-Content $backupFile | docker exec -i uni-mysql mysql -u root -proot123456 uni_db
            Write-Host ">>> 恢复完成!" -ForegroundColor Green
        } else {
            Write-Host "错误: 备份文件不存在" -ForegroundColor Red
        }
    }
    
    "q" { exit 0 }
    "Q" { exit 0 }
    default {
        Write-Host "无效选项" -ForegroundColor Red
        exit 1
    }
}
