package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.entity.RecipeEntity;
import com.uni.entity.RecipeIngredientEntity;
import com.uni.entity.UserFavoriteRecipeEntity;
import com.uni.mapper.RecipeIngredientMapper;
import com.uni.mapper.RecipeMapper;
import com.uni.mapper.UserFavoriteRecipeMapper;
import com.uni.service.RecipeService;
import com.uni.vo.recipe.RecipeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 食谱服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, RecipeEntity> implements RecipeService {

    private static final String[] DIFFICULTY_NAMES = {"", "简单", "中等", "困难"};
    private static final Map<String, String> CATEGORY_NAMES = new LinkedHashMap<>();

    static {
        CATEGORY_NAMES.put("早餐", "早餐");
        CATEGORY_NAMES.put("午餐", "午餐");
        CATEGORY_NAMES.put("晚餐", "晚餐");
        CATEGORY_NAMES.put("加餐", "加餐");
        CATEGORY_NAMES.put("低卡", "低卡食谱");
        CATEGORY_NAMES.put("高蛋白", "高蛋白食谱");
    }

    private final RecipeIngredientMapper ingredientMapper;
    private final UserFavoriteRecipeMapper favoriteMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<RecipeVO> recommendRecipes(Long userId, String category, Integer maxCalorie, int page, int size) {
        LambdaQueryWrapper<RecipeEntity> wrapper = new LambdaQueryWrapper<RecipeEntity>()
                .eq(RecipeEntity::getStatus, 1)
                .eq(RecipeEntity::getDeleteFlag, 0);

        if (category != null && !category.isBlank()) {
            wrapper.eq(RecipeEntity::getCategory, category);
        }
        if (maxCalorie != null && maxCalorie > 0) {
            wrapper.le(RecipeEntity::getCalorie, maxCalorie);
        }

        wrapper.orderByDesc(RecipeEntity::getLikeCount)
               .last("LIMIT " + size + " OFFSET " + (page - 1) * size);

        List<RecipeEntity> recipes = list(wrapper);

        // 批量获取用户收藏状态
        Set<Long> favoriteIds = userId != null ? getFavoriteIds(userId) : Collections.emptySet();

        return recipes.stream()
                .map(entity -> toVO(entity, favoriteIds.contains(entity.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public RecipeVO getRecipeDetail(Long userId, Long recipeId) {
        RecipeEntity entity = getById(recipeId);
        if (entity == null || entity.getDeleteFlag() == 1 || entity.getStatus() == 0) {
            throw new BizException(BizCode.WEIGHT_RECORD_NOT_FOUND, "食谱不存在");
        }

        Set<Long> favoriteIds = userId != null ? getFavoriteIds(userId) : Collections.emptySet();
        RecipeVO vo = toVO(entity, favoriteIds.contains(entity.getId()));

        // 查询食材
        List<RecipeIngredientEntity> ingredients = ingredientMapper.selectByRecipeId(recipeId);
        List<RecipeVO.IngredientVO> ingredientVOs = ingredients.stream().map(ing -> {
            RecipeVO.IngredientVO ingVO = new RecipeVO.IngredientVO();
            ingVO.setId(ing.getId());
            ingVO.setName(ing.getName());
            ingVO.setAmount(ing.getAmount());
            ingVO.setCalorie(ing.getCalorie());
            return ingVO;
        }).collect(Collectors.toList());
        vo.setIngredients(ingredientVOs);

        return vo;
    }

    @Override
    public List<RecipeVO> searchRecipes(Long userId, String keyword, int page, int size) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        List<RecipeEntity> recipes = baseMapper.searchRecipes(keyword.trim(), size, (page - 1) * size);

        Set<Long> favoriteIds = userId != null ? getFavoriteIds(userId) : Collections.emptySet();

        return recipes.stream()
                .map(entity -> toVO(entity, favoriteIds.contains(entity.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long userId, Long recipeId) {
        // 检查食谱是否存在
        RecipeEntity recipe = getById(recipeId);
        if (recipe == null || recipe.getDeleteFlag() == 1) {
            throw new BizException(BizCode.WEIGHT_RECORD_NOT_FOUND, "食谱不存在");
        }

        // 查询是否已收藏
        UserFavoriteRecipeEntity existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavoriteRecipeEntity>()
                        .eq(UserFavoriteRecipeEntity::getUserId, userId)
                        .eq(UserFavoriteRecipeEntity::getRecipeId, recipeId)
        );

        if (existing != null) {
            // 取消收藏
            favoriteMapper.deleteById(existing.getId());
            // 减少收藏数
            recipe.setLikeCount(Math.max(0, recipe.getLikeCount() - 1));
            updateById(recipe);
            return false;
        } else {
            // 添加收藏
            UserFavoriteRecipeEntity favorite = new UserFavoriteRecipeEntity();
            favorite.setUserId(userId);
            favorite.setRecipeId(recipeId);
            favoriteMapper.insert(favorite);
            // 增加收藏数
            recipe.setLikeCount(recipe.getLikeCount() + 1);
            updateById(recipe);
            return true;
        }
    }

    @Override
    public List<RecipeVO> getFavoriteRecipes(Long userId, int page, int size) {
        List<Long> recipeIds = favoriteMapper.selectRecipeIdsByUserId(userId);
        if (recipeIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 分页处理
        int offset = (page - 1) * size;
        if (offset >= recipeIds.size()) {
            return Collections.emptyList();
        }
        List<Long> pagedIds = recipeIds.subList(offset, Math.min(offset + size, recipeIds.size()));

        List<RecipeEntity> recipes = listByIds(pagedIds);
        // 按收藏时间排序
        Map<Long, RecipeEntity> recipeMap = recipes.stream()
                .collect(Collectors.toMap(RecipeEntity::getId, r -> r));

        Set<Long> favoriteIds = getFavoriteIds(userId);

        return pagedIds.stream()
                .filter(recipeMap::containsKey)
                .map(id -> toVO(recipeMap.get(id), favoriteIds.contains(id)))
                .collect(Collectors.toList());
    }

    private Set<Long> getFavoriteIds(Long userId) {
        List<Long> ids = favoriteMapper.selectRecipeIdsByUserId(userId);
        return ids.stream().collect(Collectors.toSet());
    }

    /**
     * Entity 转 VO
     */
    private RecipeVO toVO(RecipeEntity entity, boolean favorited) {
        RecipeVO vo = new RecipeVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setCategory(entity.getCategory());
        vo.setCategoryName(CATEGORY_NAMES.getOrDefault(entity.getCategory(), entity.getCategory()));
        vo.setDescription(entity.getDescription());
        vo.setCoverImage(entity.getCoverImage());
        vo.setCalorie(entity.getCalorie());
        vo.setProtein(entity.getProtein());
        vo.setFat(entity.getFat());
        vo.setCarbohydrate(entity.getCarbohydrate());
        vo.setFiber(entity.getFiber());
        vo.setCookingTime(entity.getCookingTime());
        vo.setDifficulty(entity.getDifficulty());
        vo.setDifficultyName(getDifficultyName(entity.getDifficulty()));
        vo.setServings(entity.getServings());
        vo.setLikeCount(entity.getLikeCount());
        vo.setFavorited(favorited);

        // 解析步骤 JSON
        if (entity.getSteps() != null && !entity.getSteps().isBlank()) {
            try {
                vo.setSteps(objectMapper.readValue(entity.getSteps(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                log.warn("解析食谱步骤JSON失败: recipeId={}", entity.getId(), e);
                vo.setSteps(Collections.singletonList(entity.getSteps()));
            }
        }

        // 解析标签
        if (entity.getTags() != null && !entity.getTags().isBlank()) {
            vo.setTagList(Arrays.asList(entity.getTags().split("[,，]")));
        }

        return vo;
    }

    private String getDifficultyName(Integer difficulty) {
        if (difficulty == null || difficulty < 1 || difficulty >= DIFFICULTY_NAMES.length) {
            return "未知";
        }
        return DIFFICULTY_NAMES[difficulty];
    }
}
