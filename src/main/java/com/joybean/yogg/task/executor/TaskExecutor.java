/**
 *
 */
package com.joybean.yogg.task.executor;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.task.Task;

/**
 * Simple executor interface that abstracts the execution of a {@link Task}.
 *
 * @author joybean
 */
public interface TaskExecutor {
    TaskReport execute(Task task);

    void stop(String taskId);

    boolean accept(Task task);

    boolean accept(String taskId);
}
