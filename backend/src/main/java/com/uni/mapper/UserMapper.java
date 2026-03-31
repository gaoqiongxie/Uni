package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
