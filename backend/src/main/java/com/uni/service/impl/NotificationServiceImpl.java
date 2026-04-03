package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.common.PageResult;
import com.uni.dto.notification.PushSettingDTO;
import com.uni.entity.UserMessageEntity;
import com.uni.entity.UserPushSettingEntity;
import com.uni.mapper.UserMessageMapper;
import com.uni.mapper.UserPushSettingMapper;
import com.uni.service.NotificationService;
import com.uni.vo.notification.MessageVO;
import com.uni.vo.notification.PushSettingVO;
import com.uni.vo.notification.UnreadCountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息通知服务实现
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserMessageMapper messageMapper;
    private final UserPushSettingMapper settingMapper;

    @Override
    public PageResult<MessageVO> getMessageList(Long userId, Integer messageType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserMessageEntity> wrapper = new LambdaQueryWrapper<>()
            .eq(UserMessageEntity::getUserId, userId)
            .orderByDesc(UserMessageEntity::getCreateTime);

        if (messageType != null) {
            wrapper.eq(UserMessageEntity::getMessageType, messageType);
        }

        Page<UserMessageEntity> page = messageMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<MessageVO> list = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());

        return PageResult.of(list, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public UnreadCountVO getUnreadCount(Long userId) {
        // 各类型未读数
        long total = messageMapper.selectCount(
            new LambdaQueryWrapper<UserMessageEntity>()
                .eq(UserMessageEntity::getUserId, userId)
                .eq(UserMessageEntity::getIsRead, 0)
        );

        long reminder = messageMapper.selectCount(
            new LambdaQueryWrapper<UserMessageEntity>()
                .eq(UserMessageEntity::getUserId, userId)
                .eq(UserMessageEntity::getIsRead, 0)
                .eq(UserMessageEntity::getMessageType, 1)
        );

        long achievement = messageMapper.selectCount(
            new LambdaQueryWrapper<UserMessageEntity>()
                .eq(UserMessageEntity::getUserId, userId)
                .eq(UserMessageEntity::getIsRead, 0)
                .eq(UserMessageEntity::getMessageType, 2)
        );

        long notice = messageMapper.selectCount(
            new LambdaQueryWrapper<UserMessageEntity>()
                .eq(UserMessageEntity::getUserId, userId)
                .eq(UserMessageEntity::getIsRead, 0)
                .eq(UserMessageEntity::getMessageType, 3)
        );

        UnreadCountVO vo = new UnreadCountVO();
        vo.setTotal((int) total);
        vo.setReminder((int) reminder);
        vo.setAchievement((int) achievement);
        vo.setNotice((int) notice);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long messageId) {
        UserMessageEntity message = messageMapper.selectById(messageId);
        if (message != null && message.getUserId().equals(userId)) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        UserMessageEntity update = new UserMessageEntity();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());

        messageMapper.update(update, new LambdaQueryWrapper<UserMessageEntity>()
            .eq(UserMessageEntity::getUserId, userId)
            .eq(UserMessageEntity::getIsRead, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long userId, Long messageId) {
        UserMessageEntity message = messageMapper.selectById(messageId);
        if (message != null && message.getUserId().equals(userId)) {
            messageMapper.deleteById(messageId);
        }
    }

    @Override
    public PushSettingVO getPushSetting(Long userId) {
        UserPushSettingEntity setting = settingMapper.selectOne(
            new LambdaQueryWrapper<UserPushSettingEntity>()
                .eq(UserPushSettingEntity::getUserId, userId)
        );

        if (setting == null) {
            // 创建默认设置
            setting = createDefaultSetting(userId);
        }

        PushSettingVO vo = new PushSettingVO();
        BeanUtils.copyProperties(setting, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePushSetting(Long userId, PushSettingDTO dto) {
        UserPushSettingEntity setting = settingMapper.selectOne(
            new LambdaQueryWrapper<UserPushSettingEntity>()
                .eq(UserPushSettingEntity::getUserId, userId)
        );

        if (setting == null) {
            setting = createDefaultSetting(userId);
        }

        BeanUtils.copyProperties(dto, setting);
        settingMapper.updateById(setting);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Long userId, Integer messageType, String title, String content, String extraData) {
        UserMessageEntity message = new UserMessageEntity();
        message.setUserId(userId);
        message.setMessageType(messageType);
        message.setTitle(title);
        message.setContent(content);
        message.setExtraData(extraData);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    private UserPushSettingEntity createDefaultSetting(Long userId) {
        UserPushSettingEntity setting = new UserPushSettingEntity();
        setting.setUserId(userId);
        setting.setMealReminder(1);
        setting.setMealReminderTime("08:00,12:00,18:00");
        setting.setExerciseReminder(1);
        setting.setExerciseReminderTime("20:00");
        setting.setWeeklyReport(1);
        setting.setGoalAchievement(1);
        setting.setSystemNotice(1);
        setting.setCreateTime(LocalDateTime.now());
        setting.setUpdateTime(LocalDateTime.now());
        settingMapper.insert(setting);
        return setting;
    }

    private MessageVO convertToVO(UserMessageEntity entity) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setMessageId(entity.getId());
        return vo;
    }
}
