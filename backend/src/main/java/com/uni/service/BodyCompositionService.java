package com.uni.service;

import com.uni.dto.BodyCompositionDTO;
import com.uni.vo.BodyCompositionVO;
import java.util.List;

/**
 * 身体成分 Service 接口
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
public interface BodyCompositionService {

    /**
     * 记录身体成分（自动计算体脂率）
     */
    BodyCompositionVO record(BodyCompositionDTO dto, Long userId);

    /**
     * 获取今日身体成分记录
     */
    BodyCompositionVO getTodayRecord(Long userId);

    /**
     * 获取最新身体成分记录
     */
    BodyCompositionVO getLatestRecord(Long userId);

    /**
     * 获取历史身体成分记录列表
     */
    List<BodyCompositionVO> getHistoryList(Long userId, Integer limit);

    /**
     * 计算体脂率（海军法公式）
     * @param gender 性别 male/female
     * @param waist 腰围(cm)
     * @param neck 颈围(cm)
     * @param hip 臀围(cm，女性用)
     * @param height 身高(cm)
     * @return 体脂率(%)
     */
    double calculateBodyFatRate(String gender, double waist, double neck, double hip, double height);

    /**
     * 根据体脂率判断等级
     * @param bodyFatRate 体脂率
     * @param gender 性别
     * @return lean(偏瘦)/normal(正常)/high(偏高)/obese(肥胖)
     */
    String getFatLevel(double bodyFatRate, String gender);

    /**
     * 估算肌肉量（基于体重和体脂率）
     */
    double estimateMuscleMass(double weight, double bodyFatRate);

    /**
     * 估算骨量（基于体重）
     */
    double estimateBoneMass(double weight);

    /**
     * 估算水分率（基于体脂率）
     */
    double estimateWaterRate(double bodyFatRate);
}
