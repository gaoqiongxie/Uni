package com.uni.vo.food;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 每日热量汇总VO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class DailyCalorieSummaryVO {

    /** 日期 */
    private LocalDate date;

    /** 早餐热量 */
    private BigDecimal breakfastCalories;

    /** 午餐热量 */
    private BigDecimal lunchCalories;

    /** 晚餐热量 */
    private BigDecimal dinnerCalories;

    /** 加餐热量 */
    private BigDecimal snackCalories;

    /** 总热量 */
    private BigDecimal totalCalories;

    /** 总蛋白质(g) */
    private BigDecimal totalProtein;

    /** 总脂肪(g) */
    private BigDecimal totalFat;

    /** 总碳水(g) */
    private BigDecimal totalCarbs;

    /** 各餐次记录 */
    private Map<String, List<FoodDailyLogVO>> mealRecords;

    /** 营养素占比 */
    private NutritionRatioVO nutritionRatio;

    /**
     * 营养素占比VO
     */
    @Data
    public static class NutritionRatioVO {
        /** 蛋白质占比(%) */
        private BigDecimal proteinRatio;

        /** 脂肪占比(%) */
        private BigDecimal fatRatio;

        /** 碳水占比(%) */
        private BigDecimal carbsRatio;
    }
}
