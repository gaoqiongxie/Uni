# 轻瘦App 部署文档

## 一、环境要求

### 服务器配置
- **CPU**: 2核+
- **内存**: 4GB+
- **磁盘**: 20GB+
- **系统**: Linux (Ubuntu 20.04+ / CentOS 7+) 或 Windows Server 2019+

### 软件依赖
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Git**: 2.0+

---

## 二、快速部署

### 1. 克隆代码

```bash
git clone https://github.com/gaoqiongxie/Uni.git
cd Uni
```

### 2. 使用部署脚本

**Linux/Mac:**
```bash
chmod +x deploy.sh
./deploy.sh
# 选择选项 1) 首次部署
```

**Windows:**
```powershell
.\deploy.ps1
# 选择选项 1) 首次部署
```

### 3. 手动部署

```bash
# 创建必要目录
mkdir -p logs uploads mysql_backup

# 构建并启动
docker-compose build --no-cache
docker-compose up -d

# 查看日志
docker-compose logs -f
```

---

## 三、服务访问

部署完成后，以下服务可用：

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端API | http://localhost:8080 | REST API |
| MySQL | localhost:3306 | 数据库 |
| Redis | localhost:6379 | 缓存 |

### 测试API

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 登录接口
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'
```

---

## 四、配置说明

### 环境变量

在 `docker-compose.yml` 中可配置以下环境变量：

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `DB_HOST` | mysql | 数据库主机 |
| `DB_PORT` | 3306 | 数据库端口 |
| `DB_NAME` | uni_db | 数据库名 |
| `DB_USER` | uni_user | 数据库用户 |
| `DB_PASSWORD` | uni_pass123 | 数据库密码 |
| `REDIS_HOST` | redis | Redis主机 |
| `REDIS_PORT` | 6379 | Redis端口 |
| `REDIS_PASSWORD` | redis123456 | Redis密码 |
| `JWT_SECRET` | uni-jwt-secret-key... | JWT密钥 |

### 生产环境配置

1. **修改默认密码**
   ```yaml
   # docker-compose.yml
   environment:
     MYSQL_ROOT_PASSWORD: your_strong_password
     MYSQL_PASSWORD: your_db_password
     REDIS_PASSWORD: your_redis_password
     JWT_SECRET: your_jwt_secret_key_minimum_32_chars
   ```

2. **配置七牛云OSS** (图片上传)
   ```yaml
   environment:
     QINIU_ACCESS_KEY: your_access_key
     QINIU_SECRET_KEY: your_secret_key
     QINIU_BUCKET: your_bucket
     QINIU_DOMAIN: https://your-domain.com
   ```

3. **配置短信服务** (可选)
   ```yaml
   environment:
     SMS_ACCESS_KEY: your_sms_key
     SMS_SECRET_KEY: your_sms_secret
     SMS_TEMPLATE_CODE: SMS_XXXXXXXX
   ```

---

## 五、数据管理

### 数据备份

```bash
# 使用脚本
./deploy.sh
# 选择选项 7) 数据备份

# 或手动备份
BACKUP_DIR="./mysql_backup/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
docker exec uni-mysql mysqldump -u root -proot123456 uni_db > "$BACKUP_DIR/backup.sql"
```

### 数据恢复

```bash
# 使用脚本
./deploy.sh
# 选择选项 8) 数据恢复

# 或手动恢复
docker exec -i uni-mysql mysql -u root -proot123456 uni_db < backup.sql
```

### 导入食谱数据

```bash
# 进入MySQL容器
docker exec -it uni-mysql mysql -u root -proot123456 uni_db

# 执行食谱SQL
source /docker-entrypoint-initdb.d/init-recipe-data.sql
```

---

## 六、更新部署

### 更新代码

```bash
git pull origin master
```

### 重新构建

```bash
# 使用脚本
./deploy.sh
# 选择选项 2) 重新构建

# 或手动
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 仅更新后端

```bash
docker-compose stop backend
docker-compose build backend
docker-compose up -d backend
```

---

## 七、监控与日志

### 查看日志

```bash
# 所有服务
docker-compose logs -f

# 指定服务
docker-compose logs -f backend
docker-compose logs -f mysql
docker-compose logs -f redis
```

### 查看状态

```bash
# 服务状态
docker-compose ps

# 资源使用
docker stats
```

### 重启服务

```bash
# 重启所有
docker-compose restart

# 重启指定服务
docker-compose restart backend
```

---

## 八、故障排查

### 常见问题

#### 1. 端口被占用

```bash
# 查看端口占用
netstat -tlnp | grep 3306

# 修改docker-compose.yml中的端口映射
ports:
  - "3307:3306"  # 外部使用3307端口
```

#### 2. 内存不足

```bash
# 查看容器内存使用
docker stats

# 调整JVM内存参数
# 修改 Dockerfile 中的 -Xmx512m
```

#### 3. 数据库连接失败

```bash
# 检查MySQL是否健康
docker-compose ps

# 查看MySQL日志
docker-compose logs mysql

# 手动连接测试
docker exec -it uni-mysql mysql -u uni_user -puni_pass123 -e "SELECT 1"
```

#### 4. 后端启动失败

```bash
# 查看详细日志
docker-compose logs backend

# 进入容器检查
docker exec -it uni-backend sh
```

---

## 九、安全建议

1. **修改默认密码** - 生产环境务必修改所有默认密码
2. **使用HTTPS** - 配置Nginx反向代理和SSL证书
3. **限制端口访问** - 仅开放必要端口，数据库不暴露公网
4. **定期备份** - 设置自动备份任务
5. **日志审计** - 定期检查日志文件

---

## 十、Nginx反向代理配置 (推荐)

```nginx
server {
    listen 80;
    server_name api.qingshou.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.qingshou.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 十一、前端部署

### H5部署

```bash
cd frontend
npm install
npm run build:h5

# 将dist/build/h5目录部署到Nginx
```

### 微信小程序

```bash
cd frontend
npm install
npm run build:mp-weixin

# 使用微信开发者工具导入 dist/build/mp-weixin
```

---

## 十二、联系与支持

- **项目地址**: https://github.com/gaoqiongxie/Uni
- **问题反馈**: 提交 GitHub Issue
- **文档更新**: 2026-04-03
