package com.joybean.yogg.task.executor;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jobean
 */
@Component
public class InstantTaskExecutor extends AbstractTaskExecutor {
    private final Logger LOGGER = LoggerFactory
            .getLogger(getClass());
    private Task currentTask;

    @Override
    public TaskReport execute(Task task) {
        currentTask = task;
        return doExecute(task);
    }

    @Override
    public void stop(String taskId) {
        if (currentTask != null) {
            websiteService.shutdown();
            javafx.concurrent.Task task = currentTask.getTaskContext().getJfxTask();
            task.cancel(true);
            LOGGER.info("Stopping instant task...");
            currentTask = null;
        }
    }

    @Override
    public boolean accept(Task task) {
        return StringUtils.isBlank(task.getCronExpr());
    }

    @Override
    public boolean accept(String taskId) {
        return taskId == null || taskId.equals(currentTask.getTaskId());
    }
}
