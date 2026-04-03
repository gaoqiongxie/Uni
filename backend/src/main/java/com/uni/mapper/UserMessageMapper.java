package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户消息Mapper
 */
@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessageEntity> {
}
