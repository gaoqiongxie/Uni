# 轻瘦 (uni) 快速启动指南

## 项目结构

```
uni/
├── backend/               # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/uni/
│   │       │   ├── UniApplication.java  # 启动类
│   │       │   ├── common/              # 公共模块 (Result, BizCode, Exception)
│   │       │   ├── config/              # 配置类 (MybatisPlus, CORS, Security)
│   │       │   ├── controller/          # 控制器
│   │       │   ├── dto/                 # 入参 DTO
│   │       │   ├── entity/              # 数据库实体
│   │       │   ├── mapper/              # Mapper 接口
│   │       │   ├── service/             # Service 接口 + 实现
│   │       │   ├── util/                # 工具类 (JwtUtil)
│   │       │   └── vo/                  # 出参 VO
│   │       └── resources/
│   │           ├── application.yml      # 开发配置
│   │           ├── application-prod.yml # 生产配置
│   │           └── db/init.sql          # 建表 SQL
│   ├── Dockerfile
│   └── pom.xml
├── frontend/              # Uni-app 前端
│   ├── src/
│   │   ├── api/           # 接口封装
│   │   ├── pages/         # 所有页面
│   │   │   ├── user/      # login / register / profile
│   │   │   ├── health/    # record-weight / weight-history
│   │   │   ├── meal/      # meal-record / meal-calendar
│   │   │   ├── dashboard/ # 数据看板
│   │   │   └── settings/  # 设置
│   │   ├── store/         # Pinia 状态管理
│   │   ├── types/         # TypeScript 类型定义
│   │   └── utils/         # 工具函数
│   ├── pages.json         # 页面路由配置
│   ├── vite.config.ts
│   └── package.json
└── docker-compose.yml     # 一键部署
```

## 本地开发启动

### 1. 启动基础服务 (MySQL + Redis)

```bash
# 只启动 mysql 和 redis
docker-compose up -d mysql redis

# 等待服务健康检查通过（约 30s）
docker-compose ps
```

### 2. 后端启动

```bash
cd backend

# 安装依赖并启动（需要 JDK 17+、Maven 3.8+）
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/uni-app-1.0.0.jar
```

后端默认端口：**8080**

### 3. 前端启动

```bash
cd frontend

# 安装依赖（需要 Node.js 18+）
npm install

# 微信小程序开发（需要 HBuilderX 或微信开发者工具）
npm run dev:mp-weixin

# H5 Web 开发预览
npm run dev:h5
```

前端开发服务：**http://localhost:5173**

## 一键 Docker 部署

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看日志
docker-compose logs -f backend

# 停止所有服务
docker-compose down
```

## 数据库配置

| 参数 | 开发环境默认值 | 说明 |
|------|--------------|------|
| host | localhost | 主机 |
| port | 3306 | 端口 |
| database | uni_db | 数据库名 |
| username | root | 用户名（开发） |
| password | root123456 | 密码（开发） |

数据库表会在应用启动时通过 `db/init.sql` 自动初始化。

## API 文档

启动后端后访问 Swagger UI（如果开启）：
```
http://localhost:8080/swagger-ui/index.html
```

主要接口：

| 模块 | 路径前缀 |
|------|---------|
| 用户 | /api/user |
| 体重记录 | /api/weight-record |
| 餐食打卡 | /api/meal-record |
| 附件上传 | /api/attachment |

## 关键配置修改

生产部署前，请修改以下配置（`docker-compose.yml` 或环境变量）：

- `MYSQL_PASSWORD` / `DB_PASSWORD`：数据库密码
- `REDIS_PASSWORD`：Redis 密码  
- `JWT_SECRET`：JWT 密钥（建议 32 位以上随机字符串）

## 技术栈

| 端 | 技术 |
|----|-----|
| 后端 | Spring Boot 3.x + MyBatis-Plus 3.5.5 + JWT |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7.0 |
| 前端 | Uni-app (Vue3 + TypeScript) + Pinia |
| 部署 | Docker + docker-compose |
