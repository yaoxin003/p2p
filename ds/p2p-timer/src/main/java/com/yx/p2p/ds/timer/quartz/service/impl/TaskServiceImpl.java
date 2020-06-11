package com.yx.p2p.ds.timer.quartz.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.timer.quartz.enums.JobBizStateEnum;
import com.yx.p2p.ds.timer.quartz.mapper.TaskMapper;
import com.yx.p2p.ds.timer.quartz.model.TaskDO;
import com.yx.p2p.ds.timer.quartz.service.JobService;
import com.yx.p2p.ds.timer.quartz.service.TaskService;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.PageUtil;
import com.yx.p2p.ds.vo.CustomerVo;
import org.apache.ibatis.session.RowBounds;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/10:02
 */
@Service
public class TaskServiceImpl implements TaskService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JobService jobService;

    public List<TaskDO> getTaskDOListByPagination(TaskDO taskDO, Integer currentPage, Integer pageSize){
        logger.debug("【分页查询定时任务】入参：currentPage=" + currentPage + ",pageSize=" + pageSize);
        Integer offset =  PageUtil.getOffset(currentPage,pageSize);
        RowBounds rowBounds = new RowBounds(offset,pageSize);
        List<TaskDO> taskDOList = taskMapper.selectByRowBounds(taskDO, rowBounds);
        logger.debug("【分页查询定时任务】结果：taskDOList" + taskDOList);
        return taskDOList;
    }

    public Integer getTaskDOListCount(TaskDO taskDO){
        int count = taskMapper.selectCount(taskDO);
        return count;
    }

    private List<TaskDO> getAll() {
        List<TaskDO> taskDOList = taskMapper.selectAll();
        return taskDOList;
    }

    @Override
    public void initSchedule() throws SchedulerException {
        List<TaskDO> taskDOList = this.getAll();
        for (TaskDO taskDO : taskDOList) {
            if(JobBizStateEnum.RUNNING.getState().equals(taskDO.getBizState())){
                jobService.addJob(taskDO);
            }
        }    
    }

    //新增的“定时任务”：业务状态为“停止”
    @Override
    public Result add(TaskDO taskDO) {
        Result result = Result.error();
        try{
            BeanHelper.setAddDefaultField(taskDO);
            taskDO.setBizState(JobBizStateEnum.STOP.getState());//停止
            taskMapper.insert(taskDO);
            result = Result.success();
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    @Override
    public TaskDO getTaskById(Integer id) {
        TaskDO taskDO = taskMapper.selectByPrimaryKey(id);
        return taskDO;
    }

    //若修改cron表达式，则删除定时任务后再添加暂定定时任务
    //修改数据库表中数据
    @Override
    public Result updateTask(TaskDO taskDO) {
        Result result = Result.error();
        try{
            TaskDO dbTask = this.getTaskDOById(taskDO.getId());
            if(JobBizStateEnum.STOP.getState().equals(dbTask.getBizState())){
                //若修改“cron表达式”，更新定时任务“时间表达式”
                if(!dbTask.getCronExpression().equals(taskDO.getCronExpression())){
                   jobService.updateJobCron(dbTask,taskDO.getCronExpression());//更新后暂定
                   jobService.pauseJob(dbTask);
                }
                //修改数据库
                BeanHelper.setUpdateDefaultField(taskDO);
                int count = taskMapper.updateByPrimaryKeySelective(taskDO);
                result = Result.success();
                logger.debug("【数据库操作：更新定时任务】count=" + count);
            }else{
                result = Result.error("请先停止定时任务");
            }
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //停止/运行定时任务，再修改数据库
    public Result updateBizState(TaskDO taskDO){
        Result result = Result.error();
        try{
            TaskDO dbTask = this.getTaskDOById(taskDO.getId());
            logger.debug("【修改定时任务状态】入参：taskDO=" + taskDO);
            if(JobBizStateEnum.STOP.getState().equals(taskDO.getBizState())){//停止
                result = jobService.pauseJob(dbTask);
            }else{//恢复
                result = jobService.resumeJob(dbTask);
            }
            if(Result.checkStatus(result)){
                result = this.updateTaskBizState(taskDO);
            }
            result = Result.success();
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result updateTaskBizState(TaskDO taskDO) {
        Result result = Result.error();
        try{
           taskMapper.updateByPrimaryKeySelective(taskDO);
            result = Result.success();
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    public TaskDO getTaskDOById(Integer taskId){
        logger.debug("【使用id查询数据库】入参：taskId=" + taskId);
        TaskDO taskDO = taskMapper.selectByPrimaryKey(taskId);
        logger.debug("【使用id查询数据库】结果：taskDO=" + taskDO);
        return taskDO;
    }

    //先删除定时任务，再删除数据库数据
    @Override
    public Result delete(Integer taskId) {
        Result result = Result.error();
        try{
            TaskDO dbTask = this.getTaskDOById(taskId);
            if(JobBizStateEnum.STOP.getState().equals(dbTask.getBizState())){
                result = jobService.deleteJob(dbTask);
                if(Result.checkStatus(result)){
                    taskMapper.deleteByPrimaryKey(taskId);
                }
                result = Result.success();
            }else{
                result = Result.error("请先停止定时任务");
            }
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    @Override
    public Result run(Integer taskId,String bizDateStr) {
        Result result = Result.error();
        try{
            TaskDO dbTask = this.getTaskDOById(taskId);
            if(JobBizStateEnum.RUNNING.getState().equals(dbTask.getBizState())){
                result = jobService.runJobNow(dbTask,bizDateStr);
                result = Result.success();
            }else{
                result = Result.error("请先启动定时任务");
            }
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }


}
