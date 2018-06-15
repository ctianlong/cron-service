package com.system.cron.config;

import java.util.regex.Pattern;

/**
 * 任务调度系统常量
 * Created by ctianlong on 2018/6/2.
 */
public class TaskConsts
{
    private TaskConsts()
    {
    }

    public static final Integer DEFAULT_PAGENUM = 1;

    public static final Integer DEFAULT_PAGESIZE = 20;

    public static final String URL_REGEX =
        "https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

}
