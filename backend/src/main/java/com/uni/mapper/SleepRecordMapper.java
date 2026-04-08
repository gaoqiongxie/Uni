package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.SleepRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 睡眠记录Mapper
 */
@Mapper
public interface SleepRecordMapper extends BaseMapper<SleepRecordEntity> {

    /**
     * 查询用户指定日期范围的睡眠记录
     */
    @Select("SELECT * FROM t_sleep_record WHERE user_id = #{userId} " +
            "AND sleep_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY sleep_date DESC")
    List<SleepRecordEntity> selectByDateRange(@Param("userId") Long userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查询用户最近一条睡眠记录
     */
    @Select("SELECT * FROM t_sleep_record WHERE user_id = #{userId} ORDER BY sleep_date DESC LIMIT 1")
    SleepRecordEntity selectLatestByUserId(@Param("userId") Long userId);

    /**
     * 计算平均睡眠时长
     */
    @Select("SELECT AVG(sleep_duration) FROM t_sleep_record WHERE user_id = #{userId} " +
            "AND sleep_date >= #{startDate}")
    Double selectAvgDuration(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    /**
     * 计算平均睡眠质量
     */
    @Select("SELECT AVG(sleep_quality) FROM t_sleep_record WHERE user_id = #{userId} " +
            "AND sleep_date >= #{startDate}")
    Double selectAvgQuality(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
}
