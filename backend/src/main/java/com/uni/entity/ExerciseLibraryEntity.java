package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运动库实体类
 * @author 高琼
 * @date 2026-04-13
 */
@Data
@TableName("t_exercise_library")
public class ExerciseLibraryEntity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 运动名称 */
    private String exerciseName;

    /** 运动分类: aerobic-有氧运动,strength-力量训练,flexibility-柔韧性训练,sports-球类运动,daily-日常活动,other-其他 */
    private String category;

    /** MET值(代谢当量) */
    private BigDecimal metValue;

    /** 强度: low-低,moderate-中,high-高 */
    private String intensity;

    /** 运动描述 */
    private String description;

    /** 运动图标URL */
    private String iconUrl;

    /** 是否常用: 0-否,1-是 */
    private Integer isCommon;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
