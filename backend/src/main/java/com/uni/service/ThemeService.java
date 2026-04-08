package com.uni.service;

import com.uni.vo.theme.UserThemeVO;

/**
 * 主题设置服务接口
 */
public interface ThemeService {

    /**
     * 获取用户主题设置
     */
    UserThemeVO getUserTheme(Long userId);

    /**
     * 保存/更新主题设置
     */
    void saveUserTheme(Long userId, UserThemeVO vo);

    /**
     * 切换主题模式
     */
    void switchThemeMode(Long userId, Integer themeMode);
}
