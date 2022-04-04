package com.quartz.demo.quartz.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quartz.demo.utils.BaseHttpClient;
import com.quartz.demo.web.bean.CronJobParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 9:06 下午
 */
@Slf4j
public class CronJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        CronJobParam param = JSON.parseObject((String) context.getJobDetail().getJobDataMap()
                .get(CronJobParam.class.getSimpleName()), CronJobParam.class);
        Map requestParams = param.getRequestParams();
        String response = new BaseHttpClient(param.getCallBackUrl())
                .param(requestParams)
                .timeout(3000)
                .retry(2)
                .post(String.class);
        log.info("call cron job request, param: {}, response: {}", JSONObject.toJSONString(param), response);

    }

}
