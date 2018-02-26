package com.joybean.yogg.report.dao;

import com.joybean.yogg.report.TaskReport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author joybean
 */
@Component
public class ReportStoreImpl implements ReportStore {
    private final SqlSession sqlSession;

    public ReportStoreImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public String fetchTaskReport(String taskId) {
        List<String> reports = sqlSession.selectList("queryTaskReport", of("taskId", taskId));
        if (CollectionUtils.isNotEmpty(reports)) {
            return reports.get(0);
        }
        return null;
    }

    @Override
    public void saveTaskReport(TaskReport taskReport) {
        sqlSession.insert("insertTaskReport", taskReport);
    }

    @Override
    public void updateTaskReport(TaskReport taskReport) {
        sqlSession.update("updateTaskReport", taskReport);
    }

    @Override
    public void deleteTaskReport(String taskId) {
        sqlSession.delete("deleteTaskReport", of("taskId", taskId));
    }
}
