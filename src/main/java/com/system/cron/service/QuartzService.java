package com.system.cron.service;

import com.system.cron.entity.Task;
import com.system.cron.service.job.HttpInvokeJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

import static com.system.cron.util.TaskUtil.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * quartz 任务调度器服务
 * Created by ctianlong on 2018/6/1.
 */
@Service
public class QuartzService
{
    private final Logger logger = LoggerFactory.getLogger(QuartzService.class);

    public static final String DEFAULT_GROUP = "default_group";

    // cron表达式默认运行时区
    public static final String DEFAULT_TIMEZONE_ID = "Asia/Shanghai";

    private final Scheduler scheduler;

    public QuartzService(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    public boolean startOneJob(Task task)
    {
        Long id = task.getId();
        String url = task.getUrl();
        String cronExp = task.getCronExpression();
        if (!(null != id && checkCronExpWithExpired(cronExp) && checkUrl(url) &&
            checkRequestTypeAndDataWithFix(task)))
        {
            logger.error("HttpInvokeJob启动失败，任务参数有误 {}", task);
            return false;
        }
        JobDetail job = newJob(HttpInvokeJob.class)
            .withIdentity(HttpInvokeJob.class.getName() + ":" + id,
                DEFAULT_GROUP)
            .usingJobData(HttpInvokeJob.PARAM_URL, url)
            .usingJobData(HttpInvokeJob.PARAM_REQUEST_TYPE,
                task.getRequestType())
            .usingJobData(HttpInvokeJob.PARAM_REQUEST_DATA,
                task.getRequestData())
            .build();
        Trigger trigger = newTrigger()
            .withIdentity(HttpInvokeJob.class.getName() + ":" + id,
                DEFAULT_GROUP)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExp).inTimeZone(
                TimeZone.getTimeZone(DEFAULT_TIMEZONE_ID)))
            .build();
        try
        {
            scheduler.scheduleJob(job, trigger);
            logger.info("HttpInvokeJob启动成功，任务id：{}", id);
            return true;
        }
        catch (SchedulerException e)
        {
            logger.error("HttpInvokeJob启动异常, {}", task, e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteOneJob(Long id)
    {
        JobKey jobKey = JobKey.jobKey(HttpInvokeJob.class.getName() + ":" + id,
            DEFAULT_GROUP);
        try
        {
            scheduler.deleteJob(jobKey);
            logger.info("HttpInvokeJob停止成功，任务id：{}", id);
            return true;
        }
        catch (SchedulerException e)
        {
            logger.error("HttpInvokeJob删除异常, Task id: {}", id, e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkExistsById(Long id)
    {
        JobKey jobKey = JobKey.jobKey(HttpInvokeJob.class.getName() + ":" + id,
            DEFAULT_GROUP);
        try
        {
            return scheduler.checkExists(jobKey);
        }
        catch (SchedulerException e)
        {
            logger.error("HttpInvokeJob检查是否存在异常, Task id: {}", id, e);
            e.printStackTrace();
        }
        return false;
    }

    public void adjustTaskStatus(Task task)
    {
        long id = task.getId();
        if (task.getEnabled())
        {
            if (checkExistsById(id))
            {
                deleteOneJob(id);
            }
            startOneJob(task);
        }
        else if (checkExistsById(id))
        {
            deleteOneJob(id);
        }
    }

}
