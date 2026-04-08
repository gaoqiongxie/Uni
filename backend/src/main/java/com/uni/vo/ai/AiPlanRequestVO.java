package com.uni.vo.ai;

import lombok.Data;

/**
 * AI生成计划请求VO
 */
@Data
public class AiPlanRequestVO {

    private String planType;
    private String additionalRequirements;
}
