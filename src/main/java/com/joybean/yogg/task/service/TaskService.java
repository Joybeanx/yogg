package com.joybean.yogg.task.service;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.task.Task;

import java.util.List;

/**
 * @author jobean
 */
public interface TaskService {
    List<Task> queryTask(String taskId);

    Task createTask(String name, String cronExpr, String... targetPhoneNumbers);

    Task createTask(String... targetPhoneNumbers);

    void saveTask(Task task);

    void updateTask(Task task);

    void deleteTask(String taskId);

    TaskReport startTask(Task task);

    void stopTask(String taskId);
}
