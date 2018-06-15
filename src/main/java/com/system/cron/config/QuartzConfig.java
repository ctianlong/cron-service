package com.system.cron.config;

import org.quartz.Scheduler;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * 任务调度quartz配置
 * Created by ctianlong on 2018/5/31.
 */
@Configuration
public class QuartzConfig
{

    private final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

    @Bean
    public QuartzJobFactory quartzJobFactory()
    {
        return new QuartzJobFactory();
    }

    @Bean
    public Properties quartzProperties()
    {
        PropertiesFactoryBean propertiesFactoryBean =
            new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(
            "/quartz.properties"));
        Properties properties = null;
        try
        {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        }
        catch (IOException e)
        {
            logger.error("Cannot load quartz.properties");
            e.printStackTrace();
        }
        return properties;
    }

    @Bean
    public Scheduler scheduler()
        throws Exception
    {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setQuartzProperties(quartzProperties());
        factoryBean.setJobFactory(quartzJobFactory());
        factoryBean.afterPropertiesSet();
        return factoryBean.getScheduler();
    }

    //解决Job中注入Spring Bean为null的问题
    private class QuartzJobFactory extends AdaptableJobFactory
    {
        @Autowired
        private AutowireCapableBeanFactory capableBeanFactory;

        protected Object createJobInstance(TriggerFiredBundle bundle)
            throws Exception
        {
            Object jobInstance = super.createJobInstance(bundle);
            capableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }
    }

}
