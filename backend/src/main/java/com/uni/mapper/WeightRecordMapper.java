package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.WeightRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重记录表 Mapper
 */
@Mapper
public interface WeightRecordMapper extends BaseMapper<WeightRecordEntity> {

    /**
     * 查询用户指定日期范围的体重记录（按日期升序）
     */
    @Select("SELECT * FROM t_weight_record WHERE user_id = #{userId} AND record_date BETWEEN #{startDate} AND #{endDate} AND delete_flag = 0 ORDER BY record_date ASC")
    List<WeightRecordEntity> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
}
