package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.dto.meal.MealRecordDTO;
import com.uni.entity.MealRecordEntity;
import com.uni.vo.meal.MealCalendarVO;
import com.uni.vo.meal.MealRecordVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 餐食打卡服务接口
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
     * 获取打卡日历
     */
    MealCalendarVO getMealCalendar(Long userId, int year, int month);

    /**
     * 删除餐食记录
     */
    boolean deleteMealRecord(Long id, Long userId);
}
