package com.uni.service;

import com.uni.vo.social.CommentVO;
import com.uni.vo.social.MomentVO;

import java.util.List;

public interface SocialService {
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    List<Long> getFriendIds(Long userId);

    void publishMoment(Long userId, String content, String images, String type);
    List<MomentVO> getFriendMoments(Long userId, int limit);

    void like(Long userId, String targetType, Long targetId);
    void unlike(Long userId, String targetType, Long targetId);

    void comment(Long userId, String targetType, Long targetId, String content);
    List<CommentVO> getComments(String targetType, Long targetId);
}
