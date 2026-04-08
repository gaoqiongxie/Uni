package com.uni.vo.water;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 饮水记录VO
 */
@Data
public class WaterRecordVO {

    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private Integer amount;
    private LocalTime drinkTime;
    private String drinkType;
    private String drinkTypeName;
    private String remark;
    private String createTime;
}
