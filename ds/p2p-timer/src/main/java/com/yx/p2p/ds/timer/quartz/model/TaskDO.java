package com.yx.p2p.ds.timer.quartz.model;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/9:50
 */
@Table(name="quartz_task")
public class TaskDO extends BaseModel {

    // 任务名称
    private String jobName;
    // 任务描述
    private String description;
    // cron表达式
    private String cronExpression;
    // 任务执行时调用哪个类的方法 类全名：包名+类名
    private String classFullName;
    // 任务分组
    private String jobGroup;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @Override
    public String toString() {
        return "TaskDO{" +
                "jobName='" + jobName + '\'' +
                ", description='" + description + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", classFullName='" + classFullName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                '}' + super.toString();
    }
}
