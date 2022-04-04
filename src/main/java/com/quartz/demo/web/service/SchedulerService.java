package com.quartz.demo.web.service;

import com.quartz.demo.web.bean.CronJobParam;
import com.quartz.demo.web.bean.TimeoutJobParam;
import org.quartz.JobDetail;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 8:33 下午
 */
public interface SchedulerService {

    /**
     * 添加定时任务
     *
     * @param param
     */
    void addQuartzJob(CronJobParam param);

    /**
     * 添加超时定时任务
     *
     * @param param
     */
    void addQuartzTimeoutJob(TimeoutJobParam param);

    /**
     * 删除定时任务
     *
     * @param key
     * @param group
     */
    void deleteQuartzJob(String key, String group);

    /**
     * 获取任务信息
     *
     * @param key
     * @param group
     * @return
     */
    JobDetail getJob(String key, String group);
}
