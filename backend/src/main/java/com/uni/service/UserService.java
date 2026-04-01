package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.dto.user.PhoneLoginDTO;
import com.uni.dto.user.UserLoginDTO;
import com.uni.dto.user.UserRegisterDTO;
import com.uni.dto.user.UserUpdateDTO;
import com.uni.entity.UserEntity;
import com.uni.vo.user.UserInfoVO;

import java.math.BigDecimal;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 用户注册
     */
    UserInfoVO register(UserRegisterDTO dto);

    /**
     * 用户登录（密码方式）
     */
    UserInfoVO login(UserLoginDTO dto);

    /**
     * 用户登录（手机号+验证码方式）
     */
    UserInfoVO loginByPhone(PhoneLoginDTO dto);

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    UserInfoVO updateUserInfo(UserUpdateDTO dto);

    /**
     * 更新体重（同步更新用户的currentWeight和BMI）
     */
    UserInfoVO updateWeight(Long userId, BigDecimal weight);

    /**
     * 发送验证码
     */
    void sendSmsCode(String phone, String type);

    /**
     * 刷新Token
     */
    UserInfoVO refreshToken(String refreshToken);

    /**
     * 验证验证码
     */
    void verifySmsCode(String phone, String code, String type);
}
