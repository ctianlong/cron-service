package com.system.cron.util;

import com.system.cron.config.TaskConsts;
import com.system.cron.entity.Task;
import com.system.cron.service.QuartzService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Description
 * @Auther ctl
 * @Date 2018/6/8
 */
public class TaskUtil
{
    private TaskUtil()
    {
    }

    /**
     * 检查 cron表达式格式，不考虑是否过期
     *
     * @param cronExp
     * @return
     */
    public static boolean checkCronExp(String cronExp)
    {
        try
        {
            new CronExpression(cronExp);
            return true;
        }
        catch (ParseException var2)
        {
            return false;
        }
    }

    /**
     * 检查 cron表达式格式，考虑是否过期
     *
     * @param cronExp
     * @return
     */
    public static boolean checkCronExpWithExpired(String cronExp)
    {
        try
        {
            CronExpression c = new CronExpression(cronExp);
            c.setTimeZone(TimeZone.getTimeZone(QuartzService.DEFAULT_TIMEZONE_ID));
            return c.getTimeAfter(new Date()) != null;
        }
        catch (ParseException var2)
        {
            return false;
        }
    }

    /**
     * 检查 URL 格式
     *
     * @param url
     * @return
     */
    public static boolean checkUrl(String url)
    {
        if (null == url)
        {
            return false;
        }
        return TaskConsts.URL_PATTERN.matcher(url).matches();
    }

    /**
     * 检查请求类型和请求数据格式，json
     *
     * @param task
     * @return
     */
    public static boolean checkRequestTypeAndDataWithFix(Task task)
    {
        Integer requestType = task.getRequestType();
        String requestData = task.getRequestData();
        if (null == requestType)
        {
            return false;
        }
        if (requestType.equals(0))
        {
            return StringUtils.isEmpty(requestData);
        }
        if (requestType.equals(1))
        {
            if (StringUtils.isEmpty(requestData))
            {
                return true;
            }
            String format = JsonValidator.formatJson(requestData);
            if (new JsonValidator().validate(format))
            {
                task.setRequestData(format);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 单独检查请求类型
     *
     * @param requestType
     * @return
     */
    public static boolean checkRequestType(Integer requestType)
    {
        return requestType != null &&
            (requestType.equals(0) || requestType.equals(1));
    }

    /**
     * 单独检查请求数据格式，json
     * @param task
     * @return
     */
    public static boolean checkRequestDataWithFix(Task task)
    {
        String requestData = task.getRequestData();
        if (StringUtils.isEmpty(requestData))
        {
            return true;
        }
        String format = JsonValidator.formatJson(requestData);
        if (new JsonValidator().validate(format))
        {
            task.setRequestData(format);
            return true;
        }
        return false;
    }

}
