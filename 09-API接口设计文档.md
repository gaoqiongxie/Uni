# 轻瘦 - API接口设计文档 & pom.xml 配置

## 📋 pom.xml 完整依赖配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.uni</groupId>
    <artifactId>uni-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>轻瘦健康应用</name>
    <description>科学减脂健康应用后端服务</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <knife4j.version>4.4.0</knife4j.version>
        <jjwt.version>0.12.3</jjwt.version>
        <qiniu.version>7.13.0</qiniu.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Spring Boot Cache -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Druid连接池 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-3-starter</artifactId>
            <version>1.2.20</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Knife4j API文档 -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
            <version>${knife4j.version}</version>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- 七牛云OSS -->
        <dependency>
            <groupId>com.qiniu</groupId>
            <artifactId>qiniu-java-sdk</artifactId>
            <version>${qiniu.version}</version>
        </dependency>

        <!-- Hutool工具库 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.22</version>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Spring Security Test -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## ⚙️ application.yml 配置文件

```yaml
# 应用配置
spring:
  application:
    name: qingshou-app
  
  # 数据源配置
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/qingshou?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
  
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0
      timeout: 10000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

# Server配置
server:
  port: 8080
  servlet:
    context-path: /

# MyBatis Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.uni.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleteFlag
      logic-delete-value: 1
      logic-not-delete-value: 0

# 应用自定义配置
app:
  jwt:
    secret: your_jwt_secret_key_minimum_256_bits
    expire: 7200       # token过期时间(秒)，默认2小时
    refresh-expire: 604800  # refresh token过期时间，默认7天
  
  qiniu:
    access-key: your_qiniu_access_key
    secret-key: your_qiniu_secret_key
    bucket: qingshou
    domain: https://your-qiniu-domain.com
  
  sms:
    access-key: your_sms_access_key
    secret-key: your_sms_secret_key
    template-code: SMS_XXXXXXXX
    sign-name: 轻瘦App
  
  # 默认值配置
  defaults:
    calorie-goal: 1400       # 默认每日热量目标(kcal)
    weight-record-days: 90   # 体重记录查询默认天数

# Swagger文档配置
knife4j:
  enable: true
  openapi:
    title: 轻瘦App API文档
    description: 轻瘦减脂健康应用接口文档
    version: 1.0.0
    concat: 高琼
    license: Apache 2.0

# 日志配置
logging:
  level:
    com.uni: DEBUG
    org.springframework.web: INFO
    com.baomidou.mybatisplus: DEBUG
  file:
    name: logs/qingshou-app.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

---

## 🌐 API接口设计文档

### 接口总览

| 模块 | 接口数量 | 基础路径 |
|-----|---------|---------|
| 用户管理 | 8 | /api/user |
| 体重记录 | 6 | /api/weight-record |
| 餐食打卡 | 7 | /api/meal-record |
| 文件上传 | 3 | /api/upload |
| 首页数据 | 3 | /api/dashboard |

---

### 一、用户管理接口 (/api/user)

#### 1.1 用户注册

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/user/register |
| 鉴权 | 不需要 |

**请求体**:
```json
{
  "phone": "13800138000",
  "password": "123456",
  "nickname": "健康达人",
  "smsCode": "123456"
}
```

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "phone": "13800138000",
    "nickname": "健康达人",
    "avatarUrl": "",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "..."
  }
}
```

#### 1.2 用户登录

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/user/login |
| 鉴权 | 不需要 |

**请求体**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

#### 1.3 获取当前用户信息

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/user/info |
| 鉴权 | 需要token |

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "username": "user_1001",
    "phone": "138****8000",
    "nickname": "健康达人",
    "avatarUrl": "https://example.com/avatar.jpg",
    "gender": 2,
    "birthday": "1992-05-15",
    "height": 165.0,
    "currentWeight": 65.0,
    "targetWeight": 55.0,
    "bmi": 23.9,
    "activityLevel": 2,
    "calorieGoal": 1400,
    "userStatus": 1,
    "lastLoginTime": "2026-03-31 16:00:00"
  }
}
```

#### 1.4 更新用户信息

| 属性 | 值 |
|-----|---|
| 请求路径 | PUT /api/user/update |
| 鉴权 | 需要token |

**请求体**:
```json
{
  "nickname": "减脂达人",
  "gender": 2,
  "birthday": "1992-05-15",
  "height": 165.0,
  "targetWeight": 55.0,
  "activityLevel": 2,
  "calorieGoal": 1400
}
```

#### 1.5 发送验证码

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/user/sms-code |
| 鉴权 | 不需要 |

**请求体**:
```json
{
  "phone": "13800138000",
  "type": "register"
}
```

#### 1.6 上传头像

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/user/avatar |
| 鉴权 | 需要token |
| Content-Type | multipart/form-data |

---

### 二、体重记录接口 (/api/weight-record)

#### 2.1 创建体重记录

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/weight-record |
| 鉴权 | 需要token |

**请求体**:
```json
{
  "weight": 64.5,
  "bodyFatRate": 28.5,
  "muscleRate": 35.0,
  "waterRate": 55.0,
  "waistCircumference": 78.0,
  "hipCircumference": 95.0,
  "recordDate": "2026-03-31",
  "recordType": 1,
  "remark": "早上空腹称重"
}
```

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 5001,
    "userId": 1001,
    "weight": 64.5,
    "bodyFatRate": 28.5,
    "bmi": 23.7,
    "recordDate": "2026-03-31",
    "weightChange": -0.5,
    "createTime": "2026-03-31 07:30:00"
  }
}
```

#### 2.2 获取体重记录列表

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/weight-record/list |
| 鉴权 | 需要token |

**请求参数**:
```
page=1&size=10&startDate=2026-01-01&endDate=2026-03-31
```

#### 2.3 获取体重统计

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/weight-record/stats |
| 鉴权 | 需要token |

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "currentWeight": 64.5,
    "targetWeight": 55.0,
    "startWeight": 68.0,
    "totalLoss": 3.5,
    "remainingLoss": 9.5,
    "progress": 26.9,
    "trend": [
      {"date": "2026-03-25", "weight": 65.0, "bmi": 23.9},
      {"date": "2026-03-28", "weight": 64.8, "bmi": 23.8},
      {"date": "2026-03-31", "weight": 64.5, "bmi": 23.7}
    ],
    "weeklyChange": -0.5,
    "monthlyChange": -3.5
  }
}
```

---

### 三、餐食打卡接口 (/api/meal-record)

#### 3.1 创建餐食记录

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/meal-record |
| 鉴权 | 需要token |

**请求体**:
```json
{
  "recordDate": "2026-03-31",
  "mealType": 1,
  "mealTime": "07:30",
  "mealContent": "全麦面包、水煮蛋、无糖豆浆",
  "calorieEstimate": 350,
  "moodType": 1,
  "attachmentIds": "100,101,102",
  "isHealthy": 1,
  "remark": "健康早餐"
}
```

#### 3.2 获取某天餐食记录

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/meal-record/date/{date} |
| 鉴权 | 需要token |

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "date": "2026-03-31",
    "totalCalorie": 1320,
    "calorieGoal": 1400,
    "checkRate": 100,
    "meals": [
      {
        "id": 6001,
        "mealType": 1,
        "mealTypeName": "早餐",
        "mealTime": "07:30",
        "mealContent": "全麦面包、水煮蛋",
        "calorieEstimate": 350,
        "isHealthy": 1,
        "photos": ["https://example.com/photo1.jpg"],
        "createTime": "2026-03-31 07:35:00"
      }
    ]
  }
}
```

#### 3.3 获取打卡日历

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/meal-record/calendar |
| 鉴权 | 需要token |

**请求参数**:
```
year=2026&month=3
```

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2026,
    "month": 3,
    "totalDays": 31,
    "checkInDays": 25,
    "checkInRate": 80.6,
    "currentStreak": 7,
    "maxStreak": 15,
    "calendar": [
      {"date": "2026-03-01", "checkIn": true, "mealCount": 3},
      {"date": "2026-03-02", "checkIn": false, "mealCount": 0}
    ]
  }
}
```

---

### 四、文件上传接口 (/api/upload)

#### 4.1 上传图片

| 属性 | 值 |
|-----|---|
| 请求路径 | POST /api/upload/image |
| 鉴权 | 需要token |
| Content-Type | multipart/form-data |

**请求参数**:
```
file: 图片文件
attachmentType: 1 (1.餐食图片,2.体重照片)
```

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 100,
    "oss_url": "https://cdn.qingshou.com/images/20260331/abc123.jpg",
    "thumbnail_url": "https://cdn.qingshou.com/images/20260331/abc123_thumb.jpg",
    "file_name": "meal_photo.jpg",
    "file_size": 204800
  }
}
```

---

### 五、首页数据接口 (/api/dashboard)

#### 5.1 获取今日概览

| 属性 | 值 |
|-----|---|
| 请求路径 | GET /api/dashboard/today |
| 鉴权 | 需要token |

**响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "greeting": "早上好，健康达人！",
    "date": "2026-03-31",
    "weight": {
      "current": 64.5,
      "target": 55.0,
      "change": -0.5,
      "trend": "down"
    },
    "meals": {
      "totalCalorie": 1320,
      "calorieGoal": 1400,
      "breakfast": true,
      "lunch": true,
      "dinner": false,
      "snack": false
    },
    "checkIn": {
      "streak": 7,
      "todayDone": false,
      "totalDays": 25
    },
    "tips": "今天的热量还剩80kcal，晚餐可以吃100g水煮鸡胸肉。"
  }
}
```

---

## 🔒 鉴权说明

### Token格式
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token失效处理
- 状态码: 401
- 响应体: `{"code": 401, "message": "登录已过期，请重新登录"}`
- 客户端操作: 清除本地token，跳转到登录页

### 错误码体系
| 错误码 | 含义 |
|-------|-----|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录/Token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 10001 | 手机号已注册 |
| 10002 | 验证码错误/过期 |
| 10003 | 用户名或密码错误 |
| 10004 | 用户已被禁用 |
| 20001 | 体重记录已存在 |
| 20002 | 记录不存在 |
| 30001 | 文件格式不支持 |
| 30002 | 文件大小超限 |

---

**文档版本**: V1.0
**更新时间**: 2026-03-31
**接口设计**: 高琼
