package com.uni.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizException extends RuntimeException {

    private Integer code;

    public BizException(String message) {
        super(message);
        this.code = BizCode.INTERNAL_ERROR;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public static BizException of(Integer code, String message) {
        return new BizException(code, message);
    }
}
