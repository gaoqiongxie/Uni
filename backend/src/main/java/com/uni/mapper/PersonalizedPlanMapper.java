package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.PersonalizedPlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 个性化减肥计划Mapper
 */
@Mapper
public interface PersonalizedPlanMapper extends BaseMapper<PersonalizedPlanEntity> {

    @Select("SELECT * FROM t_personalized_plan WHERE user_id = #{userId} AND is_active = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<PersonalizedPlanEntity> selectActivePlans(@Param("userId") Long userId);
}
