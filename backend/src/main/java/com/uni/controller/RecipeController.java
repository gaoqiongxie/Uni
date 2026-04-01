package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.RecipeService;
import com.uni.vo.recipe.RecipeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食谱推荐控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
@Tag(name = "食谱推荐", description = "食谱浏览与收藏")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "推荐食谱列表")
    @GetMapping("/recommend")
    public Result<List<RecipeVO>> recommend(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxCalorie,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("推荐食谱: userId={}, category={}, maxCalorie={}", userId, category, maxCalorie);
        return Result.success(recipeService.recommendRecipes(userId, category, maxCalorie, page, size));
    }

    @Operation(summary = "食谱详情")
    @GetMapping("/{id}")
    public Result<RecipeVO> detail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id) {
        return Result.success(recipeService.getRecipeDetail(userId, id));
    }

    @Operation(summary = "搜索食谱")
    @GetMapping("/search")
    public Result<List<RecipeVO>> search(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(recipeService.searchRecipes(userId, keyword, page, size));
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/favorite/{recipeId}")
    public Result<Boolean> toggleFavorite(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long recipeId) {
        boolean favorited = recipeService.toggleFavorite(userId, recipeId);
        return Result.success(favorited);
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/favorites")
    public Result<List<RecipeVO>> favorites(@RequestHeader("X-User-Id") Long userId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(recipeService.getFavoriteRecipes(userId, page, size));
    }
}
