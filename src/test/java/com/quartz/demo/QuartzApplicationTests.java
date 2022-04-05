package com.quartz.demo;

import com.quartz.demo.web.bean.CronJobParam;
import com.quartz.demo.web.bean.TimeoutJobParam;
import com.quartz.demo.web.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
class QuartzApplicationTests {

    @Autowired
    private SchedulerService schedulerService;

    @Test
    void contextLoads() {
    }

    /**
     * 添加定时任务
     */
    @Test
    void addQuartzJob() {
        CronJobParam param = new CronJobParam();
        Map<String, String> params = new HashMap<>();
        params.put("1", "1");
        param.setRequestParams(params);
        param.setCallBackUrl("https://callback/tirgger");
        param.setCron("0 */1 * * * ? *");
        param.setKey("TEST-CRON");
        param.setGroup("TEST");
        schedulerService.addQuartzJob(param);
    }

    /**
     * 添加超时定时任务
     */
    @Test
    void addQuartzTimeoutJob() {
        TimeoutJobParam param = new TimeoutJobParam();
        Map<String, String> params = new HashMap<>();
        params.put("1", "1");
        param.setRequestParams(params);
        param.setCallBackUrl("https://callback/tirgger");
        param.setTimeoutMinute(1);
        param.setKey("TEST-TIMEOUT-CRON");
        param.setGroup("TEST");
        schedulerService.addQuartzTimeoutJob(param);
    }

    /**
     * 删除定时任务
     */
    @Test
    void deleteQuartzJob() {
        schedulerService.deleteQuartzJob("TEST-CRON","TEST");
        schedulerService.deleteQuartzJob("TEST-TIMEOUT-CRON","TEST");
    }

}
