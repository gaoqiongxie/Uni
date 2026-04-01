package com.uni.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.dto.user.UserLoginDTO;
import com.uni.dto.user.UserRegisterDTO;
import com.uni.dto.user.UserUpdateDTO;
import com.uni.entity.UserEntity;
import com.uni.mapper.UserMapper;
import com.uni.service.UserService;
import com.uni.util.JwtUtil;
import com.uni.vo.user.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(UserRegisterDTO dto) {
        // 检查手机号是否已注册
        long count = count(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, dto.getPhone())
                .eq(UserEntity::getDeleteFlag, 0));
        if (count > 0) {
            throw new BizException(BizCode.USER_PHONE_EXISTS, "手机号已注册");
        }

        // TODO: 校验短信验证码
        // smsService.verifyCode(dto.getPhone(), dto.getSmsCode());

        // 创建用户
        UserEntity user = new UserEntity();
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        user.setUsername("user_" + System.currentTimeMillis());
        // 密码BCrypt加密
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setUserStatus(1);
        user.setCalorieGoal(1400);
        user.setActivityLevel(1);
        user.setAvatarUrl("");
        user.setCreateUser(dto.getPhone());
        user.setUpdateUser(dto.getPhone());
        user.setDeleteFlag(0);

        save(user);

        return buildUserInfoVO(user, true);
    }

    @Override
    public UserInfoVO login(UserLoginDTO dto) {
        UserEntity user = getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, dto.getPhone())
                .eq(UserEntity::getDeleteFlag, 0));

        if (user == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BizException(BizCode.USER_PASSWORD_ERROR, "手机号或密码错误");
        }

        if (user.getUserStatus() != 1) {
            throw new BizException(BizCode.USER_DISABLED, "账号已被禁用");
        }

        // 更新最后登录时间
        update(new UserEntity().setLastLoginTime(LocalDateTime.now()),
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, user.getId()));

        return buildUserInfoVO(user, true);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        UserEntity user = getById(userId);
        if (user == null || user.getDeleteFlag() == 1) {
            throw new BizException(BizCode.NOT_FOUND, "用户不存在");
        }
        return buildUserInfoVO(user, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateUserInfo(UserUpdateDTO dto) {
        UserEntity user = getById(dto.getId());
        if (user == null) {
            throw new BizException(BizCode.NOT_FOUND, "用户不存在");
        }

        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }
        if (dto.getHeight() != null) {
            user.setHeight(dto.getHeight());
            // 重新计算BMI
            if (user.getCurrentWeight() != null && user.getCurrentWeight().compareTo(BigDecimal.ZERO) > 0) {
                user.setBmi(calculateBMI(user.getCurrentWeight(), dto.getHeight()));
            }
        }
        if (dto.getTargetWeight() != null) {
            user.setTargetWeight(dto.getTargetWeight());
        }
        if (dto.getActivityLevel() != null) {
            user.setActivityLevel(dto.getActivityLevel());
        }
        if (dto.getCalorieGoal() != null) {
            user.setCalorieGoal(dto.getCalorieGoal());
        }

        updateById(user);
        return buildUserInfoVO(getById(dto.getId()), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateWeight(Long userId, BigDecimal weight) {
        UserEntity user = getById(userId);
        if (user == null) {
            throw new BizException(BizCode.NOT_FOUND, "用户不存在");
        }
        user.setCurrentWeight(weight);
        if (user.getHeight() != null && user.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            user.setBmi(calculateBMI(weight, user.getHeight()));
        }
        updateById(user);
        return buildUserInfoVO(getById(userId), false);
    }

    @Override
    public void sendSmsCode(String phone, String type) {
        // TODO: 接入短信服务商（阿里云/腾讯云）
        log.info("发送验证码 -> phone={}, type={}", phone, type);
    }

    /**
     * 构建用户VO
     */
    private UserInfoVO buildUserInfoVO(UserEntity user, boolean withToken) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        // 手机号脱敏
        if (StrUtil.isNotBlank(user.getPhone()) && user.getPhone().length() == 11) {
            vo.setPhone(user.getPhone().substring(0, 3) + "****" + user.getPhone().substring(7));
        }
        if (withToken) {
            vo.setToken(jwtUtil.generateToken(user.getId()));
            vo.setRefreshToken(jwtUtil.generateRefreshToken(user.getId()));
        }
        return vo;
    }

    /**
     * 计算BMI
     */
    private BigDecimal calculateBMI(BigDecimal weight, BigDecimal height) {
        // BMI = 体重(kg) / 身高(m)²
        BigDecimal heightInMeter = height.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return weight.divide(heightInMeter.multiply(heightInMeter), 2, RoundingMode.HALF_UP);
    }
}
