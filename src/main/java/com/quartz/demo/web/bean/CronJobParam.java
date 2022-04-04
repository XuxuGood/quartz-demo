package com.quartz.demo.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:33 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "定时任务参数", description = "定时任务参数")
public class CronJobParam extends BaseParam {

    @ApiModelProperty(value = "定时表达式")
    private String cron;

}
