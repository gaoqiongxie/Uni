package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserProfileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户画像Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfileEntity> {

    @Select("SELECT * FROM t_user_profile WHERE user_id = #{userId} AND deleted = 0")
    UserProfileEntity selectByUserId(@Param("userId") Long userId);
}
