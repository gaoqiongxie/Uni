package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.user.UserLoginDTO;
import com.uni.dto.user.UserRegisterDTO;
import com.uni.dto.user.UserUpdateDTO;
import com.uni.service.UserService;
import com.uni.vo.user.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册/登录/信息管理")
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserInfoVO> register(@RequestBody @Validated UserRegisterDTO dto) {
        log.info("用户注册: phone={}", dto.getPhone());
        return Result.success(userService.register(dto));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfoVO> login(@RequestBody @Validated UserLoginDTO dto) {
        log.info("用户登录: phone={}", dto.getPhone());
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<UserInfoVO> updateUserInfo(@RequestHeader("X-User-Id") Long userId,
                                              @RequestBody @Validated UserUpdateDTO dto) {
        dto.setId(userId);
        return Result.success(userService.updateUserInfo(dto));
    }

    @Operation(summary = "更新体重")
    @PutMapping("/weight")
    public Result<UserInfoVO> updateWeight(@RequestHeader("X-User-Id") Long userId,
                                            @RequestParam BigDecimal weight) {
        return Result.success(userService.updateWeight(userId, weight));
    }

    @Operation(summary = "发送验证码")
    @PostMapping("/sms-code")
    public Result<Void> sendSmsCode(@RequestParam String phone,
                                     @RequestParam(defaultValue = "register") String type) {
        userService.sendSmsCode(phone, type);
        return Result.success();
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh-token")
    public Result<UserInfoVO> refreshToken(@RequestBody Map<String, String> body) {
        return Result.success(userService.refreshToken(body.get("refreshToken")));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // Token 无状态，前端清除本地存储即可
        return Result.success();
    }
}
