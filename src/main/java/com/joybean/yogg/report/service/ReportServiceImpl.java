package com.joybean.yogg.report.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.dao.ReportStore;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.record.service.SMSSendingRecordService;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author jobean
 */
@Component
public class ReportServiceImpl implements ReportService {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(ReportServiceImpl.class);
    @Autowired
    private SMSSendingRecordService smsSendingRecordService;
    @Autowired
    private ReportStore reportStore;
    @Autowired
    private YoggConfig config;

    public TaskReport initTaskReport(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        TaskReport taskReport = loadTaskReport(taskId);
        if (taskReport != null) {
            List<SMSSendingRecord> smsSendingRecords = smsSendingRecordService.fetchSMSSendingRecord(taskId, new Pagination());
            taskReport.setRecords(smsSendingRecords);
        }
        return taskReport;
    }


    @Override
    public TaskReport loadTaskReport(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        TaskReport report;
        try {
            String content = reportStore.fetchTaskReport(taskId);
            if (content == null) {
                return null;
            }
            report = JsonUtils.json2Bean(content, TaskReport.class);
        } catch (Exception e) {
            LOGGER.error("Failed to load report by task {}", taskId, e);
            throw new YoggException(e);
        }
        return report;
    }

    @Override
    public Map<RecordStatus, Long> getStatusCounterMap(String taskId) {
        Map<RecordStatus, Long> counterMap = null;
        TaskReport taskReport = loadTaskReport(taskId);
        if (taskReport != null) {
            counterMap = taskReport.getStatistics().getCounterMap();
        }
        return counterMap;
    }

    @Override
    public void saveTaskReport(TaskReport taskReport) {
        updateTaskReport(taskReport);
    }


    @Override
    public void updateTaskReport(TaskReport taskReport) {
        Assert.notNull(taskReport, "Task report must not be null");
        try {
            reportStore.updateTaskReport(taskReport);
        } catch (Exception e) {
            LOGGER.error("Failed to update {}", taskReport, e);
            throw new YoggException(e);
        }
    }

    @Override
    public void replaceSMSSendingRecord(List<SMSSendingRecord> smsSendingRecords) {
        smsSendingRecordService.replaceSMSSendingRecord(smsSendingRecords);
    }


    @Override
    public List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        return smsSendingRecordService.fetchRecordWebsite(taskId, pagination, statuses);
    }

    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        return smsSendingRecordService.fetchSMSSendingRecord(taskId, website, pagination, statuses);
    }

    @Override
    public int countSMSSendingRecord(String taskId, String website, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        return smsSendingRecordService.countSMSSendingRecord(taskId, website, statuses);
    }

    private Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        return smsSendingRecordService.countSMSSendingRecordByStatus(taskId);
    }


    @Override
    public boolean isTaskReportComplete(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        TaskReport taskReport = loadTaskReport(taskId);
        return taskReport != null && taskReport.isComplete() && smsSendingRecordService.ifSMSSendingRecordExist(taskId);
    }


    @Override
    public void clearTaskReport(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            reportStore.deleteTaskReport(taskId);
            smsSendingRecordService.clearSMSSendingRecord(taskId);
        } catch (Exception e) {
            LOGGER.error("Failed to delete report by task {}", taskId, e);
            throw new YoggException(e);
        }

    }


}
