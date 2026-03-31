package com.uni.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册DTO
 */
@Data
public class UserRegisterDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称最多20个字符")
    private String nickname;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
