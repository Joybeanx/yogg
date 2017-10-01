package com.joybean.yogg.task;

import org.springframework.core.task.TaskExecutor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A task that executed by {@link TaskExecutor}
 *
 * @author jobean
 */
public class Task implements Serializable {

    private static final long serialVersionUID = -7076930824506084196L;
    public final static String TASK_ID_MAIN = "main";
    private String taskId;
    private String taskName;
    /**
     * Phone numbers to be attacked
     */
    private List<String> targetPhoneNumbers;
    /**
     * Cron expression that enables task scheduling
     */
    private String cronExpr;
    /**
     * Task status
     */
    private TaskStatus taskStatus;
    /**
     * Next execution time of a schedule task(when cron expression is specified)
     */
    private Date nextExecutionTime;
    /**
     * Task context
     */
    private transient TaskContext taskContext = new TaskContext();
    /**
     * Time when task was created
     */
    private Date createTime;

    public Task(String taskId) {
        this.taskId = taskId;
    }

    public Task() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getTargetPhoneNumbers() {
        return targetPhoneNumbers;
    }

    public void setTargetPhoneNumbers(List<String> targetPhoneNumbers) {
        this.targetPhoneNumbers = targetPhoneNumbers;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getNextExecutionTime() {
        //findbugs:May expose internal representation by returning reference to mutable object
        if (nextExecutionTime != null) {
            return new Date(nextExecutionTime.getTime());
        }
        return null;
    }

    public void setNextExecutionTime(Date nextExecutionTime) {
        if (nextExecutionTime == null) {
            this.nextExecutionTime = null;
        } else {
            this.nextExecutionTime = new Date(nextExecutionTime.getTime());
        }
    }

    public Date getCreateTime() {
        //findbugs:May expose internal representation by returning reference to mutable object
        if (createTime != null) {
            return new Date(createTime.getTime());
        }
        return null;
    }

    public void setCreateTime(Date createTime) {
        if (createTime == null) {
            this.createTime = null;
        } else {
            this.createTime = new Date(createTime.getTime());
        }
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", targetPhoneNumbers=" + targetPhoneNumbers +
                ", cronExpr='" + cronExpr + '\'' +
                ", taskStatus=" + taskStatus +
                ", nextExecutionTime=" + nextExecutionTime +
                ", taskContext=" + taskContext +
                ", createTime=" + createTime +
                '}';
    }
}
