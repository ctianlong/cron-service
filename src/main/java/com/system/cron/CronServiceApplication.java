package com.system.cron;

import com.system.cron.entity.Task;
import com.system.cron.service.QuartzService;
import com.system.cron.service.TaskService;
import com.system.cron.util.TaskUtil;
import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * 任务调度服务启动主类
 */
@SpringBootApplication
@EnableServiceComb
@MapperScan(basePackages = "com.system.cron.dao")
@EnableScheduling
public class CronServiceApplication implements CommandLineRunner
{

    private final Logger logger =
        LoggerFactory.getLogger(CronServiceApplication.class);

    private final Scheduler scheduler;

    private final TaskService taskService;

    private final QuartzService quartzService;

    public CronServiceApplication(Scheduler scheduler, TaskService taskService,
        QuartzService quartzService)
    {
        this.scheduler = scheduler;
        this.taskService = taskService;
        this.quartzService = quartzService;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(CronServiceApplication.class, args);
    }

    @Override
    public void run(String... args)
        throws Exception
    {
        logger.info("服务启动成功");
        scheduler.start();
        logger.info("{} 任务调度器启动成功，初始化任务调度中...", scheduler.getSchedulerName());
        List<Task> tasks = taskService.listAllTaskByIsEnabled(true);
        int dbEnabled = tasks.size();
        int startSuccess = 0;
        int expired = 0;
        int error = 0;
        for (Task t : tasks)
        {
            if (quartzService.startOneJob(t))
            {
                startSuccess++;
                continue;
            }
            String cronExp = t.getCronExpression();
            if (TaskUtil.checkCronExp(cronExp) &&
                !TaskUtil.checkCronExpWithExpired(cronExp))
            {
                taskService.disableTaskById(t.getId());
                expired++;
                continue;
            }
            error++;
        }
        String report = "数据库启动状态任务数：{}，成功启动任务数：{}，过期任务数：{}，异常任务数：{}";
        if (error == 0)
        {
            logger.info(report, dbEnabled, startSuccess, expired, error);
        }
        else
        {
            logger.error(report, dbEnabled, startSuccess, expired, error);
        }
    }
}


