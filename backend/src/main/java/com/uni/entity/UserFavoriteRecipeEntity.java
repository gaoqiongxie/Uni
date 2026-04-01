package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏食谱表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_favorite_recipe")
public class UserFavoriteRecipeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 食谱id */
    private Long recipeId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
