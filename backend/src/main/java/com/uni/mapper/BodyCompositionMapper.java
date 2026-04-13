package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.BodyCompositionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 身体成分 Mapper
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@Mapper
public interface BodyCompositionMapper extends BaseMapper<BodyCompositionEntity> {
}
