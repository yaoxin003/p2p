package com.yx.p2p.ds.timer.quartz.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.timer.quartz.model.TaskDO;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/10:06
 */
public interface JobService {

    public Result addJob(TaskDO taskDO);

   //删除
    public Result deleteJob(TaskDO task);

    //运行
    public Result runJobNow(TaskDO task,String bizDateStr);

    //暂停
    public Result pauseJob(TaskDO task);

    //恢复运行
    public Result resumeJob(TaskDO task);

    //更新job时间表达式
    public Result updateJobCron(TaskDO task,String cronExpression);

}
