package com.uni.service.impl;

import com.uni.entity.*;
import com.uni.mapper.*;
import com.uni.service.SocialService;
import com.uni.vo.social.CommentVO;
import com.uni.vo.social.MomentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialServiceImpl implements SocialService {

    @Autowired private FriendMapper friendMapper;
    @Autowired private MomentMapper momentMapper;
    @Autowired private LikeMapper likeMapper;
    @Autowired private CommentMapper commentMapper;
    @Autowired private UserMapper userMapper;

    @Override
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        FriendEntity f1 = new FriendEntity();
        f1.setUserId(userId);
        f1.setFriendId(friendId);
        f1.setStatus(1);
        friendMapper.insert(f1);

        FriendEntity f2 = new FriendEntity();
        f2.setUserId(friendId);
        f2.setFriendId(userId);
        f2.setStatus(1);
        friendMapper.insert(f2);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friendMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FriendEntity>()
                .eq(FriendEntity::getUserId, userId).eq(FriendEntity::getFriendId, friendId));
        friendMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FriendEntity>()
                .eq(FriendEntity::getUserId, friendId).eq(FriendEntity::getFriendId, userId));
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        List<FriendEntity> list = friendMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FriendEntity>()
                        .eq(FriendEntity::getUserId, userId).eq(FriendEntity::getStatus, 1));
        return list.stream().map(FriendEntity::getFriendId).collect(Collectors.toList());
    }

    @Override
    public void publishMoment(Long userId, String content, String images, String type) {
        MomentEntity m = new MomentEntity();
        m.setUserId(userId);
        m.setContent(content);
        m.setImages(images);
        m.setType(type);
        m.setLikeCount(0);
        m.setCommentCount(0);
        m.setStatus(1);
        momentMapper.insert(m);
    }

    @Override
    public List<MomentVO> getFriendMoments(Long userId, int limit) {
        List<MomentEntity> list = momentMapper.selectFriendMoments(userId, limit);
        return list.stream().map(m -> {
            MomentVO vo = new MomentVO();
            vo.setId(m.getId());
            vo.setUserId(m.getUserId());
            vo.setContent(m.getContent());
            vo.setImages(m.getImages());
            vo.setType(m.getType());
            vo.setLikeCount(m.getLikeCount());
            vo.setCommentCount(m.getCommentCount());
            vo.setCreateTime(m.getCreateTime());
            UserEntity user = userMapper.selectById(m.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void like(Long userId, String targetType, Long targetId) {
        LikeEntity like = new LikeEntity();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        likeMapper.insert(like);

        if ("moment".equals(targetType)) {
            momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<MomentEntity>()
                    .eq(MomentEntity::getId, targetId).setSql("like_count = like_count + 1"));
        }
    }

    @Override
    @Transactional
    public void unlike(Long userId, String targetType, Long targetId) {
        likeMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LikeEntity>()
                .eq(LikeEntity::getUserId, userId).eq(LikeEntity::getTargetType, targetType).eq(LikeEntity::getTargetId, targetId));

        if ("moment".equals(targetType)) {
            momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<MomentEntity>()
                    .eq(MomentEntity::getId, targetId).setSql("like_count = like_count - 1"));
        }
    }

    @Override
    @Transactional
    public void comment(Long userId, String targetType, Long targetId, String content) {
        CommentEntity c = new CommentEntity();
        c.setUserId(userId);
        c.setTargetType(targetType);
        c.setTargetId(targetId);
        c.setContent(content);
        c.setParentId(0L);
        c.setStatus(1);
        commentMapper.insert(c);

        if ("moment".equals(targetType)) {
            momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<MomentEntity>()
                    .eq(MomentEntity::getId, targetId).setSql("comment_count = comment_count + 1"));
        }
    }

    @Override
    public List<CommentVO> getComments(String targetType, Long targetId) {
        List<CommentEntity> list = commentMapper.selectByTarget(targetType, targetId);
        return list.stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            UserEntity user = userMapper.selectById(c.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
