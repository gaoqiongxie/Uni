package com.uni.vo.exercise;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运动库VO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class ExerciseLibraryVO {

    /** 主键ID */
    private Long id;

    /** 运动名称 */
    private String exerciseName;

    /** 运动分类 */
    private String category;

    /** 分类显示名称 */
    private String categoryName;

    /** MET值 */
    private BigDecimal metValue;

    /** 强度 */
    private String intensity;

    /** 强度显示名称 */
    private String intensityName;

    /** 运动描述 */
    private String description;

    /** 运动图标URL */
    private String iconUrl;

    /** 是否常用 */
    private Integer isCommon;

    /** 创建时间 */
    private LocalDateTime createTime;

    public String getCategoryName() {
        if (category == null) return "";
        switch (category) {
            case "aerobic": return "有氧运动";
            case "strength": return "力量训练";
            case "flexibility": return "柔韧性";
            case "sports": return "球类运动";
            case "daily": return "日常活动";
            case "other": return "其他";
            default: return category;
        }
    }

    public String getIntensityName() {
        if (intensity == null) return "";
        switch (intensity) {
            case "low": return "低强度";
            case "moderate": return "中等强度";
            case "high": return "高强度";
            default: return intensity;
        }
    }
}
