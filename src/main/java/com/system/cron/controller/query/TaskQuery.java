package com.system.cron.controller.query;

/**
 * 封装任务查询参数
 * Created by ctianlong on 2018/5/28.
 */
public class TaskQuery extends PageCommonQuery
{

    private String name;

    private Boolean enabled;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String toString()
    {
        return "TaskQuery{" +
            "name='" + name + '\'' +
            ", enabled=" + enabled +
            "} " + super.toString();
    }
}
