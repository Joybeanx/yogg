package com.joybean.yogg.task.executor;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.support.JFXUtils;
import com.joybean.yogg.task.Task;
import com.joybean.yogg.task.TaskContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author jobean
 */
@Component
public class ScheduledTaskExecutor extends AbstractTaskExecutor {
    private final Logger LOGGER = LoggerFactory
            .getLogger(ScheduledTaskExecutor.class);
    @Autowired
    private TaskScheduler scheduler;
    private final Map<String, Task> scheduledTaskMap = new ConcurrentHashMap<>();

    @Override
    public TaskReport execute(Task task) {
        String cronExpr = task.getCronExpr();
        javafx.concurrent.Task jfxTask = JFXUtils.newJFXTask(() -> doExecute(task));
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(jfxTask, new CronTrigger(cronExpr));
        task.getTaskContext().setJfxTask(jfxTask);
        task.getTaskContext().setScheduledFuture(scheduledFuture);
        scheduledTaskMap.put(task.getTaskId(), task);
        return null;
    }

    @Override
    public void stop(String taskId) {
        Task task = scheduledTaskMap.get(taskId);
        if (task != null) {
            TaskContext taskContext = task.getTaskContext();
            javafx.concurrent.Task jfxTask = taskContext.getJfxTask();
            if (jfxTask != null) {
                jfxTask.cancel(true);
            }
            ScheduledFuture<?> scheduledFuture = taskContext.getScheduledFuture();
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            scheduledTaskMap.remove(taskId);
            LOGGER.info("Scheduled task {} has stopped", taskId);
        }
    }

    @Override
    public boolean accept(Task task) {
        return StringUtils.isNotBlank(task.getCronExpr());
    }

    @Override
    public boolean accept(String taskId) {
        return scheduledTaskMap.get(taskId) != null;
    }
}
