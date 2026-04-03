package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.entity.*;
import com.uni.mapper.*;
import com.uni.service.SocialService;
import com.uni.vo.social.MomentCommentVO;
import com.uni.vo.social.MomentVO;
import com.uni.vo.social.UserFollowVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社交服务实现
 */
@Service
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final MomentMapper momentMapper;
    private final UserFollowMapper followMapper;
    private final MomentLikeMapper likeMapper;
    private final MomentCommentMapper commentMapper;
    private final UserMapper userMapper;

    // ========== 动态 ==========

    @Override
    public void publishMoment(MomentEntity moment) {
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        moment.setStatus(1);
        momentMapper.insert(moment);
    }

    @Override
    public void deleteMoment(Long momentId, Long userId) {
        LambdaQueryWrapper<MomentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentEntity::getId, momentId)
               .eq(MomentEntity::getUserId, userId);
        
        MomentEntity update = new MomentEntity();
        update.setStatus(0);
        momentMapper.update(update, wrapper);
    }

    @Override
    public Page<MomentVO> getMomentList(Long userId, String type, Integer page, Integer size) {
        Page<MomentEntity> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<MomentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentEntity::getStatus, 1)
               .eq(MomentEntity::getIsPublic, 1)
               .orderByDesc(MomentEntity::getCreateTime);
        
        if ("following".equals(type)) {
            List<Long> followingIds = followMapper.selectList(
                new LambdaQueryWrapper<UserFollowEntity>()
                    .eq(UserFollowEntity::getUserId, userId)
            ).stream().map(UserFollowEntity::getFollowUserId).collect(Collectors.toList());
            
            if (!followingIds.isEmpty()) {
                wrapper.in(MomentEntity::getUserId, followingIds);
            } else {
                return new Page<>();
            }
        }
        
        Page<MomentEntity> entityPage = momentMapper.selectPage(pageParam, wrapper);
        
        return entityPage.convert(this::convertToMomentVO);
    }

    @Override
    public Page<MomentVO> getUserMoments(Long targetUserId, Long currentUserId, Integer page, Integer size) {
        Page<MomentEntity> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<MomentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentEntity::getUserId, targetUserId)
               .eq(MomentEntity::getStatus, 1)
               .orderByDesc(MomentEntity::getCreateTime);
        
        // 不是自己查看时，只显示公开动态
        if (!targetUserId.equals(currentUserId)) {
            wrapper.eq(MomentEntity::getIsPublic, 1);
        }
        
        Page<MomentEntity> entityPage = momentMapper.selectPage(pageParam, wrapper);
        return entityPage.convert(this::convertToMomentVO);
    }

    private MomentVO convertToMomentVO(MomentEntity entity) {
        MomentVO vo = new MomentVO();
        BeanUtils.copyProperties(entity, vo);
        
        if (StringUtils.hasText(entity.getImages())) {
            vo.setImages(Arrays.asList(entity.getImages().split(",")));
        }
        
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        
        return vo;
    }

    // ========== 关注 ==========

    @Override
    @Transactional
    public void follow(Long userId, Long followUserId) {
        if (userId.equals(followUserId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        LambdaQueryWrapper<UserFollowEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowEntity::getUserId, userId)
               .eq(UserFollowEntity::getFollowUserId, followUserId);
        
        if (followMapper.selectCount(wrapper) > 0) {
            return;
        }
        
        UserFollowEntity follow = new UserFollowEntity();
        follow.setUserId(userId);
        follow.setFollowUserId(followUserId);
        followMapper.insert(follow);
    }

    @Override
    public void unfollow(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserFollowEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowEntity::getUserId, userId)
               .eq(UserFollowEntity::getFollowUserId, followUserId);
        followMapper.delete(wrapper);
    }

    @Override
    public List<UserFollowVO> getFollowingList(Long userId) {
        LambdaQueryWrapper<UserFollowEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowEntity::getUserId, userId);
        
        return followMapper.selectList(wrapper).stream()
            .map(f -> convertToFollowVO(f.getFollowUserId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserFollowVO> getFollowerList(Long userId) {
        LambdaQueryWrapper<UserFollowEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowEntity::getFollowUserId, userId);
        
        return followMapper.selectList(wrapper).stream()
            .map(f -> convertToFollowVO(f.getUserId()))
            .collect(Collectors.toList());
    }

    private UserFollowVO convertToFollowVO(Long userId) {
        UserFollowVO vo = new UserFollowVO();
        vo.setUserId(userId);
        
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        
        return vo;
    }

    @Override
    public Boolean isFollowing(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserFollowEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowEntity::getUserId, userId)
               .eq(UserFollowEntity::getFollowUserId, targetUserId);
        return followMapper.selectCount(wrapper) > 0;
    }

    // ========== 点赞 ==========

    @Override
    @Transactional
    public void likeMoment(Long momentId, Long userId) {
        LambdaQueryWrapper<MomentLikeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentLikeEntity::getMomentId, momentId)
               .eq(MomentLikeEntity::getUserId, userId);
        
        if (likeMapper.selectCount(wrapper) > 0) {
            return;
        }
        
        MomentLikeEntity like = new MomentLikeEntity();
        like.setMomentId(momentId);
        like.setUserId(userId);
        likeMapper.insert(like);
        
        momentMapper.incrementLikeCount(momentId);
    }

    @Override
    @Transactional
    public void unlikeMoment(Long momentId, Long userId) {
        LambdaQueryWrapper<MomentLikeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentLikeEntity::getMomentId, momentId)
               .eq(MomentLikeEntity::getUserId, userId);
        
        if (likeMapper.delete(wrapper) > 0) {
            momentMapper.decrementLikeCount(momentId);
        }
    }

    // ========== 评论 ==========

    @Override
    @Transactional
    public void commentMoment(Long momentId, Long userId, String content, Long parentId, Long replyUserId) {
        MomentCommentEntity comment = new MomentCommentEntity();
        comment.setMomentId(momentId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId != null ? parentId : 0L);
        comment.setReplyUserId(replyUserId);
        comment.setLikeCount(0);
        comment.setStatus(1);
        commentMapper.insert(comment);
        
        momentMapper.incrementCommentCount(momentId);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        LambdaQueryWrapper<MomentCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentCommentEntity::getId, commentId)
               .eq(MomentCommentEntity::getUserId, userId);
        
        MomentCommentEntity comment = commentMapper.selectOne(wrapper);
        if (comment != null) {
            comment.setStatus(0);
            commentMapper.updateById(comment);
            momentMapper.decrementCommentCount(comment.getMomentId());
        }
    }

    @Override
    public List<MomentCommentVO> getMomentComments(Long momentId) {
        LambdaQueryWrapper<MomentCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentCommentEntity::getMomentId, momentId)
               .eq(MomentCommentEntity::getStatus, 1)
               .eq(MomentCommentEntity::getParentId, 0)
               .orderByDesc(MomentCommentEntity::getCreateTime);
        
        List<MomentCommentEntity> parentComments = commentMapper.selectList(wrapper);
        
        return parentComments.stream()
            .map(this::convertToCommentVO)
            .peek(vo -> vo.setReplies(getReplies(vo.getId())))
            .collect(Collectors.toList());
    }

    private List<MomentCommentVO> getReplies(Long parentId) {
        LambdaQueryWrapper<MomentCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentCommentEntity::getParentId, parentId)
               .eq(MomentCommentEntity::getStatus, 1)
               .orderByAsc(MomentCommentEntity::getCreateTime);
        
        return commentMapper.selectList(wrapper).stream()
            .map(this::convertToCommentVO)
            .collect(Collectors.toList());
    }

    private MomentCommentVO convertToCommentVO(MomentCommentEntity entity) {
        MomentCommentVO vo = new MomentCommentVO();
        BeanUtils.copyProperties(entity, vo);
        
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        
        if (entity.getReplyUserId() != null) {
            UserEntity replyUser = userMapper.selectById(entity.getReplyUserId());
            if (replyUser != null) {
                vo.setReplyUserNickname(replyUser.getNickname());
            }
        }
        
        return vo;
    }
}
