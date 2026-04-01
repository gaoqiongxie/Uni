package com.uni.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 运动记录DTO
 */
@Data
public class ExerciseRecordDTO {

    /** 用户ID（由Controller从Header中注入） */
    private Long userId;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    @NotBlank(message = "运动类型不能为空")
    private String exerciseType;

    @NotNull(message = "运动时长不能为空")
    private Integer duration;

    /** 消耗热量(kcal) */
    private Integer calorieBurn = 0;

    /** 运动距离(km) */
    private BigDecimal distance;

    /** 平均心率(bpm) */
    private Integer heartRateAvg;

    /** 最大心率(bpm) */
    private Integer heartRateMax;

    /** 运动强度(1.低强度,2.中等强度,3.高强度) */
    private Integer intensity = 2;

    /** 运动感受(0.一般,1.轻松,2.适中,3.吃力,4.很累) */
    private Integer feeling = 0;

    /** 附件id列表(逗号分隔) */
    private String attachmentIds;

    /** 记录来源(1.手动录入,2.智能设备同步) */
    private Integer recordSource = 1;

    /** 备注 */
    private String remark;
}
