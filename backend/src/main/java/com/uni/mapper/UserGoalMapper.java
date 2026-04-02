package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserGoalEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户目标 Mapper
 */
@Mapper
public interface UserGoalMapper extends BaseMapper<UserGoalEntity> {

    /**
     * 查询用户当前进行中的目标
     */
    @Select("SELECT * FROM t_user_goal WHERE user_id = #{userId} AND status = 1 AND delete_flag = 0 ORDER BY create_time DESC LIMIT 1")
    UserGoalEntity selectActiveGoal(Long userId);
}
