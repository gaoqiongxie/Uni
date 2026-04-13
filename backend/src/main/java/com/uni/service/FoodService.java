package com.uni.service;

import com.uni.dto.food.AddFoodLogDTO;
import com.uni.dto.food.FoodSearchDTO;
import com.uni.vo.food.DailyCalorieSummaryVO;
import com.uni.vo.food.FoodDailyLogVO;
import com.uni.vo.food.FoodLibraryVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 食物服务接口
 * @author 高琼
 * @date 2026-04-13
 */
public interface FoodService {

    /**
     * 获取食物分类列表
     * @return 分类列表
     */
    List<String> getFoodCategories();

    /**
     * 根据分类获取食物列表
     * @param category 分类
     * @return 食物列表
     */
    List<FoodLibraryVO> getFoodsByCategory(String category);

    /**
     * 搜索食物
     * @param searchDTO 搜索条件
     * @return 食物列表
     */
    List<FoodLibraryVO> searchFoods(FoodSearchDTO searchDTO);

    /**
     * 获取常用食物列表
     * @return 常用食物列表
     */
    List<FoodLibraryVO> getCommonFoods();

    /**
     * 添加食物摄入记录
     * @param userId 用户ID
     * @param dto 记录信息
     * @return 记录ID
     */
    Long addFoodLog(Long userId, AddFoodLogDTO dto);

    /**
     * 删除食物摄入记录
     * @param userId 用户ID
     * @param logId 记录ID
     */
    void deleteFoodLog(Long userId, Long logId);

    /**
     * 获取用户某日某餐的食物记录
     * @param userId 用户ID
     * @param date 日期
     * @param mealType 餐次
     * @return 食物记录列表
     */
    List<FoodDailyLogVO> getMealRecords(Long userId, LocalDate date, String mealType);

    /**
     * 获取用户某日的热量汇总
     * @param userId 用户ID
     * @param date 日期
     * @return 热量汇总
     */
    DailyCalorieSummaryVO getDailySummary(Long userId, LocalDate date);

    /**
     * 计算食物热量
     * @param foodId 食物ID
     * @param amount 摄入量
     * @return 热量(千卡)
     */
    java.math.BigDecimal calculateCalories(Long foodId, java.math.BigDecimal amount);
}
