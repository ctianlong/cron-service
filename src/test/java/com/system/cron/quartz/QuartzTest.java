package com.system.cron.quartz;

import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.propertyeditors.ZoneIdEditor;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by ctianlong on 2018/5/16.
 */
public class QuartzTest {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(MyJob.class)
                    .withIdentity("job1", "group1")
                    .usingJobData("arg", "v1")
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(5)
                            .repeatForever())
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
//            job.getJobDataMap().replace("arg", "v2"); // 无法修改
            System.out.println(scheduler.getSchedulerName());
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        final String exp = "0 */19 0 9 6 ? 2018";
        try
        {
            CronExpression c = new CronExpression(exp);
            c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            Date firstTime = c.getTimeAfter(new Date());
            System.out.println(firstTime);
        }
        catch (ParseException e)
        {
            System.out.println("e");
            e.printStackTrace();
        }


    }
}



