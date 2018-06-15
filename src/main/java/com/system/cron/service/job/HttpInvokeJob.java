package com.system.cron.service.job;

import com.system.cron.util.HttpUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP 调用任务
 * Created by ctianlong on 2018/5/31.
 */
public class HttpInvokeJob implements Job
{

    private final Logger logger = LoggerFactory.getLogger(HttpInvokeJob.class);

    public static final String PARAM_URL = "url";

    public static final String PARAM_REQUEST_TYPE = "requestType";

    public static final String PARAM_REQUEST_DATA = "requestData";

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
        throws JobExecutionException
    {
        JobDataMap jobDataMap =
            jobExecutionContext.getJobDetail().getJobDataMap();
        String url = jobDataMap.getString(PARAM_URL);
        int requestType = jobDataMap.getInt(PARAM_REQUEST_TYPE);
        if (requestType == 0)
        {
            try
            {
                HttpUtil.Response resp = HttpUtil.get(url, null, null, true);
                logger.info("HttpInvokeJob SUCCESS, GET {} {}",
                    resp.getStatusCode(),
                    url);
            }
            catch (IOException e)
            {
                logger.error("HttpInvokeJob FAIL, GET {}", url);
                e.printStackTrace();
            }
        }
        else if (requestType == 1)
        {
            String requestData = jobDataMap.getString(PARAM_REQUEST_DATA);
            try
            {
                HttpUtil.Response resp =
                    HttpUtil.post(url, null, null, requestData, true);
                logger.info("HttpInvokeJob SUCCESS, POST {} {} {}",
                    resp.getStatusCode(),
                    url,
                    requestData);
            }
            catch (IOException e)
            {
                logger.error("HttpInvokeJob FAIL, POST {} {}",
                    url,
                    requestData);
                e.printStackTrace();
            }
        }
    }

}
