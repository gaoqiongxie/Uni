package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserBadgeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户徽章Mapper
 */
@Mapper
public interface UserBadgeMapper extends BaseMapper<UserBadgeEntity> {

    /**
     * 查询用户已获得的徽章ID列表
     */
    @Select("SELECT badge_id FROM t_user_badge WHERE user_id = #{userId}")
    List<Long> selectBadgeIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户新获得的徽章数量
     */
    @Select("SELECT COUNT(*) FROM t_user_badge WHERE user_id = #{userId} AND is_new = 1")
    Integer selectNewBadgeCount(@Param("userId") Long userId);

    /**
     * 标记徽章为已读
     */
    @Update("UPDATE t_user_badge SET is_new = 0 WHERE user_id = #{userId} AND is_new = 1")
    int markAsRead(@Param("userId") Long userId);

    /**
     * 检查用户是否已获得某徽章
     */
    @Select("SELECT COUNT(*) FROM t_user_badge WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    int checkUserHasBadge(@Param("userId") Long userId, @Param("badgeId") Long badgeId);
}
