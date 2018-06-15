package com.system.cron.config;

import com.system.cron.entity.Task;
import com.system.cron.service.QuartzService;
import com.system.cron.service.TaskService;
import com.system.cron.util.TaskUtil;
import org.quartz.*;
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务调度系统固有的定时任务，用spring 自带 scheduler 实现，和quartz分离，容错
 * Created by ctianlong on 2018/6/7.
 */
@Component
public class SystemJobs
{
    private final Logger logger = LoggerFactory.getLogger(SystemJobs.class);

    public static final long ONE_MINUTE = 60 * 1000;

    public static final long QUARTER_HOUR = 15 * 60 * 1000;

    public static final long HALF_HOUR = 30 * 60 * 1000;

    private final TaskService taskService;

    private final QuartzService quartzService;

    private final Scheduler scheduler;

    public SystemJobs(TaskService taskService, QuartzService quartzService,
        Scheduler scheduler)
    {
        this.taskService = taskService;
        this.quartzService = quartzService;
        this.scheduler = scheduler;
    }

    /**
     * 任务哨兵，定时检查数据库任务状态与程序实际任务运行状态，使其保持一致
     */
    @Scheduled(initialDelay = HALF_HOUR, fixedRate = HALF_HOUR)
    public void taskSentinel()
    {
        try
        {
            if (scheduler.isShutdown())
            {
                scheduler.start();
            }
        }
        catch (SchedulerException e)
        {
            logger.error("任务检查，检测scheduler状态失败或开启scheduler失败");
            e.printStackTrace();
        }
        List<Task> tasks = taskService.listAllTask();
        Set<JobKey> jobKeys;
        try
        {
            jobKeys =
                scheduler.getJobKeys(GroupMatcher.groupEquals(QuartzService.DEFAULT_GROUP));
        }
        catch (SchedulerException e)
        {
            logger.error("任务检查，从scheduler中获取全部任务失败");
            e.printStackTrace();
            return;
        }
        int dbTotal = tasks.size();
        int jobTotal = jobKeys.size();
        int dbEnabled = 0;
        int error = 0;
        int errorSolved = 0;
        int expired = 0;
        Set<Long> jobIds = jobKeys.stream()
            .map((key) -> Long.parseLong(key.getName().split(":")[1]))
            .collect(Collectors.toSet());
        for (Task t : tasks)
        {
            Long id = t.getId();
            if (t.getEnabled())
            {
                dbEnabled++;
                if (jobIds.contains(id))
                {
                    jobIds.remove(id);
                }
                else
                {
                    String cronExp = t.getCronExpression();
                    if (TaskUtil.checkCronExp(cronExp) &&
                        !TaskUtil.checkCronExpWithExpired(cronExp))
                    {
                        taskService.disableTaskById(t.getId());
                        expired++;
                    }
                    else
                    {
                        error++;
                        if (quartzService.startOneJob(t))
                        {
                            errorSolved++;
                        }
                    }
                }
            }
            else if (jobIds.contains(id))
            {
                error++;
                if (quartzService.deleteOneJob(id))
                {
                    errorSolved++;
                }
                jobIds.remove(id);
            }
        }
        if (!jobIds.isEmpty())
        {
            error += jobIds.size();
            for (Long id : jobIds)
            {
                if (quartzService.deleteOneJob(id))
                {
                    errorSolved++;
                }
            }
        }
        String report =
            "数据库总任务数：{}，数据库启用状态任务数：{}，实际启用任务数：{}，过期任务数：{}，异常任务数：{}，解决异常任务数：{}";
        if (error == 0)
        {
            logger.info(report,
                dbTotal,
                dbEnabled,
                jobTotal,
                expired,
                error,
                errorSolved);
        }
        else
        {
            logger.error(report,
                dbTotal,
                dbEnabled,
                jobTotal,
                expired,
                error,
                errorSolved);
        }

    }

}
