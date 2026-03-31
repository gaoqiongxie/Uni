# 轻瘦 - MVP阶段数据库设计与Java代码

## 📊 数据库设计总览

### MVP阶段表结构(4张表)

| 表名 | 中文名称 | 用途 | 记录数预估 |
|-----|---------|-----|-----------|
| `t_user` | 用户表 | 存储用户基础信息 | 1万+ |
| `t_weight_record` | 体重记录表 | 记录用户体重变化 | 10万+ |
| `t_meal_record` | 餐食打卡表 | 记录三餐打卡情况 | 30万+ |
| `t_attachment` | 附件表 | 存储打卡图片 | 10万+ |

---

## 📋 DDL建表语句

### 1. 用户表 (t_user)
**用途**: 存储用户注册信息和健康档案基础数据

```sql
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名',
  `phone` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码(加密存储)',
  `nickname` varchar(50) NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar_url` varchar(500) NOT NULL DEFAULT '' COMMENT '头像URL',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别(0.未知,1.男,2.女)',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `height` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '身高(cm)',
  `current_weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '当前体重(kg)',
  `target_weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '目标体重(kg)',
  `bmi` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT 'BMI指数',
  `activity_level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '活动等级(1.久坐,2.轻度活动,3.中度活动,4.重度活动)',
  `calorie_goal` int(11) NOT NULL DEFAULT '1400' COMMENT '每日热量目标(kcal)',
  `user_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态(1.正常,2.冻结,3.注销)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`user_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 体重记录表 (t_weight_record)
**用途**: 记录用户体重变化历史

```sql
CREATE TABLE `t_weight_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `record_date` date NOT NULL COMMENT '记录日期',
  `weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '体重(kg)',
  `body_fat_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '体脂率(%)',
  `muscle_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '肌肉率(%)',
  `water_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '水分率(%)',
  `waist_circumference` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '腰围(cm)',
  `hip_circumference` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '臀围(cm)',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型(1.手动录入,2.智能设备同步)',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_date` (`user_id`, `record_date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体重记录表';
```

### 3. 餐食打卡表 (t_meal_record)
**用途**: 记录用户三餐打卡情况

```sql
CREATE TABLE `t_meal_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `record_date` date NOT NULL COMMENT '记录日期',
  `meal_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '餐食类型(1.早餐,2.午餐,3.晚餐,4.加餐)',
  `meal_time` time NOT NULL COMMENT '用餐时间',
  `meal_content` varchar(500) NOT NULL DEFAULT '' COMMENT '餐食内容描述',
  `calorie_estimate` int(11) NOT NULL DEFAULT '0' COMMENT '热量估算(kcal)',
  `mood_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '心情类型(0.正常,1.开心,2.疲惫,3.压力大,4.其他)',
  `attachment_ids` varchar(500) NOT NULL DEFAULT '' COMMENT '附件id列表(逗号分隔)',
  `record_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录来源(1.手动录入,2.拍照识别)',
  `is_healthy` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否健康(0.不健康,1.健康)',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_meal_type` (`meal_type`),
  KEY `idx_user_date_type` (`user_id`, `record_date`, `meal_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐食打卡表';
```

### 4. 附件表 (t_attachment)
**用途**: 存储用户上传的图片等附件

```sql
CREATE TABLE `t_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `file_name` varchar(255) NOT NULL DEFAULT '' COMMENT '文件名',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) NOT NULL DEFAULT '' COMMENT '文件类型(image/jpeg, image/png等)',
  `file_extension` varchar(20) NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `storage_path` varchar(500) NOT NULL DEFAULT '' COMMENT '存储路径',
  `oss_url` varchar(500) NOT NULL DEFAULT '' COMMENT 'OSS访问URL',
  `thumbnail_url` varchar(500) NOT NULL DEFAULT '' COMMENT '缩略图URL',
  `attachment_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '附件类型(1.餐食图片,2.体重照片,3.运动照片,4.其他)',
  `upload_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '上传来源(1.手机拍照,2.相册选择)',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_attachment_type` (`attachment_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';
```

---

## 🎯 Java代码生成

### 项目结构
```
com.uni
├── common           # 公共模块
├── entity           # 实体类
├── mapper           # 数据访问层
├── service          # 业务逻辑层
├── controller       # 控制器层
└── vo              # 视图对象
```

### 1. 用户表Entity

```java
package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码(加密存储)
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别(0.未知,1.男,2.女)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 当前体重(kg)
     */
    private BigDecimal currentWeight;

    /**
     * 目标体重(kg)
     */
    private BigDecimal targetWeight;

    /**
     * BMI指数
     */
    private BigDecimal bmi;

    /**
     * 活动等级(1.久坐,2.轻度活动,3.中度活动,4.重度活动)
     */
    private Integer activityLevel;

    /**
     * 每日热量目标(kcal)
     */
    private Integer calorieGoal;

    /**
     * 用户状态(1.正常,2.冻结,3.注销)
     */
    private Integer userStatus;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建人Ad
     */
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人Ad
     */
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime editTime;

    /**
     * 删除标记(0.未删除,1.已删除)
     */
    @TableLogic
    private Integer deleteFlag;
}
```

### 2. 体重记录表Mapper

```java
package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.WeightRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 体重记录表 Mapper 接口
 * </p>
 */
@Mapper
public interface WeightRecordMapper extends BaseMapper<WeightRecordEntity> {

}
```

### 3. 餐食打卡表Service接口

```java
package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.entity.MealRecordEntity;
import com.uni.vo.meal.MealRecordVO;
import com.uni.dto.meal.MealRecordDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 餐食打卡表 服务类
 * </p>
 */
public interface MealRecordService extends IService<MealRecordEntity> {


    /**
     * 创建餐食打卡记录
     */
    MealRecordVO createMealRecord(MealRecordDTO dto);

    /**
     * 获取用户某天的餐食记录
     */
    List<MealRecordVO> getUserMealRecordsByDate(Long userId, LocalDate recordDate);

    /**
     * 获取用户最近7天的打卡统计
     */
    Object getRecentMealStats(Long userId);

    /**
     * 删除餐食记录
     */
    boolean deleteMealRecord(Long id, Long userId);
}
```

### 4. 餐食打卡表ServiceImpl

```java
package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.entity.MealRecordEntity;
import com.uni.mapper.MealRecordMapper;
import com.uni.service.MealRecordService;
import com.uni.vo.meal.MealRecordVO;
import com.uni.dto.meal.MealRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 餐食打卡表 服务实现类
 * </p>
 */
@Slf4j
@Service
public class MealRecordServiceImpl extends ServiceImpl<MealRecordMapper, MealRecordEntity> implements MealRecordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealRecordVO createMealRecord(MealRecordDTO dto) {
        MealRecordEntity entity = new MealRecordEntity();
        BeanUtils.copyProperties(dto, entity);
        
        // 设置用户信息
        entity.setUserId(dto.getUserId());
        entity.setRecordDate(dto.getRecordDate());
        
        // 保存记录
        boolean saved = save(entity);
        if (saved) {
            MealRecordVO vo = new MealRecordVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }

    @Override
    public List<MealRecordVO> getUserMealRecordsByDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<MealRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MealRecordEntity::getUserId, userId)
                   .eq(MealRecordEntity::getRecordDate, recordDate)
                   .eq(MealRecordEntity::getDeleteFlag, 0)
                   .orderByAsc(MealRecordEntity::getMealTime);
        
        List<MealRecordEntity> entities = list(queryWrapper);
        return entities.stream().map(entity -> {
            MealRecordVO vo = new MealRecordVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Object getRecentMealStats(Long userId) {
        // 实现最近7天统计逻辑
        // TODO: 具体统计实现
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMealRecord(Long id, Long userId) {
        // 逻辑删除
        return removeById(id);
    }
}
```

### 5. 用户Controller

```java
package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.user.UserLoginDTO;
import com.uni.dto.user.UserRegisterDTO;
import com.uni.dto.user.UserUpdateDTO;
import com.uni.service.UserService;
import com.uni.vo.user.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户控制器
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserInfoVO> register(@RequestBody @Validated UserRegisterDTO dto) {
        log.info("用户注册: {}", dto.getPhone());
        UserInfoVO vo = userService.register(dto);
        return Result.success(vo);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfoVO> login(@RequestBody @Validated UserLoginDTO dto) {
        log.info("用户登录: {}", dto.getPhone());
        UserInfoVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        UserInfoVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<UserInfoVO> updateUserInfo(@RequestHeader("X-User-Id") Long userId,
                                            @RequestBody @Validated UserUpdateDTO dto) {
        dto.setId(userId);
        UserInfoVO vo = userService.updateUserInfo(dto);
        return Result.success(vo);
    }

    @Operation(summary = "更新体重")
    @PutMapping("/weight")
    public Result<UserInfoVO> updateWeight(@RequestHeader("X-User-Id") Long userId,
                                          @RequestParam BigDecimal weight) {
        UserInfoVO vo = userService.updateWeight(userId, weight);
        return Result.success(vo);
    }
}
```

### 6. 体重记录VO

```java
package com.uni.vo.weight;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 体重记录VO
 * </p>
 */
@Data
@Accessors(chain = true)
public class WeightRecordVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 体脂率(%)
     */
    private BigDecimal bodyFatRate;

    /**
     * 肌肉率(%)
     */
    private BigDecimal muscleRate;

    /**
     * 水分率(%)
     */
    private BigDecimal waterRate;

    /**
     * 腰围(cm)
     */
    private BigDecimal waistCircumference;

    /**
     * 臀围(cm)
     */
    private BigDecimal hipCircumference;

    /**
     * 记录类型(1.手动录入,2.智能设备同步)
     */
    private Integer recordType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;
}
```

---

## 🔧 配置类代码

### 1. MyBatis Plus配置

```java
package com.uni.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 */
@Configuration
public class MyBatisPlusConfig implements MetaObjectHandler {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 插入时填充字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "editTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时填充字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "editTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 2. 跨域配置

```java
package com.uni.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 3. 统一响应结果类

```java
package com.uni.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

---

## 📝 注意事项

### 数据库相关
1. **时间字段管理**
   - `create_time` 和 `edit_time` 由数据库自动维护，禁止在业务代码中手动赋值
   - Java Entity中需添加 `@TableField(fill = FieldFill.INSERT)` 和 `@TableField(fill = FieldFill.UPDATE)` 注解

2. **逻辑删除**
   - 所有表都使用 `delete_flag` 字段进行逻辑删除
   - 已删除(`delete_flag=1`)的记录不允许重置为未删除(`delete_flag=0`)
   - 使用 `@TableLogic` 注解自动处理逻辑删除

3. **金额/数值字段**
   - 体重、身高等字段使用 `BigDecimal` 类型
   - 金额字段使用 `DECIMAL(24,8)`，对应Java类型为 `BigDecimal`

### 代码规范
1. **命名规范**
   - 数据库字段: snake_case (user_name)
   - Java字段: camelCase (userName)
   - 常量: UPPER_SNAKE_CASE (MAX_RETRY_COUNT)

2. **类型映射**
   - MySQL `bigint` → Java `Long`
   - MySQL `datetime` → Java `LocalDateTime`
   - MySQL `date` → Java `LocalDate`
   - MySQL `decimal` → Java `BigDecimal`

3. **索引优化建议**
   - 用户表的 `phone` 字段需要唯一索引
   - 体重记录表的 `(user_id, record_date)` 需要唯一索引
   - 餐食打卡表的 `(user_id, record_date, meal_type)` 需要联合索引

### 性能考虑
1. **数据量预估**
   - 用户表: 1万+记录
   - 体重记录表: 10万+记录 (每个用户每周记录2-3次)
   - 餐食打卡表: 30万+记录 (每个用户每天3次)
   - 附件表: 10万+记录 (每个用户每周上传3-5张图片)

2. **分页查询**
   - 所有列表查询都需要支持分页
   - 使用MyBatis Plus的分页插件

3. **缓存策略**
   - 用户基本信息可缓存到Redis
   - 热点数据考虑使用本地缓存

---

**文档版本**: V1.0
**更新时间**: 2026-03-31
**技术负责**: 高琼
