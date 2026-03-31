package com.uni.vo.weight;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 体重统计VO
 */
@Data
@Accessors(chain = true)
public class WeightStatsVO {

    /** 当前体重 */
    private BigDecimal currentWeight;
    /** 目标体重 */
    private BigDecimal targetWeight;
    /** 开始体重 */
    private BigDecimal startWeight;
    /** 总减少量 */
    private BigDecimal totalLoss;
    /** 剩余目标 */
    private BigDecimal remainingLoss;
    /** 进度(%) */
    private BigDecimal progress;
    /** 周变化 */
    private BigDecimal weeklyChange;
    /** 月变化 */
    private BigDecimal monthlyChange;
    /** 趋势数据 */
    private List<WeightTrendVO> trend;

    @Data
    @Accessors(chain = true)
    public static class WeightTrendVO {
        private String date;
        private BigDecimal weight;
        private BigDecimal bmi;
    }
}
