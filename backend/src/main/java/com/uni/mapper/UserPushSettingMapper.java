package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserPushSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户推送设置Mapper
 */
@Mapper
public interface UserPushSettingMapper extends BaseMapper<UserPushSettingEntity> {
}
