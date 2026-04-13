package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.FoodDailyLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 食物摄入记录Mapper接口
 * @author 高琼
 * @date 2026-04-13
 */
@Mapper
public interface FoodDailyLogMapper extends BaseMapper<FoodDailyLogEntity> {

    /**
     * 查询用户某日某餐的食物记录
     * @param userId 用户ID
     * @param recordDate 日期
     * @param mealType 餐次
     * @return 食物记录列表
     */
    @Select("SELECT * FROM t_food_daily_log WHERE user_id = #{userId} AND record_date = #{recordDate} AND meal_type = #{mealType} ORDER BY create_time DESC")
    List<FoodDailyLogEntity> selectByUserAndDateAndMeal(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate, @Param("mealType") String mealType);

    /**
     * 查询用户某日的所有食物记录
     * @param userId 用户ID
     * @param recordDate 日期
     * @return 食物记录列表
     */
    @Select("SELECT * FROM t_food_daily_log WHERE user_id = #{userId} AND record_date = #{recordDate} ORDER BY meal_type, create_time DESC")
    List<FoodDailyLogEntity> selectByUserAndDate(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    /**
     * 查询用户某日某餐的热量总和
     * @param userId 用户ID
     * @param recordDate 日期
     * @param mealType 餐次
     * @return 热量总和
     */
    @Select("SELECT COALESCE(SUM(calories), 0) FROM t_food_daily_log WHERE user_id = #{userId} AND record_date = #{recordDate} AND meal_type = #{mealType}")
    java.math.BigDecimal sumCaloriesByMeal(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate, @Param("mealType") String mealType);

    /**
     * 查询用户某日总热量
     * @param userId 用户ID
     * @param recordDate 日期
     * @return 总热量
     */
    @Select("SELECT COALESCE(SUM(calories), 0) FROM t_food_daily_log WHERE user_id = #{userId} AND record_date = #{recordDate}")
    java.math.BigDecimal sumCaloriesByDate(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);
}
