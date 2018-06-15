package com.system.cron.service;

import com.github.pagehelper.PageHelper;
import com.system.cron.config.TaskConsts;
import com.system.cron.controller.query.TaskQuery;
import com.system.cron.dao.TaskDao;
import com.system.cron.entity.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Task 服务
 * Created by ctianlong on 2018/5/23.
 */
@Service
public class TaskService
{

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public List<Task> listAllTask()
    {
        List<Task> result;
        try
        {
            result = taskDao.selectAll();
        }
        catch (Exception e)
        {
            logger.error("数据库查询所有任务异常", e);
            e.printStackTrace();
            result = new ArrayList<>();
        }
        return result;
    }

    public boolean saveTask(Task task)
    {
        if (null == task)
        {
            return false;
        }
        task.setId(null);
        task.setCreateTime(new Date());
        task.setModifiedTime(new Date());
        try
        {
            if (taskDao.insert(task) != 1)
            {
                logger.error("数据库插入任务异常，{}", task);
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error("数据库插入任务异常，{}", task, e);
            e.printStackTrace();
            return false;
        }
        logger.info("数据库插入任务成功，任务ID/名称：{} / {}", task.getId(), task.getName());
        return true;
    }

    public int countAllTask()
    {
        int result = 0;
        try
        {
            result = taskDao.count();
        }
        catch (Exception e)
        {
            logger.error("数据库统计任务数量异常", e);
            e.printStackTrace();
        }
        return result;
    }

    public List<Task> listTask(TaskQuery taskQuery)
    {
        List<Task> result;
        if (null == taskQuery)
        {
            PageHelper.startPage(TaskConsts.DEFAULT_PAGENUM,
                TaskConsts.DEFAULT_PAGESIZE);
            try
            {
                result = taskDao.selectAll();
            }
            catch (Exception e)
            {
                logger.error("数据库分页查询任务异常", e);
                e.printStackTrace();
                result = new ArrayList<>();
            }
            return result;
        }
        if (taskQuery.getPageNum() != null && taskQuery.getPageSize() != null)
        {
            PageHelper.startPage(taskQuery.getPageNum(),
                taskQuery.getPageSize());
        }
        else
        {
            PageHelper.startPage(TaskConsts.DEFAULT_PAGENUM,
                TaskConsts.DEFAULT_PAGESIZE);
        }
        WeekendSqls<Task> weekendSqls = WeekendSqls.custom();
        if (StringUtils.isNotBlank(taskQuery.getName()))
        {
            weekendSqls.andLike(Task::getName,
                "%" + taskQuery.getName().trim() + "%");
        }
        if (taskQuery.getEnabled() != null)
        {
            weekendSqls.andEqualTo(Task::getEnabled, taskQuery.getEnabled());
        }
        Example.Builder builder =
            new Example.Builder(Task.class).where(weekendSqls);
        String orderColumn = taskQuery.getOrderColumn();
        String orderDir = taskQuery.getOrderDir();
        if (StringUtils.isNotBlank(orderColumn))
        {
            if (StringUtils.isNotBlank(orderDir) &&
                orderDir.trim().equalsIgnoreCase("desc"))
            {
                builder.orderByDesc(orderColumn.trim());
            }
            else
            {
                builder.orderByAsc(orderColumn.trim());
            }
        }
        try
        {
            result = taskDao.selectByExample(builder.build());
        }
        catch (Exception e)
        {
            logger.error("数据库分页查询任务异常", e);
            e.printStackTrace();
            result = new ArrayList<>();
        }
        return result;
    }

    public Task getTaskById(Long id)
    {
        Task result = null;
        try
        {
            result = taskDao.selectByPrimaryKey(id);
        }
        catch (Exception e)
        {
            logger.error("数据库ID查询任务异常", e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 增量更新，只更新属性不为null的值
     *
     * @param task
     * @return
     */
    public boolean updateTaskByIdSelective(Task task)
    {
        return updateTaskByIdIfSelective(task, true);
    }

    /**
     * 全量更新，null或空字符串值会被更新，数据库字段非空约束需要调用层自己保证
     *
     * @param task
     * @return
     */
    public boolean updateTaskById(Task task)
    {
        return updateTaskByIdIfSelective(task, false);
    }

    private boolean updateTaskByIdIfSelective(Task task, boolean selective)
    {
        if (null == task || null == task.getId())
        {
            return false;
        }
        task.setModifiedTime(new Date());
        try
        {
            if ((selective ?
                taskDao.updateByPrimaryKeySelective(task) :
                taskDao.updateById(task)) != 1)
            {
                logger.error("数据库更新任务异常，{}", task);
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error("数据库更新任务异常，{}", task, e);
            e.printStackTrace();
            return false;
        }
        logger.info("数据库更新任务成功，任务ID/名称：{} / {}", task.getId(), task.getName());
        return true;
    }

    public boolean checkExistsById(Long id)
    {
        boolean result = false;
        try
        {
            result = taskDao.existsWithPrimaryKey(id);
        }
        catch (Exception e)
        {
            logger.error("数据库检查任务是否存在，异常，任务ID：{}", id, e);
            e.printStackTrace();
        }
        return result;
    }

    public boolean removeTaskById(Long id)
    {
        if (null == id)
        {
            return false;
        }
        try
        {
            if (taskDao.deleteByPrimaryKey(id) != 1)
            {
                logger.error("数据库删除任务异常，任务ID：{}", id);
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error("数据库删除任务异常，任务ID：{}", id, e);
            e.printStackTrace();
            return false;
        }
        logger.info("数据库删除任务成功，任务ID：{}", id);
        return true;
    }

    public boolean disableTaskById(Long id)
    {
        if (null == id)
        {
            return false;
        }
        try
        {
            if (taskDao.updateIsEnabledById(false, id, new Date()) != 1)
            {
                logger.error("数据库修改任务状态为停用，异常，任务ID：{}", id);
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error("数据库修改任务状态为停用，异常，任务ID：{}", id, e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean enableTaskById(Long id)
    {
        if (null == id)
        {
            return false;
        }
        try
        {
            if (taskDao.updateIsEnabledById(true, id, new Date()) != 1)
            {
                logger.error("数据库修改任务状态为启用，异常，任务ID：{}", id);
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error("数据库修改任务状态为启用，异常，任务ID：{}", id, e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Task> listAllTaskByIsEnabled(Boolean enabled)
    {
        List<Task> result;
        try
        {
            result = taskDao.listAllTaskByIsEnabled(enabled);
        }
        catch (Exception e)
        {
            logger.error("数据库根据是否启用查询任务，异常", e);
            e.printStackTrace();
            result = new ArrayList<>();
        }
        return result;
    }

}
