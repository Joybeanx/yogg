package com.joybean.yogg.report.record.dao;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jobean
 */
@Repository
public class SMSSendingRecordFileStore extends AbstractSMSSendingRecordStore {
    @Autowired
    private YoggConfig config;

    public void insertSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        if (smsSendingRecords.length != 0) {
            Path filePath = getFilePath(smsSendingRecords[0].getTaskId());
            Path dir = filePath.getParent();
            try {
                if (dir != null && Files.notExists(dir)) {
                    Files.createDirectory(dir);
                }
            } catch (IOException e) {
                throw new YoggException("Failed to create record file %s", filePath.getFileName());
            }
            Stream.of(smsSendingRecords).forEach(w -> {
                try {
                    Files.write(filePath, (JsonUtils.bean2Json(w) + StringUtils.LF).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new YoggException(e);
                }
            });
        }
    }

    @Override
    public void replaceSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        insertSMSSendingRecord(smsSendingRecords);
    }

    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses) {
        int limit = Integer.MAX_VALUE;
        int offset = 0;
        if (pagination != null) {
            limit = pagination.getLimit();
            offset = pagination.getOffset();
        }
        try {
            Path recordFilePath = getFilePath(taskId);
            if (Files.exists(recordFilePath)) {
                return Files.lines(recordFilePath).map(r -> JsonUtils.json2Bean(r, SMSSendingRecord.class)).filter(r -> filterStatus(r, statuses) && (website == null || r.getWebsite().contains(website))).limit(limit + offset).skip(offset).collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            throw new YoggException(e);
        }
    }

    @Override
    public List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses) {
        int limit = Integer.MAX_VALUE;
        int offset = 0;
        if (pagination != null) {
            limit = pagination.getOffset();
            offset = pagination.getLimit();
        }
        try {
            Path recordFilePath = getFilePath(taskId);
            if (Files.exists(recordFilePath)) {
                return Files.lines(recordFilePath).map(r -> JsonUtils.json2Bean(r, SMSSendingRecord.class)).filter(r -> filterStatus(r, statuses)).map(r -> r.getWebsite()).limit(limit + offset).skip(offset).collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            throw new YoggException(e);
        }
    }

    @Override
    public int countSMSSendingRecord(String taskId, String website, RecordStatus... statuses) {
        try {
            Path recordFilePath = getFilePath(taskId);
            if (Files.exists(recordFilePath)) {
                Long count = Files.lines(recordFilePath).map(l -> JsonUtils.json2Bean(l, SMSSendingRecord.class)).filter(r -> filterStatus(r, statuses)).filter(r -> r.getWebsite().contains(website)).count();
                return count.intValue();
            }
            return 0;
        } catch (Exception e) {
            throw new YoggException(e);
        }
    }

    @Override
    public Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean ifSMSSendingRecordExist(String taskId) {
        try {
            Path recordFilePath = getFilePath(taskId);
            if (Files.notExists(recordFilePath)) {
                return false;
            }
            return Files.lines(recordFilePath).findAny().isPresent();
        } catch (IOException e) {
            throw new YoggException(e);
        }

    }

    @Override
    public void clearSMSSendingRecord(String taskId) {
        try {
            Path recordFilePath = getFilePath(taskId);
            if (Files.exists(recordFilePath)) {
                //TODO may not be possible to remove a file when it is open and in use by this Java virtual machine or other programs
                Files.delete(recordFilePath);
            }
        } catch (IOException e) {
            throw new YoggException(e);
        }
    }

    @Override
    protected DataSourceType getType() {
        return DataSourceType.FILE;
    }

    private boolean filterStatus(SMSSendingRecord record, RecordStatus... statuses) {
        return (statuses.length == 0 || Arrays.asList(statuses).contains(record.getStatus()));
    }

    private Path getFilePath(String taskId) {
        String recordsFileNameFormat = config.getRecordsFileNameFormat();
        return Paths.get(String.format(recordsFileNameFormat, taskId));
    }
}
