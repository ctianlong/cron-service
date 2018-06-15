package com.system.cron.dao;

import com.system.cron.entity.Task;
import com.system.cron.util.mybatis.MyMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务 DAO
 */
@CacheNamespaceRef(TaskDao.class)
public interface TaskDao extends MyMapper<Task>
{
    int count();

    int updateIsEnabledById(@Param("enabled") Boolean enabled,
        @Param("id") Long id, @Param("modifiedTime") Date modifiedTime);

    List<Task> listAllTaskByIsEnabled(@Param("enabled") Boolean enabled);

    // 全量更新，null或空字符串值会被更新，数据库字段非空约束需要调用层自己保证
    int updateById(Task task);
}