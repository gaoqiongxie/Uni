package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.BadgeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 徽章Mapper
 */
@Mapper
public interface BadgeMapper extends BaseMapper<BadgeEntity> {

    /**
     * 根据条件类型查询徽章
     */
    @Select("SELECT * FROM t_badge WHERE condition_type = #{conditionType} AND status = 1 ORDER BY condition_value ASC")
    List<BadgeEntity> selectByConditionType(@Param("conditionType") String conditionType);

    /**
     * 查询用户未获得的徽章
     */
    @Select("SELECT b.* FROM t_badge b " +
            "WHERE b.status = 1 AND b.id NOT IN " +
            "(SELECT badge_id FROM t_user_badge WHERE user_id = #{userId}) " +
            "ORDER BY b.sort_order ASC")
    List<BadgeEntity> selectUnacquiredBadges(@Param("userId") Long userId);
}
