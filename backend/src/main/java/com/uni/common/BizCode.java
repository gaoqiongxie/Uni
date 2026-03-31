package com.uni.common;

/**
 * 业务错误码常量
 */
public interface BizCode {

    // 成功
    int SUCCESS = 200;

    // 客户端错误
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;

    // 服务端错误
    int INTERNAL_ERROR = 500;

    // 用户模块 1xxxx
    int USER_PHONE_EXISTS = 10001;       // 手机号已注册
    int USER_SMS_CODE_ERROR = 10002;     // 验证码错误/过期
    int USER_PASSWORD_ERROR = 10003;     // 用户名或密码错误
    int USER_DISABLED = 10004;           // 用户已被禁用

    // 体重记录模块 2xxxx
    int WEIGHT_RECORD_EXISTS = 20001;    // 体重记录已存在
    int WEIGHT_RECORD_NOT_FOUND = 20002; // 记录不存在

    // 文件模块 3xxxx
    int FILE_FORMAT_NOT_SUPPORT = 30001; // 文件格式不支持
    int FILE_SIZE_EXCEEDED = 30002;      // 文件大小超限
}
