package com.uni.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.entity.MomentEntity;
import com.uni.vo.social.MomentCommentVO;
import com.uni.vo.social.MomentVO;
import com.uni.vo.social.UserFollowVO;

import java.util.List;

/**
 * 社交服务接口
 */
public interface SocialService {

    // ========== 动态 ==========

    void publishMoment(MomentEntity moment);

    void deleteMoment(Long momentId, Long userId);

    Page<MomentVO> getMomentList(Long userId, String type, Integer page, Integer size);

    Page<MomentVO> getUserMoments(Long targetUserId, Long currentUserId, Integer page, Integer size);

    // ========== 关注 ==========

    void follow(Long userId, Long followUserId);

    void unfollow(Long userId, Long followUserId);

    List<UserFollowVO> getFollowingList(Long userId);

    List<UserFollowVO> getFollowerList(Long userId);

    Boolean isFollowing(Long userId, Long targetUserId);

    // ========== 点赞 ==========

    void likeMoment(Long momentId, Long userId);

    void unlikeMoment(Long momentId, Long userId);

    // ========== 评论 ==========

    void commentMoment(Long momentId, Long userId, String content, Long parentId, Long replyUserId);

    void deleteComment(Long commentId, Long userId);

    List<MomentCommentVO> getMomentComments(Long momentId);
}
