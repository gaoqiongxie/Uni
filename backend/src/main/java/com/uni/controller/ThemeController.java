package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.ThemeService;
import com.uni.util.UserContext;
import com.uni.vo.theme.UserThemeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 主题设置控制器
 */
@RestController
@RequestMapping("/api/theme")
@RequiredArgsConstructor
@Tag(name = "主题设置", description = "用户主题设置相关接口")
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping("/get")
    @Operation(summary = "获取用户主题设置")
    public Result<UserThemeVO> getUserTheme() {
        Long userId = UserContext.getCurrentUserId();
        UserThemeVO theme = themeService.getUserTheme(userId);
        return Result.success(theme);
    }

    @PostMapping("/save")
    @Operation(summary = "保存主题设置")
    public Result<Void> saveUserTheme(@RequestBody UserThemeVO vo) {
        Long userId = UserContext.getCurrentUserId();
        themeService.saveUserTheme(userId, vo);
        return Result.success();
    }

    @PostMapping("/switch/{themeMode}")
    @Operation(summary = "切换主题模式", description = "0-跟随系统, 1-浅色, 2-深色")
    public Result<Void> switchThemeMode(@PathVariable Integer themeMode) {
        Long userId = UserContext.getCurrentUserId();
        themeService.switchThemeMode(userId, themeMode);
        return Result.success();
    }
}
