package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.WaterReminderSettingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 饮水提醒设置Mapper
 */
@Mapper
public interface WaterReminderSettingMapper extends BaseMapper<WaterReminderSettingEntity> {

    /**
     * 根据用户ID查询设置
     */
    @Select("SELECT * FROM t_water_reminder_setting WHERE user_id = #{userId} AND deleted = 0")
    WaterReminderSettingEntity selectByUserId(@Param("userId") Long userId);

    /**
     * 查询开启提醒的用户列表
     */
    @Select("SELECT * FROM t_water_reminder_setting WHERE enabled = 1 AND deleted = 0")
    List<WaterReminderSettingEntity> selectEnabledSettings();
}
