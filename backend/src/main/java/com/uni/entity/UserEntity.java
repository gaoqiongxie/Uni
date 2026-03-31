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
 * 用户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 手机号 */
    private String phone;

    /** 密码(加密存储) */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatarUrl;

    /** 性别(0.未知,1.男,2.女) */
    private Integer gender;

    /** 出生日期 */
    private LocalDate birthday;

    /** 身高(cm) */
    private BigDecimal height;

    /** 当前体重(kg) */
    private BigDecimal currentWeight;

    /** 目标体重(kg) */
    private BigDecimal targetWeight;

    /** BMI指数 */
    private BigDecimal bmi;

    /** 活动等级(1.久坐,2.轻度活动,3.中度活动,4.重度活动) */
    private Integer activityLevel;

    /** 每日热量目标(kcal) */
    private Integer calorieGoal;

    /** 用户状态(1.正常,2.冻结,3.注销) */
    private Integer userStatus;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 创建人Ad */
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改人Ad */
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime editTime;

    @TableLogic
    private Integer deleteFlag;
}
