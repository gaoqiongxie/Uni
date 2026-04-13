package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.food.AddFoodLogDTO;
import com.uni.dto.food.FoodSearchDTO;
import com.uni.service.FoodService;
import com.uni.vo.food.DailyCalorieSummaryVO;
import com.uni.vo.food.FoodDailyLogVO;
import com.uni.vo.food.FoodLibraryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 食物控制器
 * @author 高琼
 * @date 2026-04-13
 */
@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    /**
     * 获取食物分类列表
     */
    @GetMapping("/categories")
    public Result<List<Map<String, String>>> getCategories() {
        List<String> categories = foodService.getFoodCategories();
        List<Map<String, String>> result = categories.stream().map(cat -> {
            Map<String, String> map = new java.util.HashMap<>();
            map.put("code", cat);
            map.put("name", getCategoryName(cat));
            return map;
        }).toList();
        return Result.success(result);
    }

    /**
     * 根据分类获取食物列表
     */
    @GetMapping("/library/category/{category}")
    public Result<List<FoodLibraryVO>> getFoodsByCategory(@PathVariable String category) {
        return Result.success(foodService.getFoodsByCategory(category));
    }

    /**
     * 搜索食物
     */
    @PostMapping("/library/search")
    public Result<List<FoodLibraryVO>> searchFoods(@RequestBody FoodSearchDTO searchDTO) {
        return Result.success(foodService.searchFoods(searchDTO));
    }

    /**
     * 获取常用食物列表
     */
    @GetMapping("/library/common")
    public Result<List<FoodLibraryVO>> getCommonFoods() {
        return Result.success(foodService.getCommonFoods());
    }

    /**
     * 添加食物摄入记录
     */
    @PostMapping("/log")
    public Result<Long> addFoodLog(@RequestAttribute("userId") Long userId,
                                    @RequestBody @Validated AddFoodLogDTO dto) {
        Long logId = foodService.addFoodLog(userId, dto);
        return Result.success(logId);
    }

    /**
     * 删除食物摄入记录
     */
    @DeleteMapping("/log/{logId}")
    public Result<Void> deleteFoodLog(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long logId) {
        foodService.deleteFoodLog(userId, logId);
        return Result.success();
    }

    /**
     * 获取用户某日某餐的食物记录
     */
    @GetMapping("/log/meal")
    public Result<List<FoodDailyLogVO>> getMealRecords(@RequestAttribute("userId") Long userId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                       @RequestParam String mealType) {
        return Result.success(foodService.getMealRecords(userId, date, mealType));
    }

    /**
     * 获取用户某日的热量汇总
     */
    @GetMapping("/summary/daily")
    public Result<DailyCalorieSummaryVO> getDailySummary(@RequestAttribute("userId") Long userId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(foodService.getDailySummary(userId, date));
    }

    /**
     * 计算食物热量
     */
    @GetMapping("/calculate")
    public Result<java.math.BigDecimal> calculateCalories(@RequestParam Long foodId,
                                                           @RequestParam java.math.BigDecimal amount) {
        return Result.success(foodService.calculateCalories(foodId, amount));
    }

    /**
     * 获取分类名称
     */
    private String getCategoryName(String category) {
        return switch (category) {
            case "staple" -> "主食";
            case "meat" -> "肉蛋奶";
            case "vegetable" -> "蔬菜";
            case "fruit" -> "水果";
            case "snack" -> "零食";
            case "drink" -> "饮品";
            default -> "其他";
        };
    }
}
