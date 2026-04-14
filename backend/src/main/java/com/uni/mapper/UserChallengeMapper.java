package com.uni.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserChallengeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface UserChallengeMapper extends BaseMapper<UserChallengeEntity> {
    @Select("SELECT * FROM t_user_challenge WHERE user_id = #{userId} AND status = 1 ORDER BY create_time DESC")
    List<UserChallengeEntity> selectActiveByUserId(@Param("userId") Long userId);
}
