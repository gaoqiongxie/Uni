package com.uni.vo.goal;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 目标 VO
 */
@Data
public class UserGoalVO {

    private Long id;

    /** 目标类型(1.减重,2.维持,3.增肌) */
    private Integer goalType;

    /** 目标类型名称 */
    private String goalTypeName;

    /** 起始体重(kg) */
    private BigDecimal startWeight;

    /** 目标体重(kg) */
    private BigDecimal targetWeight;

    /** 需减重/增重(kg) */
    private BigDecimal weightGap;

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

    /** 状态名称 */
    private String statusName;

    /** 达成日期 */
    private LocalDate achievedDate;

    /** 动力宣言 */
    private String remark;

    // ── 进度统计（实时计算）──

    /** 当前体重(kg) */
    private BigDecimal currentWeight;

    /** 已减重(kg) */
    private BigDecimal weightLost;

    /** 完成进度(0-100) */
    private BigDecimal progressPercent;

    /** 已坚持天数 */
    private Integer persistDays;

    /** 距目标天数（剩余） */
    private Integer remainDays;

    /** 累计打卡天数 */
    private Integer checkinDays;

    /** 打卡率(%) */
    private BigDecimal checkinRate;
}
