package com.joybean.yogg.task.service;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.task.Task;
import com.joybean.yogg.task.TaskStatus;
import com.joybean.yogg.task.executor.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author joybean
 */
@Service
public class TaskServiceImpl implements TaskService {
    private final Logger LOGGER = LoggerFactory
            .getLogger(TaskServiceImpl.class);
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Override
    public List<Task> queryTask(String taskId) {
        return null;
    }

    @Override
    public Task createTask(String name, String cronExpr, String... targetPhoneNumbers) {
        Assert.notEmpty(targetPhoneNumbers, "Target phone numbers must not be empty");
        Task task = new Task(UUID.randomUUID().toString());
        task.setTaskName(name);
        task.setCronExpr(cronExpr);
        task.setTargetPhoneNumbers(Arrays.asList(targetPhoneNumbers));
        task.setTaskStatus(TaskStatus.READY);
        task.setCreateTime(new Date());
        return task;
    }

    @Override
    public Task createTask(String... targetPhoneNumbers) {

        return createTask(null, null, targetPhoneNumbers);
    }

    @Override
    public void saveTask(Task task) {

    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void deleteTask(String taskId) {

    }

    @Override
    public TaskReport startTask(Task task) {
        return taskExecutor.execute(task);
    }

    @Override
    public void stopTask(String taskId) {
        taskExecutor.stop(taskId);
    }
}
