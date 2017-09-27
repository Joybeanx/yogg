package com.joybean.yogg.task.executor;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.task.Task;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author jobean
 */
@Component("taskExecutor")
public class TaskExecutorImpl extends AbstractTaskExecutor {
    @Override
    public TaskReport execute(Task task) {
        Assert.notNull(task, "Task must not be null");
        return getTaskExecutor(task).execute(task);
    }

    @Override
    public void stop(String taskId) {
        getTaskExecutor(taskId).stop(taskId);
    }

    @Override
    public boolean accept(Task task) {
        return false;
    }


    @Override
    public boolean accept(String taskId) {
        return false;
    }

    private TaskExecutor getTaskExecutor(Task task) {
        for (TaskExecutor executor : getTaskExecutorList()) {
            if (executor.accept(task)) {
                return executor;
            }
        }
        throw new YoggException("No matched task executor found by %s", task);
    }

    private TaskExecutor getTaskExecutor(String taskId) {
        for (TaskExecutor executor : getTaskExecutorList()) {
            if (executor.accept(taskId)) {
                return executor;
            }
        }
        throw new YoggException("No matched task executor found by task id [%s]", taskId);
    }
}
