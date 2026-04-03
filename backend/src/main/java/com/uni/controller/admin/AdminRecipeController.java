package com.uni.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.common.Result;
import com.uni.entity.RecipeEntity;
import com.uni.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 - 食谱管理
 */
@Tag(name = "后台-食谱管理")
@RestController
@RequestMapping("/api/admin/recipe")
@RequiredArgsConstructor
public class AdminRecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "获取食谱列表")
    @GetMapping("/list")
    public Result<Page<RecipeEntity>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String mealType) {
        // 实际实现需要扩展Service支持查询参数
        Page<RecipeEntity> result = recipeService.page(new Page<>(page, size));
        return Result.success(result);
    }

    @Operation(summary = "获取食谱详情")
    @GetMapping("/{id}")
    public Result<RecipeEntity> detail(@PathVariable Long id) {
        RecipeEntity recipe = recipeService.getById(id);
        return Result.success(recipe);
    }

    @Operation(summary = "新增食谱")
    @PostMapping
    public Result<Long> create(@RequestBody RecipeEntity recipe) {
        recipeService.save(recipe);
        return Result.success(recipe.getId());
    }

    @Operation(summary = "更新食谱")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RecipeEntity recipe) {
        recipe.setId(id);
        recipeService.updateById(recipe);
        return Result.success();
    }

    @Operation(summary = "删除食谱")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recipeService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        recipeService.removeByIds(ids);
        return Result.success();
    }
}
