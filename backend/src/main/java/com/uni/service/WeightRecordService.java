package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.dto.weight.WeightRecordDTO;
import com.uni.entity.WeightRecordEntity;
import com.uni.vo.weight.WeightRecordVO;
import com.uni.vo.weight.WeightStatsVO;

import java.util.List;

/**
 * 体重记录服务接口
 */
public interface WeightRecordService extends IService<WeightRecordEntity> {

    /**
     * 创建体重记录
     */
    WeightRecordVO createWeightRecord(Long userId, WeightRecordDTO dto);

    /**
     * 获取体重记录列表（分页）
     */
    List<WeightRecordVO> getWeightRecords(Long userId, int page, int size, String startDate, String endDate);

    /**
     * 获取体重统计
     */
    WeightStatsVO getWeightStats(Long userId);

    /**
     * 删除体重记录
     */
    boolean deleteWeightRecord(Long id, Long userId);
}
