package com.uni.service;

import com.uni.common.PageResult;
import com.uni.dto.notification.PushSettingDTO;
import com.uni.vo.notification.MessageVO;
import com.uni.vo.notification.PushSettingVO;
import com.uni.vo.notification.UnreadCountVO;

/**
 * 消息通知服务
 */
public interface NotificationService {

    /**
     * 获取消息列表
     */
    PageResult<MessageVO> getMessageList(Long userId, Integer messageType, Integer pageNum, Integer pageSize);

    /**
     * 获取未读消息数
     */
    UnreadCountVO getUnreadCount(Long userId);

    /**
     * 标记消息已读
     */
    void markAsRead(Long userId, Long messageId);

    /**
     * 标记所有已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除消息
     */
    void deleteMessage(Long userId, Long messageId);

    /**
     * 获取推送设置
     */
    PushSettingVO getPushSetting(Long userId);

    /**
     * 更新推送设置
     */
    void updatePushSetting(Long userId, PushSettingDTO dto);

    /**
     * 发送消息(内部使用)
     */
    void sendMessage(Long userId, Integer messageType, String title, String content, String extraData);
}
