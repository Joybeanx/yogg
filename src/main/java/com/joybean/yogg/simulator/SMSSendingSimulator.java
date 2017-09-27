package com.joybean.yogg.simulator;

import com.joybean.yogg.report.record.SMSSendingRecord;

import java.util.List;

/**
 * Simulator that simulates user operations of sending SMS by website SMS gateway.
 *
 * @author jobean
 */
public interface SMSSendingSimulator {
    /**
     * Try to send SMS to a specified phone numbers by a url.
     *
     * @param targetPhoneNumbers target phone numbers which we try to send SMS to
     * @param url                url of a website by which we can send SMS
     * @param taskId             task for witch current simulator is working
     * @return record of this sending
     */
    SMSSendingRecord trySend(List<String> targetPhoneNumbers, String url, String taskId);

    /**
     * Try to send SMS to a specified phone numbers by a url.
     *
     * @param targetPhoneNumbers target phone numbers which we try to send SMS to
     * @param url                url of a website by which we can send SMS
     * @return record of this sending
     */
    SMSSendingRecord trySend(List<String> targetPhoneNumbers, String url);
}
