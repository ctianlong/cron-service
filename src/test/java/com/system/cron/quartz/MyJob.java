package com.system.cron.quartz;

//import org.quartz.*;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

/**
 * Created by ctianlong on 2018/5/16.
 */
//@PersistJobDataAfterExecution
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println(dataMap.getString("arg"));
//        dataMap.replace("arg", "v2");
//        dataMap.put("arg", "v2"); // put和replace都能替换掉原来的键值对
        System.out.println("this is a job, time: " + LocalDateTime.now());
    }
}
