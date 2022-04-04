package com.quartz.demo.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:26 下午
 */
@Data
@ApiModel(value = "基础参数", description = "基础参数")
public class BaseParam {

    @ApiModelProperty(value = "job key")
    private String key;

    @ApiModelProperty(value = "job group")
    private String group;

    @ApiModelProperty(value = "call back url")
    private String callBackUrl;

    @ApiModelProperty(value = "call back param")
    private Map requestParams;

}
