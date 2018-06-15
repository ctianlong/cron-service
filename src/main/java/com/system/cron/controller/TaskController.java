package com.system.cron.controller;

import com.github.pagehelper.PageInfo;
import com.system.cron.common.rest.RestResult;
import com.system.cron.common.rest.ResultStatus;
import com.system.cron.controller.query.TaskQuery;
import com.system.cron.entity.Task;
import com.system.cron.service.QuartzService;
import com.system.cron.service.TaskService;
import com.system.cron.util.TaskUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.system.cron.common.rest.RestResult.*;

/**
 * 任务调度前端控制器，暴露 API
 * Created by ctianlong on 2018/5/23.
 */
@RestSchema(schemaId = "task")
@RequestMapping(path = "/task", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController
{
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    private final QuartzService quartzService;

    public TaskController(TaskService taskService, QuartzService quartzService)
    {
        this.taskService = taskService;
        this.quartzService = quartzService;
    }

    /**
     * 分页查询任务列表
     * 该API本来应该为 GET /，但是CSE中在没用 @RequestBody的情况下不支持复杂类型的参数映射，只能用 @RequestParam挨个映射
     * 当参数比较多的时候，@RequestParam显然不方便，故将其改为 POST，使用 @RequestBody 映射
     *
     * @param taskQuery 查询条件
     * @return 任务分页列表
     */
    @PostMapping(path = "/query")
    public RestResult<PageInfo<Task>> listTask(@RequestBody TaskQuery taskQuery)
    {
        List<Task> tasks = taskService.listTask(taskQuery);
        return successData(new PageInfo<>(tasks));
    }

    /**
     * id查询单个任务
     *
     * @param id 任务id
     * @return 单个任务
     */
    @GetMapping(path = "/{id}")
    public RestResult<Task> getTask(@PathVariable(name = "id") Long id)
    {
        Task task = taskService.getTaskById(id);
        return null == task ? fail() : successData(task);
    }

    /**
     * 添加新任务
     *
     * @param task 新任务对象
     * @return 保存后的该任务对象，含id
     */
    @PostMapping(path = "/")
    public RestResult<Task> saveTask(@RequestBody Task task)
    {
        if (!checkParam(task))
        {
            logger.warn("添加任务，请求参数无效");
            return error(ResultStatus.INVALID_PARAM);
        }
        if (!taskService.saveTask(task))
            return fail(ResultStatus.FAIL_ADD);
        quartzService.adjustTaskStatus(task);
        return successData(ResultStatus.SUCCESS_ADD, task);
    }

    /**
     * 全量更新任务，所有属性都会被更新
     *
     * @param id   任务id
     * @param task 更新任务对象
     * @return
     */
    @PutMapping(path = "/{id}")
    public RestResult<Void> updateTask(@PathVariable(name = "id") Long id,
        @RequestBody Task task)
    {
        if (!checkParam(task))
        {
            logger.warn("更新任务，请求参数无效，任务ID：{}", id);
            return error(ResultStatus.INVALID_PARAM);
        }
        task.setId(id);
        if (!taskService.updateTaskById(task))
            return fail(ResultStatus.FAIL_UPDATE);
        quartzService.adjustTaskStatus(task);
        return success(ResultStatus.SUCCESS_UPDATE);
    }

    /**
     * 增量更新任务，只更新属性不为null的值
     *
     * @param id   任务id
     * @param task 更新任务对象
     * @return
     */
    @PatchMapping(path = "/{id}")
    public RestResult<Void> updateTaskSelective(
        @PathVariable(name = "id") Long id,
        @RequestBody Task task)
    {
        if (!checkParamSelective(task))
        {
            logger.warn("更新任务，请求参数无效，任务ID：{}", id);
            return error(ResultStatus.INVALID_PARAM);
        }
        task.setId(id);
        if (!taskService.updateTaskByIdSelective(task))
            return fail(ResultStatus.FAIL_UPDATE);
        task = taskService.getTaskById(id);
        quartzService.adjustTaskStatus(task);
        return success(ResultStatus.SUCCESS_UPDATE);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return 删除状态
     */
    @DeleteMapping(path = "/{id}")
    public RestResult<Void> removeTask(@PathVariable(name = "id") Long id)
    {
        if (!taskService.removeTaskById(id))
            return fail(ResultStatus.FAIL_DELETE);
        if (quartzService.checkExistsById(id))
            quartzService.deleteOneJob(id);
        return success(ResultStatus.SUCCESS_DELETE);
    }

    /**
     * 启动任务
     *
     * @param id 任务id
     * @return 启动状态
     */
    @PostMapping(path = "/{id}/start")
    public RestResult<Void> startTask(@PathVariable(name = "id") Long id)
    {
        Task task = taskService.getTaskById(id);
        if (null == task)
        {
            if (quartzService.checkExistsById(id))
                quartzService.deleteOneJob(id);
            return fail();
        }
        if (quartzService.checkExistsById(id))
        {
            if (!task.getEnabled())
                taskService.enableTaskById(id);
            return success();
        }
        if (quartzService.startOneJob(task))
        {
            if (!task.getEnabled())
                taskService.enableTaskById(id);
            return success();
        }
        if (task.getEnabled())
            taskService.disableTaskById(id);
        return fail();
    }

    /**
     * 停止任务
     *
     * @param id 任务id
     * @return 停止状态
     */
    @PostMapping(path = "/{id}/stop")
    public RestResult<Void> stopTask(@PathVariable(name = "id") Long id)
    {
        Task task = taskService.getTaskById(id);
        if (null == task)
        {
            if (quartzService.checkExistsById(id))
                quartzService.deleteOneJob(id);
            return fail();
        }
        if (!quartzService.checkExistsById(id))
        {
            if (task.getEnabled())
                taskService.disableTaskById(id);
            return success();
        }
        if (quartzService.deleteOneJob(id))
        {
            if (task.getEnabled())
                taskService.disableTaskById(id);
            return success();
        }
        if (!task.getEnabled())
            taskService.enableTaskById(id);
        return fail();
    }

    /**
     * 检查任务参数，全部检查
     *
     * @param task 任务
     * @return 参数正确性
     */
    private boolean checkParam(Task task)
    {
        // trim
        if (null != task.getName())
            task.setName(task.getName().trim());
        if (null != task.getCronExpression())
            task.setCronExpression(task.getCronExpression().trim());
        if (null != task.getUrl())
            task.setUrl(task.getUrl().trim());
        if (null != task.getRequestData())
            task.setRequestData(task.getRequestData().trim());
        return StringUtils.isNotBlank(task.getName())
            && null != task.getEnabled()
            && TaskUtil.checkCronExpWithExpired(task.getCronExpression())
            && TaskUtil.checkUrl(task.getUrl())
            && TaskUtil.checkRequestTypeAndDataWithFix(task);
    }

    /**
     * 选择性检查任务参数，只检查不为null的参数
     *
     * @param task
     * @return
     */
    private boolean checkParamSelective(Task task)
    {
        // trim
        if (null != task.getName())
            task.setName(task.getName().trim());
        if (null != task.getCronExpression())
            task.setCronExpression(task.getCronExpression().trim());
        if (null != task.getUrl())
            task.setUrl(task.getUrl().trim());
        if (null != task.getRequestData())
            task.setRequestData(task.getRequestData().trim());
        String name = task.getName();
        String cronExp = task.getCronExpression();
        String url = task.getUrl();
        Integer requestType = task.getRequestType();
        boolean result = (null == name || StringUtils.isNotBlank(name))
            && (null == cronExp || TaskUtil.checkCronExpWithExpired(cronExp))
            && (null == url || TaskUtil.checkUrl(url))
            && (null == requestType || TaskUtil.checkRequestType(requestType))
            && TaskUtil.checkRequestDataWithFix(task);
        return result;
    }

}
