package com.quartz.demo.quartz.scheduler;

import com.alibaba.fastjson.JSON;
import com.quartz.demo.quartz.job.CronJob;
import com.quartz.demo.quartz.job.TimeoutJob;
import com.quartz.demo.web.bean.CronJobParam;
import com.quartz.demo.web.bean.TimeoutJobParam;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 定时任务CRUD类
 *
 * @author xiaoxuxuy
 * @date 2022/4/4 9:02 下午
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TriggerScheduler {

    private static final int MIN_TO_MILLISECONDS = 60 * 1000;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    public void startJob(CronJobParam param) throws SchedulerException {
        String key = param.getKey();
        String group = param.getGroup();
        // 注册前先移除任务
        removeJob(key, group);
        JobDetail jobDetail = JobBuilder.newJob(CronJob.class).withIdentity(key, group).build();
        jobDetail.getJobDataMap().put(CronJobParam.class.getSimpleName(), JSON.toJSONString(param));
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(param.getCron());
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(key, group)
                .withSchedule(scheduleBuilder).build();
        schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, cronTrigger);
    }

    public void startJob(TimeoutJobParam param) throws SchedulerException {
        String key = param.getKey();
        String group = param.getGroup();
        // 注册前先移除任务
        removeJob(key, group);
        JobDetail jobDetail = JobBuilder.newJob(TimeoutJob.class).withIdentity(key, group).build();
        jobDetail.getJobDataMap().put(TimeoutJobParam.class.getSimpleName(), JSON.toJSONString(param));
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .startAt(getStartTime(param.getTimeoutMinute()))
                .withIdentity(key, group)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, simpleTrigger);
    }

    private Date getStartTime(int timeout) {
        return new Date(System.currentTimeMillis() + (long) timeout * MIN_TO_MILLISECONDS);
    }

    /**
     * 移除任务
     *
     * @param key
     * @param group
     */
    public void removeJob(String key, String group) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.deleteJob(JobKey.jobKey(key, group));
    }

    /**
     * 获取任务信息
     *
     * @param key
     * @param group
     * @return
     * @throws SchedulerException
     */
    public JobDetail getJob(String key, String group) throws SchedulerException {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(group)) {
            return null;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        return scheduler.getJobDetail(new JobKey(key, group));
    }

}
