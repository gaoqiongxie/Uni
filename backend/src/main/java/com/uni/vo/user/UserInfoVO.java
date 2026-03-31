package com.uni.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
@Accessors(chain = true)
public class UserInfoVO {

    private Long id;
    private String username;
    /** 手机号（脱敏） */
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer gender;
    private LocalDate birthday;
    private BigDecimal height;
    private BigDecimal currentWeight;
    private BigDecimal targetWeight;
    private BigDecimal bmi;
    private Integer activityLevel;
    private Integer calorieGoal;
    private Integer userStatus;
    private LocalDateTime lastLoginTime;

    /** 登录时返回的token */
    private String token;
    /** 刷新token */
    private String refreshToken;
}
