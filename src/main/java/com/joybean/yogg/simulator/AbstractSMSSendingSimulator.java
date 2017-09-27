package com.joybean.yogg.simulator;

import com.joybean.yogg.report.record.SMSSendingRecord;

import java.util.List;

/**
 * @author jobean
 */
public abstract class AbstractSMSSendingSimulator implements SMSSendingSimulator {


    public SMSSendingRecord trySend(List<String> targetPhoneNumbers, String url) {
        return trySend(targetPhoneNumbers, url, null);
    }
}
