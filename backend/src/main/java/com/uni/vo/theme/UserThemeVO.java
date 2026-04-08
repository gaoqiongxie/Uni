package com.uni.vo.theme;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户主题设置VO
 */
@Data
public class UserThemeVO {

    private Long id;

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
}
