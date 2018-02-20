package com.joybean.yogg.report.dao;

import com.joybean.yogg.report.TaskReport;

/**
 * @author jobean
 */
public interface ReportStore {
    String fetchTaskReport(String taskId);

    void saveTaskReport(TaskReport taskReport);

    void updateTaskReport(TaskReport taskReport);

    void deleteTaskReport(String taskId);
}
