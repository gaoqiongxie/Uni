# 轻瘦 (QingShou) - 智能健康减脂陪伴应用

> 不节食、不过度运动，用科学的方式享受轻松的健康生活

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![Uni-app](https://img.shields.io/badge/Uni-app-3.x-blue.svg)](https://uniapp.dcloud.net.cn/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🌟 项目愿景

**轻瘦** 是一款专注于科学减脂的健康管理应用，致力于为用户提供：

- 📊 **数据驱动** - 精准记录体重、体脂、饮食、运动数据，洞察身体变化
- 🤖 **AI 智能** - 基于用户画像生成个性化减脂计划，智能陪伴每一步
- 🎯 **目标导向** - 设定合理目标，可视化进度，保持动力
- 🤝 **社交激励** - 打卡挑战、好友互动，让减脂不再孤单

---

## ✨ 已实现功能

| 模块 | 功能描述 | 页面 |
|------|----------|------|
| 👤 **用户体系** | 登录/注册、个人资料管理 | login, register, profile |
| ⚖️ **体重管理** | 记录体重、查看历史、体重目标设定 | record-weight, weight-history, goal-set |
| 🍽️ **餐食打卡** | 拍照/文字记录每餐、打卡日历 | meal-record, meal-calendar |
| 🏃 **运动记录** | 添加运动项目、查看运动历史、消耗统计 | exercise-record, exercise-add, exercise-list |
| 📖 **食谱推荐** | 分类浏览、搜索、收藏、营养详情 | recipe-list, recipe-detail |
| 🤖 **AI 助手** | 用户画像收集、智能分析、个性化计划生成、AI 对话 | profile-setup, ai-home, ai-chat |
| 🎨 **主题设置** | 深色/浅色/跟随系统、主题色、字体大小 | theme-setting |
| 📈 **数据看板** | 热量收支、趋势分析、可视化图表 | dashboard |
| ⚙️ **个人中心** | 账户管理、设置入口 | mine, settings |

### 🔄 Sprint 开发进度

| Sprint | 主题 | 完成日期 |
|--------|------|----------|
| Sprint 1-8 | 核心功能 MVP | ✅ 已完成 |
| Sprint 9 | 深色模式主题系统 | ✅ 已完成 |
| Sprint 10 | AI 智能助手 | ✅ 已完成 |
| Sprint 11 | 打磨 + 数据沉淀 + 社交增长 | 🔄 进行中 |

---

## 🏗️ 项目结构

```
uni/
├── backend/                        # Spring Boot 后端
│   ├── src/main/
│   │   ├── java/com/uni/
│   │   │   ├── UniApplication.java         # 启动类
│   │   │   ├── common/                     # 公共模块 (Result, BizCode, Exception)
│   │   │   ├── config/                     # 配置类 (MybatisPlus, CORS, JWT)
│   │   │   ├── controller/                 # 控制器
│   │   │   │   ├── UserController.java     # 用户模块
│   │   │   │   ├── WeightRecordController  # 体重记录
│   │   │   │   ├── MealRecordController    # 餐食打卡
│   │   │   │   ├── ExerciseController      # 运动记录
│   │   │   │   ├── RecipeController        # 食谱推荐
│   │   │   │   ├── GoalController          # 目标管理
│   │   │   │   ├── ThemeController         # 主题设置
│   │   │   │   ├── AiAssistantController   # AI 助手
│   │   │   │   └── AttachmentController    # 文件上传
│   │   │   ├── dto/                        # 入参 DTO
│   │   │   ├── entity/                      # 数据库实体
│   │   │   ├── mapper/                      # Mapper 接口
│   │   │   ├── service/                    # Service 接口 + Impl
│   │   │   ├── util/                        # 工具类 (JwtUtil)
│   │   │   └── vo/                          # 出参 VO
│   │   └── resources/
│   │       ├── application.yml             # 开发配置
│   │       ├── application-prod.yml        # 生产配置
│   │       └── db/                         # 数据库初始化脚本
│   │           ├── init.sql                # 基础表
│   │           ├── init-recipe.sql         # 食谱表
│   │           ├── init-theme.sql          # 主题表
│   │           └── init-ai-assistant.sql   # AI 助手表
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                       # Uni-app 前端 (Vue3 + TypeScript)
│   ├── src/
│   │   ├── api/                    # API 封装
│   │   │   ├── user.api.ts
│   │   │   ├── weight.api.ts
│   │   │   ├── meal.api.ts
│   │   │   ├── exercise.api.ts
│   │   │   ├── recipe.api.ts
│   │   │   ├── theme.api.ts
│   │   │   └── ai-assistant.api.ts
│   │   ├── components/             # 公共组件
│   │   ├── pages/                  # 页面
│   │   │   ├── index/              # 首页
│   │   │   ├── user/               # 用户 (login/register/profile)
│   │   │   ├── health/             # 健康 (weight-record/weight-history)
│   │   │   ├── meal/               # 餐食 (meal-record/meal-calendar)
│   │   │   ├── exercise/           # 运动 (exercise-record/exercise-list)
│   │   │   ├── recipe/             # 食谱 (recipe-list/recipe-detail)
│   │   │   ├── goal/               # 目标 (goal-set/goal-detail)
│   │   │   ├── ai-assistant/        # AI 助手 (profile-setup/ai-home/ai-chat)
│   │   │   ├── theme/               # 主题设置
│   │   │   ├── dashboard/           # 数据看板
│   │   │   ├── mine/                # 我的
│   │   │   └── settings/            # 设置
│   │   ├── stores/                 # Pinia 状态管理
│   │   │   ├── user.ts
│   │   │   ├── theme.ts
│   │   │   └── ...
│   │   ├── types/                  # TypeScript 类型定义
│   │   ├── styles/                 # 全局样式 + 主题
│   │   ├── static/                 # 静态资源 (图标等)
│   │   ├── utils/                  # 工具函数
│   │   ├── App.vue                  # 应用入口
│   │   ├── main.ts                 # 主入口
│   │   └── uni.scss                # 全局样式
│   ├── pages.json                  # 页面路由配置
│   ├── vite.config.ts
│   └── package.json
│
├── docker-compose.yml              # Docker 一键部署
├── Dockerfile.backend              # 后端 Dockerfile
└── README.md                       # 项目说明文档
```

---

## 🗄️ 数据库表清单

| 表名 | 说明 | Sprint |
|------|------|--------|
| `t_user` | 用户表 | ✅ Sprint 1 |
| `t_weight_record` | 体重记录表 | ✅ Sprint 1 |
| `t_meal_record` | 餐食打卡表 | ✅ Sprint 2 |
| `t_attachment` | 附件表（图片/视频） | ✅ Sprint 2 |
| `t_exercise_record` | 运动记录表 | ✅ Sprint 3 |
| `t_recipe` | 食谱表 | ✅ Sprint 4 |
| `t_recipe_ingredient` | 食谱食材关联表 | ✅ Sprint 4 |
| `t_user_favorite_recipe` | 用户收藏食谱表 | ✅ Sprint 4 |
| `t_user_goal` | 用户目标表 | ✅ Sprint 5 |
| `t_goal_checkin` | 打卡记录汇总表 | ✅ Sprint 5 |
| `t_user_theme` | 用户主题设置表 | ✅ Sprint 9 |
| `t_user_profile` | AI 用户画像表 | ✅ Sprint 10 |
| `t_ai_chat_history` | AI 对话历史表 | ✅ Sprint 10 |
| `t_personalized_plan` | 个性化计划表 | ✅ Sprint 10 |
| `t_ai_daily_tip` | AI 每日建议表 | ✅ Sprint 10 |

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker | 20+ |
| MySQL | 8.0 |
| Redis | 7.0 |

### 1. 启动基础服务

```bash
# 克隆项目
git clone https://github.com/gaoqiongxie/Uni.git
cd uni

# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 等待服务启动
docker-compose ps
```

### 2. 后端启动

```bash
cd backend

# 安装依赖并启动
mvn spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/uni-app-1.0.0.jar
```

> 后端地址：http://localhost:8080

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 微信小程序开发
npm run dev:mp-weixin

# H5 Web 预览
npm run dev:h5
```

> 前端开发服务：http://localhost:5173

### 4. Docker 一键部署

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 🔌 API 接口

| 模块 | 路径前缀 | 主要接口 |
|------|----------|----------|
| 用户 | `/api/user` | 登录、注册、获取信息 |
| 体重 | `/api/weight-record` | 记录体重、查询历史 |
| 餐食 | `/api/meal-record` | 打卡、查询日历 |
| 运动 | `/api/exercise` | 添加运动、查询列表 |
| 食谱 | `/api/recipe` | 列表、详情、收藏 |
| 目标 | `/api/goal` | 设置目标、查看进度 |
| 主题 | `/api/theme` | 获取/更新主题设置 |
| AI 助手 | `/api/ai` | 画像、计划、对话 |

完整 API 文档启动后访问：
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🛠️ 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.x |
| ORM | MyBatis Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7.0 |
| 认证 | JWT |
| 前端框架 | Uni-app (Vue3 + TypeScript) |
| 状态管理 | Pinia |
| UI 组件 | uView UI |
| 构建工具 | Vite |
| 部署 | Docker + docker-compose |

---

## 📊 配置说明

### 开发环境

| 参数 | 默认值 |
|------|--------|
| 数据库地址 | localhost:3306 |
| 数据库名 | uni_db |
| 用户名 | root |
| 密码 | root123456 |

### 生产环境

部署前请修改以下配置：

```yaml
# docker-compose.yml 或环境变量
MYSQL_PASSWORD: your_secure_password
DB_PASSWORD: your_secure_password
REDIS_PASSWORD: your_redis_password
JWT_SECRET: your_32_char_random_string
```

---

## 📝 开发规范

- **后端**：遵循 fehorizon 项目规范 (db-conventions.md)
- **命名**：驼峰命名 + 中文注释
- **提交**：采用 Conventional Commits 规范
  - `feat:` 新功能
  - `fix:` 修复 Bug
  - `docs:` 文档更新
  - `refactor:` 重构
  - `test:` 测试

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: add some amazing feature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👨‍💻 作者

**高琼** - Java 开发工程师

- GitHub: [gaoqiongxie](https://github.com/gaoqiongxie)

---

> 💪 轻瘦相伴，健康同行！
