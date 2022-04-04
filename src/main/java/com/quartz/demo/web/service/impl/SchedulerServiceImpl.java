package com.quartz.demo.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.quartz.demo.exception.BusinessException;
import com.quartz.demo.quartz.scheduler.TriggerScheduler;
import com.quartz.demo.web.bean.CronJobParam;
import com.quartz.demo.web.bean.TimeoutJobParam;
import com.quartz.demo.web.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:36 下午
 */
@Slf4j
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final int MIN_TIMEOUT_MINUTE = 1;
    private static final int MAX_TIMEOUT_MINUTE = 24 * 60;

    @Resource
    private TriggerScheduler triggerScheduler;

    /**
     * 添加定时任务
     *
     * @param param
     * @return
     */
    @Override
    public void addQuartzJob(CronJobParam param) {
        log.info("register cron job, param: {}", JSON.toJSONString(param));
        checkCronJobParam(param);
        try {
            triggerScheduler.startJob(param);
        } catch (SchedulerException e) {
            if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage().endsWith("will never fire.")) {
                log.error("register cron job error, cron will never fire, param: {}, e: ", JSON.toJSONString(param), e);
                throw new BusinessException("表达式永远不会被触发，请检查！" + param.getCron());
            } else {
                log.error("register cron job error, start cron job error, param : {}, e: ", JSON.toJSONString(param), e);
                throw new BusinessException("start cron job error!");
            }
        }
    }

    /**
     * 添加超时定时任务
     *
     * @param param
     */
    @Override
    public void addQuartzTimeoutJob(TimeoutJobParam param) {
        log.info("register timeout job, param: {}", JSON.toJSONString(param));
        checkTimeoutJobParam(param);
        try {
            triggerScheduler.startJob(param);
        } catch (SchedulerException e) {
            log.error("register timeout job error, start cron job error, param : {}, e: ", JSON.toJSONString(param), e);
            throw new BusinessException("start timeout job error!");
        }
    }

    /**
     * 删除定时任务
     *
     * @param key
     * @param group
     */
    @Override
    public void deleteQuartzJob(String key, String group) {
        log.info("delete cron job, key: {}, group: {}", key, group);
        if (org.apache.commons.lang.StringUtils.isBlank(key) || org.apache.commons.lang.StringUtils.isBlank(group)) {
            log.error("empty key or group");
            throw new BusinessException("empty key or group");
        }
        try {
            triggerScheduler.removeJob(key, group);
        } catch (Exception e) {
            log.error("delete cron job error, key: {}, group: {}", key, group);
            throw new BusinessException("delete cron job error!");
        }
    }

    /**
     * 获取任务信息
     *
     * @param key
     * @param group
     * @return
     */
    @Override
    public JobDetail getJob(String key, String group) {
        try {
            return triggerScheduler.getJob(key, group);
        } catch (SchedulerException e) {
            log.error("get cron job info error, key: {}, group: {}", key, group);
            throw new BusinessException("get cron job info error!");
        }
    }

    /**
     * 校验注册超时定时参数
     *
     * @param param
     */
    private void checkTimeoutJobParam(TimeoutJobParam param) {
        if (StringUtils.isBlank(param.getKey())
                || StringUtils.isBlank(param.getGroup())
                || StringUtils.isBlank(param.getCallBackUrl())
                || param.getTimeoutMinute() < MIN_TIMEOUT_MINUTE || param.getTimeoutMinute() > MAX_TIMEOUT_MINUTE) {
            log.error("register timeout job, error param, param: {}", param);
            throw new BusinessException("timer job param error!");
        }
    }

    /**
     * 校验注册定时参数
     *
     * @param param
     */
    private void checkCronJobParam(CronJobParam param) {
        if (StringUtils.isBlank(param.getKey())
                || StringUtils.isBlank(param.getGroup())
                || StringUtils.isBlank(param.getCallBackUrl())
                || !CronExpression.isValidExpression(param.getCron())) {
            log.error("register cron job, error param, param: {}", JSON.toJSONString(param));
            throw new BusinessException("timer job param error!");
        }
    }

}
