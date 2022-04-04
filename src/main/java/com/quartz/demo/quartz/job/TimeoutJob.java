package com.quartz.demo.quartz.job;

import com.alibaba.fastjson.JSON;
import com.quartz.demo.utils.BaseHttpClient;
import com.quartz.demo.web.bean.TimeoutJobParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 9:11 下午
 */
@Slf4j
public class TimeoutJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        TimeoutJobParam param = JSON.parseObject((String) context.getJobDetail().getJobDataMap()
                .get(TimeoutJobParam.class.getSimpleName()), TimeoutJobParam.class);
        String response = new BaseHttpClient(param.getCallBackUrl())
                .param(param.getRequestParams())
                .timeout(3000)
                .retry(2)
                .post(String.class);
        log.info("call timeout job request, param: {}, response: {}", JSON.toJSONString(param), response);
    }

}
