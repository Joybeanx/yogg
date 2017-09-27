package com.joybean.yogg;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.service.ReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author jobean
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReportServiceTests {
    @Autowired
    private ReportService reportService;
    private final static String TEST_URL = "http://test_url.com";
    private final static String TEST_TASK_ID = "testTask";

    @Test
    public void testReplaceFailedRecord() throws Exception {
        SMSSendingRecord record = new SMSSendingRecord();
        record.setWebsite(TEST_URL);
        record.setStatus(RecordStatus.REDIRECT_LINK_NOT_FOUND);
        record.setTaskId(TEST_TASK_ID);
        reportService.replaceSMSSendingRecord(Arrays.asList(record));
        List<SMSSendingRecord> records = reportService.fetchSMSSendingRecord(TEST_TASK_ID, TEST_URL, null);
        Assert.isTrue(!CollectionUtils.isEmpty(records) && records.get(0).equals(record), "Unexpected fetched record");
    }
}
