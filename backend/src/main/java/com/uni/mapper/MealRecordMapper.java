package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.MealRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 餐食打卡表 Mapper
 */
@Mapper
public interface MealRecordMapper extends BaseMapper<MealRecordEntity> {

    /**
     * 查询用户某天的餐食记录
     */
    @Select("SELECT * FROM t_meal_record WHERE user_id = #{userId} AND record_date = #{recordDate} AND delete_flag = 0 ORDER BY meal_time ASC")
    List<MealRecordEntity> selectByUserIdAndDate(@Param("userId") Long userId,
                                                  @Param("recordDate") LocalDate recordDate);

    /**
     * 统计用户某月打卡天数
     */
    @Select("SELECT DISTINCT record_date FROM t_meal_record WHERE user_id = #{userId} AND YEAR(record_date) = #{year} AND MONTH(record_date) = #{month} AND delete_flag = 0")
    List<LocalDate> selectCheckInDates(@Param("userId") Long userId,
                                        @Param("year") int year,
                                        @Param("month") int month);
}
