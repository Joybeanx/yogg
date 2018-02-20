package com.joybean.yogg;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.simulator.HtmlUnitSMSSendingSimulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author joybean
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimulatorTests {
    @Autowired
    @Qualifier("htmlUnitSMSSendingSimulator")
    private HtmlUnitSMSSendingSimulator simulator;
    @Autowired
    private YoggConfig config;

    @Test
    public void testSendByWebsiteWithoutCaptcha() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "001farm.com");
        assertEquals(RecordStatus.SUCCESS, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteWithSimpleCaptcha() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "yiche.com");
        assertEquals(RecordStatus.SUCCESS, smsSendingRecord.getStatus());
    }


    @Test
    public void testSendByWebsitePopupCaptcha() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "021jmc.com");
        assertEquals(RecordStatus.SUCCESS, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteNotAccessible() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "gavinnie.cn");
        assertEquals(RecordStatus.HOME_PAGE_NOT_LOADED, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteWithoutTargetPage() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "baidu.com");
        assertEquals(RecordStatus.REDIRECT_LINK_NOT_FOUND, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteWithoutPhoneInput() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "0-tech.com");
        assertEquals(RecordStatus.PHONE_NUMBER_INPUT_NOT_FOUND, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteWithoutSendButton() throws Exception {
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "bmai.com");
        assertEquals(RecordStatus.PHONE_NUMBER_INPUT_NOT_FOUND, smsSendingRecord.getStatus());
    }

    @Test
    public void testSendByWebsiteTimeout() throws Exception {
        config.setTimeout(10000);
        SMSSendingRecord smsSendingRecord = simulator.trySend(randomPhoneNumbers(1), "6769.com");
        assertEquals(RecordStatus.TIME_OUT, smsSendingRecord.getStatus());
    }

    public List<String> randomPhoneNumbers(int size) {
        List<String> pns = new ArrayList<>();
        for (int n = 0; n < size; n++) {
            Random random = new Random();
            StringBuilder pn = new StringBuilder("156");
            for (int i = 0; i < 8; i++) {
                pn.append(random.nextInt(10));
            }
            pns.add(pn.toString());
        }
        return pns;
    }
}
