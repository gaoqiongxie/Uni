package com.uni.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.MomentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface MomentMapper extends BaseMapper<MomentEntity> {
    @Select("SELECT * FROM t_moment WHERE user_id IN (SELECT friend_id FROM t_friend WHERE user_id=#{userId} AND status=1) AND status=1 ORDER BY create_time DESC LIMIT #{limit}")
    List<MomentEntity> selectFriendMoments(@Param("userId") Long userId, @Param("limit") int limit);
}
