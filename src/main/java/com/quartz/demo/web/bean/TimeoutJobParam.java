package com.quartz.demo.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:34 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "超时任务参数", description = "超时任务参数")
public class TimeoutJobParam extends BaseParam {

    @ApiModelProperty(value = "超时分钟")
    private int timeoutMinute;

}
