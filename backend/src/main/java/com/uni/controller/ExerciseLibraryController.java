package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.exercise.CalculateCaloriesDTO;
import com.uni.entity.ExerciseLibraryEntity;
import com.uni.service.ExerciseLibraryService;
import com.uni.vo.exercise.CaloriesBurnedVO;
import com.uni.vo.exercise.ExerciseLibraryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运动库控制器
 * @author 高琼
 * @date 2026-04-13
 */
@RestController
@RequestMapping("/api/exercise-library")
public class ExerciseLibraryController {

    @Autowired
    private ExerciseLibraryService exerciseLibraryService;

    /**
     * 获取所有运动分类
     */
    @GetMapping("/categories")
    public Result<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> categories = Arrays.asList(
            createCategory("aerobic", "有氧运动", "🔥"),
            createCategory("strength", "力量训练", "💪"),
            createCategory("flexibility", "柔韧性", "🧘"),
            createCategory("sports", "球类运动", "⚽"),
            createCategory("daily", "日常活动", "🚶"),
            createCategory("other", "其他", "🎯")
        );
        return Result.success(categories);
    }

    private Map<String, String> createCategory(String value, String label, String icon) {
        Map<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        map.put("icon", icon);
        return map;
    }

    /**
     * 根据分类获取运动列表
     */
    @GetMapping("/category/{category}")
    public Result<List<ExerciseLibraryVO>> getByCategory(@PathVariable String category) {
        List<ExerciseLibraryVO> list = exerciseLibraryService.getExercisesByCategory(category);
        return Result.success(list);
    }

    /**
     * 搜索运动
     */
    @GetMapping("/search")
    public Result<List<ExerciseLibraryVO>> search(@RequestParam String keyword) {
        List<ExerciseLibraryVO> list = exerciseLibraryService.searchExercises(keyword);
        return Result.success(list);
    }

    /**
     * 获取常用运动列表
     */
    @GetMapping("/common")
    public Result<List<ExerciseLibraryVO>> getCommon() {
        List<ExerciseLibraryVO> list = exerciseLibraryService.getCommonExercises();
        return Result.success(list);
    }

    /**
     * 获取运动详情
     */
    @GetMapping("/{id}")
    public Result<ExerciseLibraryEntity> getById(@PathVariable Long id) {
        ExerciseLibraryEntity entity = exerciseLibraryService.getById(id);
        return Result.success(entity);
    }

    /**
     * 计算运动消耗热量
     */
    @PostMapping("/calculate-calories")
    public Result<CaloriesBurnedVO> calculateCalories(@Valid @RequestBody CalculateCaloriesDTO dto) {
        ExerciseLibraryEntity exercise = exerciseLibraryService.getById(dto.getExerciseId());
        if (exercise == null) {
            return Result.error("运动不存在");
        }

        BigDecimal calories = exerciseLibraryService.calculateCaloriesBurned(
            exercise.getMetValue(), dto.getWeight(), dto.getDuration()
        );

        CaloriesBurnedVO vo = new CaloriesBurnedVO();
        vo.setExerciseId(exercise.getId());
        vo.setExerciseName(exercise.getExerciseName());
        vo.setMetValue(exercise.getMetValue());
        vo.setWeight(dto.getWeight());
        vo.setDuration(dto.getDuration());
        vo.setCaloriesBurned(calories);
        vo.setCaloriesDescription("约相当于 " + getFoodEquivalent(calories) + " 的热量");

        return Result.success(vo);
    }

    /**
     * 获取热量对应的食物等量描述
     */
    private String getFoodEquivalent(BigDecimal calories) {
        int cal = calories.intValue();
        if (cal < 50) {
            return "一个小苹果";
        } else if (cal < 100) {
            return "一碗米饭";
        } else if (cal < 200) {
            return "一个汉堡";
        } else if (cal < 300) {
            return "一份炸鸡";
        } else if (cal < 400) {
            return "一份披萨";
        } else {
            return "一顿丰盛的晚餐";
        }
    }
}
