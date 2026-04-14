package com.uni.vo.calorie;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 热量趋势数据点VO
 * @author 高琼
 * @date 2026-04-14
 */
@Data
public class CalorieTrendPointVO {

    /** 日期 */
    private String date;

    /** 展示标签(如：4/14) */
    private String label;

    /** 摄入热量(千卡) */
    private BigDecimal intake;

    /** 消耗热量(千卡) */
    private BigDecimal burned;

    /** 净摄入(千卡) */
    private BigDecimal net;
}
