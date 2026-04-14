package com.uni.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
    @Select("SELECT * FROM t_comment WHERE target_type=#{type} AND target_id=#{targetId} AND status=1 ORDER BY create_time DESC")
    List<CommentEntity> selectByTarget(@Param("type") String type, @Param("targetId") Long targetId);
}
