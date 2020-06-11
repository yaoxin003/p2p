package com.yx.p2p.ds.timer.quartz.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.timer.quartz.model.TaskDO;
import com.yx.p2p.ds.timer.quartz.service.JobService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/10:07
 */
@Service
public class JobServiceImpl implements JobService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Scheduler scheduler;

    @Override
    public Result addJob(TaskDO task) {
        Result result = Result.error();
        try {
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            Class<? extends Job> jobClass = (Class<? extends Job>) (Class.forName(task.getClassFullName()).newInstance()
                    .getClass());
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(task.getJobName(), task.getJobGroup())// 任务名称和组构成任务key
                    .build();

            //设置时间参数
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put("bizDate", DateUtil.dateYMD2Str(new Date()));

            // 使用cornTrigger规则
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobName(), task.getJobGroup())// 触发器key
                    .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))//当前时间的1秒后
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                    .startNow()//立即生效
                    .build();

            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
                result = Result.success();
                logger.debug("【添加定时任务】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
            }
        } catch (Exception e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

   //删除一个job
    public Result deleteJob(TaskDO task) {
        Result result = Result.error();
        try{
            JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
            scheduler.deleteJob(jobKey);
            result = Result.success();
            logger.debug("【删除定时任务】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    public Result runJobNow(TaskDO task,String bizDateStr){
        Result result = Result.error();
        try {
            JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("bizDate",bizDateStr);
            scheduler.triggerJob(jobKey,jobDataMap);
            result = Result.success();
            logger.debug("【立即定时任务】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
        } catch (SchedulerException e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //暂停
    @Override
    public Result pauseJob(TaskDO task) {
        Result result = Result.error();
        try {
            JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
            scheduler.pauseJob(jobKey);
            result = Result.success();
            logger.debug("【停止定时任务】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
        } catch (SchedulerException e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //恢复运行
    @Override
    public Result resumeJob(TaskDO task) {
        Result result = Result.error();
        try {
            JobKey jobKey = JobKey.jobKey(task.getJobName(), task.getJobGroup());
            scheduler.resumeJob(jobKey);
            result = Result.success();
            logger.debug("【恢复定时任务】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
        } catch (SchedulerException e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //更新job时间表达式
    public Result updateJobCron(TaskDO task,String cronExpression){
        Result result = Result.error();
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobName(), task.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            trigger = trigger.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))//当前时间的1秒后
                    .withSchedule(scheduleBuilder)
                    .startNow()//立即生效
                    .build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            result = Result.success();
            logger.debug("【更新定时任务时间表达式】jobGroup=" + task.getJobGroup() + "jobName=" + task.getJobName() );
        } catch (SchedulerException e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }


}
