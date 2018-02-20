package com.joybean.yogg.report.record.dao;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.EnumValue;
import com.joybean.yogg.support.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author joybean
 */
@Repository
public class SMSSendingRecordStoreImpl implements SMSSendingRecordStore {

    private final SqlSession sqlSession;

    public SMSSendingRecordStoreImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }


    @Override
    public void insertSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        sqlSession.insert("insertSMSSendingRecord", smsSendingRecords);
    }

    @Override
    public void replaceSMSSendingRecord(SMSSendingRecord... smsSendingRecords) {
        sqlSession.insert("replaceSMSSendingRecord", smsSendingRecords);
    }


    @Override
    public List<SMSSendingRecord> fetchSMSSendingRecord(String taskId, String website, Pagination pagination, RecordStatus... statuses) {
        return sqlSession.selectList("querySMSSendingRecord",
                of("taskId", taskId, "website", website == null ? StringUtils.EMPTY : website, "pagination", pagination == null ? new Pagination(Integer.MAX_VALUE) : pagination, "statuses", statuses));
    }

    @Override
    public List<String> fetchRecordWebsite(String taskId, Pagination pagination, RecordStatus... statuses) {
        return sqlSession.selectList("queryRecordWebsite",
                of("taskId", taskId, "pagination", pagination == null ? new Pagination(Integer.MAX_VALUE) : pagination, "statuses", statuses));
    }

    @Override
    public int countSMSSendingRecord(String taskId, String website, RecordStatus... statuses) {
        return sqlSession.selectOne("countSMSSendingRecord", of("taskId", taskId, "website", website == null ? StringUtils.EMPTY : website, "statuses", statuses));
    }

    @Override
    public Map<RecordStatus, Long> countSMSSendingRecordByStatus(String taskId) {
        List<Map<String, ?>> list = sqlSession.selectList("countSMSSendingRecordByStatus", of("taskId", taskId));
        return list.stream().collect(
                Collectors.toMap(m -> EnumValue.convert((Integer) m.get("status"), RecordStatus.class), m -> (Long) m.get("counter")));
    }

    @Override
    public boolean ifSMSSendingRecordExist(String taskId) {
        return sqlSession.selectOne("ifSMSSendingRecordExist", of("taskId", taskId)).equals(1);
    }

    @Override
    public void clearSMSSendingRecord(String taskId) {
        sqlSession.update("deleteSMSSendingRecord", of("taskId", taskId));
    }
}
