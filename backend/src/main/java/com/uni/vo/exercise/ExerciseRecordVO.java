package com.uni.vo.exercise;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 运动记录VO
 */
@Data
@Accessors(chain = true)
public class ExerciseRecordVO {

    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private String exerciseType;
    private Integer duration;
    private Integer calorieBurn;
    private BigDecimal distance;
    private Integer heartRateAvg;
    private Integer heartRateMax;
    private Integer intensity;
    private String intensityName;
    private Integer feeling;
    private String feelingName;
    private String remark;
    private LocalDateTime createTime;
    /** 图片URL列表 */
    private List<String> photos;
}
