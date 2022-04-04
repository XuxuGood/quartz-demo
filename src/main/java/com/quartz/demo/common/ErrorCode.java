package com.quartz.demo.common;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 错误码
 *
 * @author Xuxu
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode implements IEnum<String> {

    /**
     * 以下为错误代码及相应的Message
     */
    SYS_ERROR(10001, "系统异常");

    public long code;
    public String message;

    ErrorCode(final long code, final String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}
