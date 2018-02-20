package com.joybean.yogg.report.dao;

import com.joybean.yogg.report.TaskReport;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

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
        return sqlSession.selectOne("queryTaskReport", of("taskId", taskId));
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
