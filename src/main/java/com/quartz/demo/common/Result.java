package com.quartz.demo.common;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 统一API响应结果封装
 *
 * @author XiaoXuxuy
 */
@ApiModel(value = "统一API响应结果封装（Result）", description = "统一API响应结果封装（Result）")
public class Result<T> {

    @ApiModelProperty(value = "错误编号(0:成功-1:失败)")
    private int code;

    @ApiModelProperty(value = "结果信息")
    private String message;

    @ApiModelProperty(value = "结果数据")
    private T data;

    public Result<T> setCode(ResultCode resultCode) {
        this.code = resultCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
