package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.WaterRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮水记录Mapper
 */
@Mapper
public interface WaterRecordMapper extends BaseMapper<WaterRecordEntity> {

    /**
     * 查询用户某日的饮水记录
     */
    @Select("SELECT * FROM t_water_record WHERE user_id = #{userId} AND record_date = #{date} AND deleted = 0 ORDER BY drink_time DESC")
    List<WaterRecordEntity> selectByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 查询用户某日饮水总量
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_water_record WHERE user_id = #{userId} AND record_date = #{date} AND deleted = 0")
    Integer selectTotalAmountByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 查询用户连续达标天数
     */
    @Select("SELECT COUNT(DISTINCT record_date) FROM t_water_record WHERE user_id = #{userId} AND record_date >= #{startDate} AND deleted = 0 GROUP BY record_date HAVING SUM(amount) >= #{goal}")
    List<Integer> selectConsecutiveDays(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("goal") Integer goal);
}
