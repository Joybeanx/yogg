package com.joybean.yogg.report.record.dao;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.Pagination;
import com.sun.prism.impl.Disposer;

import java.util.List;
import java.util.Map;

/**
 * @author jobean
 */
public interface SMSSendingRecordStore {

    void insertSMSSendingRecord(SMSSendingRecord... SMSSendingRecords);

    void replaceSMSSendingRecord(SMSSendingRecord... SMSSendingRecords);

    List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses);

    List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses);

    int countSMSSendingRecord(String taskId, String name, RecordStatus... statuses);

    Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId);

    boolean ifSMSSendingRecordExist(String taskId);

    void clearSMSSendingRecord(String taskId);
}
