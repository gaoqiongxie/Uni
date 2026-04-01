package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 运动记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_exercise_record")
public class ExerciseRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 运动类型(跑步/游泳/瑜伽/力量训练/骑行/跳绳/散步/其他) */
    private String exerciseType;

    /** 运动时长(分钟) */
    private Integer duration;

    /** 消耗热量(kcal) */
    private Integer calorieBurn;

    /** 运动距离(km) */
    private BigDecimal distance;

    /** 平均心率(bpm) */
    private Integer heartRateAvg;

    /** 最大心率(bpm) */
    private Integer heartRateMax;

    /** 运动强度(1.低强度,2.中等强度,3.高强度) */
    private Integer intensity;

    /** 运动感受(0.一般,1.轻松,2.适中,3.吃力,4.很累) */
    private Integer feeling;

    /** 附件id列表(逗号分隔) */
    private String attachmentIds;

    /** 记录来源(1.手动录入,2.智能设备同步) */
    private Integer recordSource;

    /** 备注 */
    private String remark;

    /** 创建人Ad */
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改人Ad */
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime editTime;

    @TableLogic
    private Integer deleteFlag;
}
