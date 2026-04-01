package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.ExerciseRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录表 Mapper
 */
@Mapper
public interface ExerciseRecordMapper extends BaseMapper<ExerciseRecordEntity> {

    /**
     * 查询用户指定日期范围的运动记录（按日期降序）
     */
    @Select("SELECT * FROM t_exercise_record WHERE user_id = #{userId} AND record_date BETWEEN #{startDate} AND #{endDate} AND delete_flag = 0 ORDER BY record_date DESC, create_time DESC")
    List<ExerciseRecordEntity> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 查询用户指定日期的运动记录
     */
    @Select("SELECT * FROM t_exercise_record WHERE user_id = #{userId} AND record_date = #{recordDate} AND delete_flag = 0 ORDER BY create_time DESC")
    List<ExerciseRecordEntity> selectByUserIdAndDate(@Param("userId") Long userId,
                                                      @Param("recordDate") LocalDate recordDate);
}
