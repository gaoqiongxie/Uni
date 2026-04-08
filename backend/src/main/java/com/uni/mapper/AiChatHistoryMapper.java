package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.AiChatHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI对话记录Mapper
 */
@Mapper
public interface AiChatHistoryMapper extends BaseMapper<AiChatHistoryEntity> {

    @Select("SELECT * FROM t_ai_chat_history WHERE user_id = #{userId} AND session_id = #{sessionId} AND deleted = 0 ORDER BY created_at ASC")
    List<AiChatHistoryEntity> selectBySession(@Param("userId") Long userId, @Param("sessionId") String sessionId);
}
