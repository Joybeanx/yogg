package com.joybean.yogg.task;

import javafx.concurrent.Task;

import java.util.concurrent.ScheduledFuture;

/**
 * @author joybean
 */
public class TaskContext {
    private Task<?> jfxTask;
    private ScheduledFuture<?> scheduledFuture;

    public Task<?> getJfxTask() {
        return jfxTask;
    }

    public void setJfxTask(Task<?> jfxTask) {
        this.jfxTask = jfxTask;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @Override
    public String toString() {
        return "TaskContext{" +
                "jfxTask=" + jfxTask +
                ", scheduledFuture=" + scheduledFuture +
                '}';
    }
}
