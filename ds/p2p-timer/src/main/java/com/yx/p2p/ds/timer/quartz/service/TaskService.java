package com.yx.p2p.ds.timer.quartz.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.timer.quartz.model.TaskDO;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/9:43
 */
public interface TaskService {

    public List<TaskDO> getTaskDOListByPagination(TaskDO taskDO, Integer currentPage, Integer pageSize);

    public Integer getTaskDOListCount(TaskDO taskDO);

    public void initSchedule() throws SchedulerException;

    public Result add(TaskDO taskDO);

    public Result updateTask(TaskDO taskDO);

    //业务状态改为“停止”，删除定时任务并修改数据库业务状态
    //业务状态改为“运行”，添加定时任务并修改数据库业务状态
    public Result updateBizState(TaskDO taskDO);

    public TaskDO getTaskById(Integer taskId);

    public Result delete(Integer taskId);

    public Result run(Integer taskId,String bizDateStr);


}
