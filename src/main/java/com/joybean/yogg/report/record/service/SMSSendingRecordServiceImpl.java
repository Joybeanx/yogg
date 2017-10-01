package com.joybean.yogg.report.record.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.record.dao.SMSSendingRecordStore;
import com.joybean.yogg.report.record.dao.SMSSendingRecordStoreFactory;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jobean
 */
@Service
public class SMSSendingRecordServiceImpl implements SMSSendingRecordService {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(SMSSendingRecordService.class);
    @Autowired
    private YoggConfig config;
    @Autowired
    private SMSSendingRecordStoreFactory smsSendingRecordStoreFactory;

    @Override
    public void insertSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        try {
            getRecordStore().insertSMSSendingRecord(smsSendingRecords);
        } catch (Exception e) {
            LOGGER.error("Failed to insert record", e);
            throw new YoggException(e);
        }
    }

    @Override
    public void replaceSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        try {
            getRecordStore().replaceSMSSendingRecord(smsSendingRecords);
        } catch (Exception e) {
            LOGGER.error("Failed to replace record", e);
            throw new YoggException(e);
        }
    }

    @Override
    public void replaceSMSSendingRecord(List<SMSSendingRecord> smsSendingRecords) {
        if (!CollectionUtils.isEmpty(smsSendingRecords)) {
            replaceSMSSendingRecord(smsSendingRecords.toArray(new SMSSendingRecord[smsSendingRecords.size()]));
        }
    }

    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        RecordStatus[] recordStatuses = statuses;
        if (statuses.length == 1 && statuses[0] == null) {
            recordStatuses = new RecordStatus[0];
        }
        try {
            return getRecordStore().fetchSMSSendingRecord(taskId, website, pagination, recordStatuses);
        } catch (Exception e) {
            LOGGER.error("Failed to query record of task[{}] by website[{}],{} and {}",
                    taskId, website, Arrays.asList(recordStatuses), pagination, e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, Pagination pagination, RecordStatus... statuses) {
        return fetchSMSSendingRecord(taskId, null, pagination, statuses);
    }

    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, RecordStatus... statuses) {
        return fetchSMSSendingRecord(taskId, null, null, statuses);
    }

    @Override
    public List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            return getRecordStore().fetchRecordWebsite(taskId, pagination, statuses);
        } catch (Exception e) {
            LOGGER.error("Failed to query record website of task[{}] by {} and {}", taskId, pagination,
                    Arrays.asList(statuses), e);
            throw new YoggException(e);
        }
    }


    @Override
    public int countSMSSendingRecord(String taskId, String name, RecordStatus... statuses) {
        Assert.notNull(taskId, "Task id must not be null");
        RecordStatus[] recordStatuses = statuses;
        if (statuses.length == 1 && statuses[0] == null) {
            recordStatuses = new RecordStatus[0];
        }
        try {
            return getRecordStore().countSMSSendingRecord(taskId, name, recordStatuses);
        } catch (Exception e) {
            LOGGER.error("Failed to count record of task[{}] by {}", taskId,
                    Arrays.asList(recordStatuses), e);
            throw new YoggException(e);
        }
    }

    @Override
    public Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            return getRecordStore().countSMSSendingRecordByStatus(taskId);
        } catch (Exception e) {
            LOGGER.error("Failed to count record of task[{}]", taskId, e);
            throw new YoggException(e);
        }
    }

    @Override
    public boolean ifSMSSendingRecordExist(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            return getRecordStore().ifSMSSendingRecordExist(taskId);
        } catch (Exception e) {
            LOGGER.error("Failed to decide if record of task[{}] exist", taskId, e);
            throw new YoggException(e);
        }
    }

    @Override
    public void clearSMSSendingRecord(String taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        try {
            getRecordStore().clearSMSSendingRecord(taskId);
        } catch (Exception e) {
            LOGGER.error("Failed to clear record of task[{}]", taskId, e);
            throw new YoggException(e);
        }
    }

    private SMSSendingRecordStore getRecordStore() {
        SMSSendingRecordStore recordStore = smsSendingRecordStoreFactory.getInstance(config.getDataSource().getDataSourceType());
        if (recordStore == null) {
            recordStore = smsSendingRecordStoreFactory.getInstance(DataSourceType.FILE);
        }
        return recordStore;
    }
}
