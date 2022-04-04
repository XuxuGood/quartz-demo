package com.quartz.demo.web.controller;

import com.quartz.demo.common.Result;
import com.quartz.demo.common.ResultGenerator;
import com.quartz.demo.web.bean.CronJobParam;
import com.quartz.demo.web.bean.TimeoutJobParam;
import com.quartz.demo.web.service.SchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:45 下午
 */
@Slf4j
@RestController
@RequestMapping("/quartz")
@Api(tags = "定时任务相关API")
public class TimerController {

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/crontab")
    @ApiOperation(value = "添加定时任务", notes = "添加定时任务")
    public Result<String> addQuartzJob(@RequestBody CronJobParam param) {
        schedulerService.addQuartzJob(param);
        return ResultGenerator.genSuccessResult("添加定时任务成功！");
    }

    @PostMapping("/timeout")
    @ApiOperation(value = "添加超时定时任务", notes = "添加超时定时任务")
    public Result<String> add(@RequestBody TimeoutJobParam param) {
        schedulerService.addQuartzTimeoutJob(param);
        return ResultGenerator.genSuccessResult("添加超时定时任务成功！");
    }

    @DeleteMapping("/{key}/{group}")
    @ApiOperation(value = "删除定时任务", notes = "删除定时任务")
    public Result<String> deleteQuartzJob(@PathVariable String key, @PathVariable String group) {
        schedulerService.deleteQuartzJob(key, group);
        return ResultGenerator.genSuccessResult("删除定时任务成功！");
    }

    @GetMapping("/{key}/{group}")
    @ApiOperation(value = "获取任务信息", notes = "获取任务信息")
    public Result<JobDetail> get(@PathVariable String key, @PathVariable String group) {
        JobDetail job = schedulerService.getJob(key, group);
        return ResultGenerator.genSuccessResult(job);
    }

}
