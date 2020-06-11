package com.yx.p2p.ds.timer.quartz.controller;

import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.timer.quartz.enums.JobBizStateEnum;
import com.yx.p2p.ds.timer.quartz.model.TaskDO;
import com.yx.p2p.ds.timer.quartz.service.TaskService;
import com.yx.p2p.ds.vo.CustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/11:47
 */
@Controller
@RequestMapping("task")
public class TaskController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskService taskService;

    @RequestMapping("init")
    public String init(){
        logger.debug("【定时任务页面】");
        return "init";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(String description,Integer page, Integer rows){
        logger.debug("【定时任务列表页面】入参：page=" + page + ",rows=" + rows + ",description=" + description);
        TaskDO param= new TaskDO();
        if(description != null && !"".equals(description)){
            param.setDescription(description);
        }
        int totalCount = taskService.getTaskDOListCount(param);
        List<TaskDO> taskDOList = taskService.getTaskDOListByPagination(param,page,rows);
        Map<String, Object> pageMap = Pagination.buildMap(totalCount, taskDOList);
        logger.debug("【定时任务列表页面】结果：taskDOList=" + taskDOList);
        return pageMap;
    }

    /**
     * 添加定时任务
     * 新添加的定时任务，默认设置为停止。需要手工运行
     */
    @RequestMapping("add")
    @ResponseBody
    public Result add(TaskDO taskDO) {
        logger.debug("【添加定时任务】入参：taskDO=" + taskDO);
        return taskService.add(taskDO);
    }

    @RequestMapping("getById")
    @ResponseBody
    public TaskDO getById(Integer id){
        logger.debug("【用id查询定时任务】入参：id=" + id);
        TaskDO taskDO = taskService.getTaskById(id);
        logger.debug("【用id查询定时任务】结果：taskDO=" + taskDO);
        return taskDO;
    }

    /**
     * 修改定时任务
     */
    @RequestMapping("update")
    @ResponseBody
    public Result update(TaskDO taskDO){
        logger.debug("【修改定时任务】入参：taskDO=" + taskDO);
        return taskService.updateTask(taskDO);
    }

    @RequestMapping("updateBizState")
    @ResponseBody
    public Result updateBizState(TaskDO taskDO){
        logger.debug("【修改定时任务状态页面】入参：taskDO=" + taskDO);
        return taskService.updateBizState(taskDO);
    }

    @RequestMapping("delete")
    @ResponseBody
    public Result delete(Integer taskId){
        logger.debug("【删除定时任务】入参：taskId=" + taskId);
        return taskService.delete(taskId);
    }

    @RequestMapping("run")
    @ResponseBody
    public Result run(Integer taskId,String bizDate){
        logger.debug("【执行定时任务】入参：taskId=" + taskId + ",bizDate=" + bizDate);
        return taskService.run(taskId,bizDate);
    }


}
