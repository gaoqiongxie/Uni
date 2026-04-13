package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.dto.food.AddFoodLogDTO;
import com.uni.dto.food.FoodSearchDTO;
import com.uni.entity.FoodDailyLogEntity;
import com.uni.entity.FoodLibraryEntity;
import com.uni.mapper.FoodDailyLogMapper;
import com.uni.mapper.FoodLibraryMapper;
import com.uni.service.FoodService;
import com.uni.vo.food.DailyCalorieSummaryVO;
import com.uni.vo.food.FoodDailyLogVO;
import com.uni.vo.food.FoodLibraryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 食物服务实现类
 * @author 高琼
 * @date 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodLibraryMapper foodLibraryMapper;
    private final FoodDailyLogMapper foodDailyLogMapper;

    /** 食物分类映射 */
    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();
    static {
        CATEGORY_MAP.put("staple", "主食");
        CATEGORY_MAP.put("meat", "肉蛋奶");
        CATEGORY_MAP.put("vegetable", "蔬菜");
        CATEGORY_MAP.put("fruit", "水果");
        CATEGORY_MAP.put("snack", "零食");
        CATEGORY_MAP.put("drink", "饮品");
        CATEGORY_MAP.put("other", "其他");
    }

    /** 餐次映射 */
    private static final Map<String, String> MEAL_TYPE_MAP = new HashMap<>();
    static {
        MEAL_TYPE_MAP.put("breakfast", "早餐");
        MEAL_TYPE_MAP.put("lunch", "午餐");
        MEAL_TYPE_MAP.put("dinner", "晚餐");
        MEAL_TYPE_MAP.put("snack", "加餐");
    }

    @Override
    public List<String> getFoodCategories() {
        return Arrays.asList("staple", "meat", "vegetable", "fruit", "snack", "drink", "other");
    }

    @Override
    public List<FoodLibraryVO> getFoodsByCategory(String category) {
        List<FoodLibraryEntity> entities = foodLibraryMapper.selectByCategory(category);
        return entities.stream().map(this::convertToFoodLibraryVO).collect(Collectors.toList());
    }

    @Override
    public List<FoodLibraryVO> searchFoods(FoodSearchDTO searchDTO) {
        List<FoodLibraryEntity> entities;
        
        if (Boolean.TRUE.equals(searchDTO.getCommonOnly())) {
            entities = foodLibraryMapper.selectCommonFoods();
        } else if (StringUtils.hasText(searchDTO.getKeyword())) {
            entities = foodLibraryMapper.searchByName(searchDTO.getKeyword());
        } else if (StringUtils.hasText(searchDTO.getCategory())) {
            entities = foodLibraryMapper.selectByCategory(searchDTO.getCategory());
        } else {
            entities = foodLibraryMapper.selectList(null);
        }
        
        return entities.stream().map(this::convertToFoodLibraryVO).collect(Collectors.toList());
    }

    @Override
    public List<FoodLibraryVO> getCommonFoods() {
        List<FoodLibraryEntity> entities = foodLibraryMapper.selectCommonFoods();
        return entities.stream().map(this::convertToFoodLibraryVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addFoodLog(Long userId, AddFoodLogDTO dto) {
        FoodDailyLogEntity entity = new FoodDailyLogEntity();
        entity.setUserId(userId);
        entity.setRecordDate(dto.getRecordDate());
        entity.setMealType(dto.getMealType());
        entity.setFoodId(dto.getFoodId());
        entity.setFoodName(dto.getFoodName());
        entity.setCalories(dto.getCalories());
        entity.setAmount(dto.getAmount());
        entity.setUnit(dto.getUnit());
        entity.setProtein(dto.getProtein() != null ? dto.getProtein() : BigDecimal.ZERO);
        entity.setFat(dto.getFat() != null ? dto.getFat() : BigDecimal.ZERO);
        entity.setCarbs(dto.getCarbs() != null ? dto.getCarbs() : BigDecimal.ZERO);
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setRemark(dto.getRemark());
        
        foodDailyLogMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFoodLog(Long userId, Long logId) {
        LambdaQueryWrapper<FoodDailyLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodDailyLogEntity::getId, logId)
               .eq(FoodDailyLogEntity::getUserId, userId);
        foodDailyLogMapper.delete(wrapper);
    }

    @Override
    public List<FoodDailyLogVO> getMealRecords(Long userId, LocalDate date, String mealType) {
        List<FoodDailyLogEntity> entities = foodDailyLogMapper.selectByUserAndDateAndMeal(userId, date, mealType);
        return entities.stream().map(this::convertToFoodDailyLogVO).collect(Collectors.toList());
    }

    @Override
    public DailyCalorieSummaryVO getDailySummary(Long userId, LocalDate date) {
        // 获取当日所有记录
        List<FoodDailyLogEntity> allRecords = foodDailyLogMapper.selectByUserAndDate(userId, date);
        
        DailyCalorieSummaryVO summary = new DailyCalorieSummaryVO();
        summary.setDate(date);
        
        // 按餐次分组
        Map<String, List<FoodDailyLogVO>> mealMap = new HashMap<>();
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        
        // 各餐次热量
        BigDecimal breakfastCal = BigDecimal.ZERO;
        BigDecimal lunchCal = BigDecimal.ZERO;
        BigDecimal dinnerCal = BigDecimal.ZERO;
        BigDecimal snackCal = BigDecimal.ZERO;
        
        for (FoodDailyLogEntity entity : allRecords) {
            FoodDailyLogVO vo = convertToFoodDailyLogVO(entity);
            mealMap.computeIfAbsent(entity.getMealType(), k -> new ArrayList<>()).add(vo);
            
            BigDecimal cal = entity.getCalories();
            totalCalories = totalCalories.add(cal);
            totalProtein = totalProtein.add(entity.getProtein() != null ? entity.getProtein() : BigDecimal.ZERO);
            totalFat = totalFat.add(entity.getFat() != null ? entity.getFat() : BigDecimal.ZERO);
            totalCarbs = totalCarbs.add(entity.getCarbs() != null ? entity.getCarbs() : BigDecimal.ZERO);
            
            switch (entity.getMealType()) {
                case "breakfast": breakfastCal = breakfastCal.add(cal); break;
                case "lunch": lunchCal = lunchCal.add(cal); break;
                case "dinner": dinnerCal = dinnerCal.add(cal); break;
                case "snack": snackCal = snackCal.add(cal); break;
            }
        }
        
        summary.setBreakfastCalories(breakfastCal);
        summary.setLunchCalories(lunchCal);
        summary.setDinnerCalories(dinnerCal);
        summary.setSnackCalories(snackCal);
        summary.setTotalCalories(totalCalories);
        summary.setTotalProtein(totalProtein);
        summary.setTotalFat(totalFat);
        summary.setTotalCarbs(totalCarbs);
        summary.setMealRecords(mealMap);
        
        // 计算营养素占比
        if (totalCalories.compareTo(BigDecimal.ZERO) > 0) {
            DailyCalorieSummaryVO.NutritionRatioVO ratio = new DailyCalorieSummaryVO.NutritionRatioVO();
            // 蛋白质4kcal/g, 脂肪9kcal/g, 碳水4kcal/g
            BigDecimal proteinCal = totalProtein.multiply(new BigDecimal("4"));
            BigDecimal fatCal = totalFat.multiply(new BigDecimal("9"));
            BigDecimal carbsCal = totalCarbs.multiply(new BigDecimal("4"));
            
            ratio.setProteinRatio(proteinCal.multiply(new BigDecimal("100")).divide(totalCalories, 1, RoundingMode.HALF_UP));
            ratio.setFatRatio(fatCal.multiply(new BigDecimal("100")).divide(totalCalories, 1, RoundingMode.HALF_UP));
            ratio.setCarbsRatio(carbsCal.multiply(new BigDecimal("100")).divide(totalCalories, 1, RoundingMode.HALF_UP));
            summary.setNutritionRatio(ratio);
        }
        
        return summary;
    }

    @Override
    public BigDecimal calculateCalories(Long foodId, BigDecimal amount) {
        if (foodId == null || amount == null) {
            return BigDecimal.ZERO;
        }
        
        FoodLibraryEntity food = foodLibraryMapper.selectById(foodId);
        if (food == null) {
            return BigDecimal.ZERO;
        }
        
        // 按100g计算热量
        return food.getCaloriesPer100g().multiply(amount).divide(new BigDecimal("100"), 1, RoundingMode.HALF_UP);
    }

    /**
     * 转换为食物库VO
     */
    private FoodLibraryVO convertToFoodLibraryVO(FoodLibraryEntity entity) {
        FoodLibraryVO vo = new FoodLibraryVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCategoryName(CATEGORY_MAP.getOrDefault(entity.getCategory(), "其他"));
        return vo;
    }

    /**
     * 转换为食物记录VO
     */
    private FoodDailyLogVO convertToFoodDailyLogVO(FoodDailyLogEntity entity) {
        FoodDailyLogVO vo = new FoodDailyLogVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setMealTypeName(MEAL_TYPE_MAP.getOrDefault(entity.getMealType(), "其他"));
        return vo;
    }
}
