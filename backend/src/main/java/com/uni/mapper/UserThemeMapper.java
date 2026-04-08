package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserThemeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户主题设置Mapper
 */
@Mapper
public interface UserThemeMapper extends BaseMapper<UserThemeEntity> {

    /**
     * 根据用户ID查询主题设置
     */
    @Select("SELECT * FROM t_user_theme WHERE user_id = #{userId} AND deleted = 0")
    UserThemeEntity selectByUserId(@Param("userId") Long userId);
}
