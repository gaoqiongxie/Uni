package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.SleepGoalEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 睡眠目标Mapper
 */
@Mapper
public interface SleepGoalMapper extends BaseMapper<SleepGoalEntity> {

    /**
     * 根据用户ID查询
     */
    @Select("SELECT * FROM t_sleep_goal WHERE user_id = #{userId}")
    SleepGoalEntity selectByUserId(@Param("userId") Long userId);
}
