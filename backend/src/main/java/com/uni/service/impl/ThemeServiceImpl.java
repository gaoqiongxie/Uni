package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.UserThemeEntity;
import com.uni.mapper.UserThemeMapper;
import com.uni.service.ThemeService;
import com.uni.vo.theme.UserThemeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 主题设置服务实现
 */
@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {

    private final UserThemeMapper userThemeMapper;

    @Override
    public UserThemeVO getUserTheme(Long userId) {
        LambdaQueryWrapper<UserThemeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserThemeEntity::getUserId, userId)
               .eq(UserThemeEntity::getDeleted, 0);
        
        UserThemeEntity entity = userThemeMapper.selectOne(wrapper);
        
        // 如果没有设置，返回默认设置
        if (entity == null) {
            UserThemeVO defaultTheme = new UserThemeVO();
            defaultTheme.setThemeMode(0); // 跟随系统
            defaultTheme.setPrimaryColor("#667eea");
            defaultTheme.setFontSize(1); // 中
            return defaultTheme;
        }
        
        UserThemeVO vo = new UserThemeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserTheme(Long userId, UserThemeVO vo) {
        LambdaQueryWrapper<UserThemeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserThemeEntity::getUserId, userId)
               .eq(UserThemeEntity::getDeleted, 0);
        
        UserThemeEntity existing = userThemeMapper.selectOne(wrapper);
        
        if (existing != null) {
            // 更新
            existing.setThemeMode(vo.getThemeMode());
            existing.setPrimaryColor(vo.getPrimaryColor());
            existing.setFontSize(vo.getFontSize());
            existing.setUpdatedAt(LocalDateTime.now());
            userThemeMapper.updateById(existing);
        } else {
            // 新增
            UserThemeEntity entity = new UserThemeEntity();
            entity.setUserId(userId);
            entity.setThemeMode(vo.getThemeMode());
            entity.setPrimaryColor(vo.getPrimaryColor());
            entity.setFontSize(vo.getFontSize());
            entity.setDeleted(0);
            userThemeMapper.insert(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchThemeMode(Long userId, Integer themeMode) {
        LambdaQueryWrapper<UserThemeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserThemeEntity::getUserId, userId)
               .eq(UserThemeEntity::getDeleted, 0);
        
        UserThemeEntity existing = userThemeMapper.selectOne(wrapper);
        
        if (existing != null) {
            existing.setThemeMode(themeMode);
            existing.setUpdatedAt(LocalDateTime.now());
            userThemeMapper.updateById(existing);
        } else {
            UserThemeEntity entity = new UserThemeEntity();
            entity.setUserId(userId);
            entity.setThemeMode(themeMode);
            entity.setPrimaryColor("#667eea");
            entity.setFontSize(1);
            entity.setDeleted(0);
            userThemeMapper.insert(entity);
        }
    }
}
