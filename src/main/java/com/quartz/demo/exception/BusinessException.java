package com.quartz.demo.exception;

import com.quartz.demo.common.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author XiaoXuxuy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    public Long code;

    /**
     * 异常信息
     */
    public String message;

    public BusinessException(String message) {
        this.code = -1L;
        this.message = message;
    }

    public BusinessException(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCode code) {
        this.code = code.code;
        this.message = code.message;
    }

}
