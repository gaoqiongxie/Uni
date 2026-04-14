package com.uni.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.ChallengeCheckinEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
@Mapper
public interface ChallengeCheckinMapper extends BaseMapper<ChallengeCheckinEntity> {
    @Select("SELECT COUNT(*) FROM t_challenge_checkin WHERE user_id=#{userId} AND challenge_id=#{challengeId} AND checkin_date=#{date}")
    int countByUserAndChallengeAndDate(@Param("userId") Long userId, @Param("challengeId") Long challengeId, @Param("date") LocalDate date);
}
