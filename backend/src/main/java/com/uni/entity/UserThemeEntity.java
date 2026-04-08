package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户主题设置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_theme")
public class UserThemeEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 主题模式: 0-跟随系统, 1-浅色, 2-深色
     */
    private Integer themeMode;

    /**
     * 主题色
     */
    private String primaryColor;

    /**
     * 字体大小: 0-小, 1-中, 2-大
     */
    private Integer fontSize;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    private Integer deleted;
}
