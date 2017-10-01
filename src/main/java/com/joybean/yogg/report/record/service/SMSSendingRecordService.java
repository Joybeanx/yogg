package com.joybean.yogg.report.record.service;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.Pagination;

import java.util.List;
import java.util.Map;

/**
 * @author jobean
 */
public interface SMSSendingRecordService {
    void insertSMSSendingRecord(SMSSendingRecord... smsSendingRecords);

    void replaceSMSSendingRecord(SMSSendingRecord... smsSendingRecords);

    void replaceSMSSendingRecord(List<SMSSendingRecord> smsSendingRecords);

    List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses);

    List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, Pagination pagination, RecordStatus... statuses);

    List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, RecordStatus... statuses);

    List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses);

    int countSMSSendingRecord(String taskId, String website, RecordStatus... statuses);

    Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId);

    boolean ifSMSSendingRecordExist(String taskId);

    void clearSMSSendingRecord(String taskId);
}
