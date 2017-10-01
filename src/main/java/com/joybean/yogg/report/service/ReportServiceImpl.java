package com.joybean.yogg.report.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.record.service.SMSSendingRecordService;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        TaskReport report = null;
        try {
            Path reportFilePath = getReportFilePath(taskId);
            if (Files.exists(reportFilePath)) {
                String content = new String(Files.readAllBytes(reportFilePath), Charset.defaultCharset());
                report = JsonUtils.json2Bean(content, TaskReport.class);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load report by task {}", taskId, e);
            throw new YoggException(e);
        }
        return report;
    }

    @Override
    public Map<RecordStatus, Long> getStatusCounterMap(String taskId) {
        Map<RecordStatus, Long> counterMap = null;
        if (config.getDataSource().getDataSourceType() == DataSourceType.DATABASE) {
            counterMap = countSMSSendingRecordByStatus(Task.TASK_ID_MAIN);
        } else {
            TaskReport taskReport = loadTaskReport(Task.TASK_ID_MAIN);
            if (taskReport != null) {
                counterMap = taskReport.getStatistics().getCounterMap();
            }
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
        String taskId = taskReport.getTaskId();
        try {
            String content = JsonUtils.bean2PrettyJson(taskReport);
            Path filePath = getReportFilePath(taskId);
            Path fileDir = filePath.getParent();
            if (fileDir != null && Files.notExists(fileDir)) {
                Files.createDirectory(fileDir);
            }
            Files.write(filePath, content.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
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
    public boolean isTaskReportExistent(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        Path reportFilePath = getReportFilePath(taskId);
        return Files.exists(reportFilePath);
    }


    @Override
    public void clearTaskReport(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            Path reportFilePath = getReportFilePath(taskId);
            if (Files.exists(reportFilePath)) {
                Files.delete(reportFilePath);
            }
            smsSendingRecordService.clearSMSSendingRecord(taskId);
        } catch (IOException e) {
            LOGGER.error("Failed to delete report by task {}", taskId, e);
            throw new YoggException(e);
        }

    }

    private Path getReportFilePath(String taskId) {
        String reportFileName = String.format(config.getReportFileNameFormat(), taskId);
        return Paths.get(reportFileName);
    }


}
