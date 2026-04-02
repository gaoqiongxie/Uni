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
 * 用户目标表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_goal")
public class UserGoalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 目标类型(1.减重,2.维持,3.增肌) */
    private Integer goalType;

    /** 起始体重(kg) */
    private BigDecimal startWeight;

    /** 目标体重(kg) */
    private BigDecimal targetWeight;

    /** 每日热量目标(kcal) */
    private Integer calorieGoal;

    /** 每周运动天数 */
    private Integer exerciseDaysPerWeek;

    /** 每日运动时长(分钟) */
    private Integer exerciseMinutesPerDay;

    /** 开始日期 */
    private LocalDate startDate;

    /** 目标截止日期 */
    private LocalDate targetDate;

    /** 状态(0.已放弃,1.进行中,2.已达成) */
    private Integer status;

    /** 达成日期 */
    private LocalDate achievedDate;

    /** 备注/动力宣言 */
    private String remark;

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
