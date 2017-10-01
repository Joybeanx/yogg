package com.joybean.yogg.report.service;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.Pagination;

import java.util.List;
import java.util.Map;

/**
 * @author jobean
 */
public interface ReportService {

    TaskReport loadTaskReport(String taskId);

    Map<RecordStatus, Long> getStatusCounterMap(String taskId);

    void saveTaskReport(TaskReport taskReport);

    void updateTaskReport(TaskReport taskReport);

    void replaceSMSSendingRecord(List<SMSSendingRecord> smsSendingRecords);

    List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses);

    List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses);

    int countSMSSendingRecord(String taskId, String website, RecordStatus... statuses);

    boolean isTaskReportComplete(String taskId);

    boolean isTaskReportExistent(String taskId);

    void clearTaskReport(String taskId);
}
