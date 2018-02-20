package com.joybean.yogg.report.record;

import javafx.scene.control.Hyperlink;

import java.util.Date;

/**
 * A proxy for displaying url link pattern in sending record table
 * @author joybean
 */
public class SMSSendingRecordProxy {
    private SMSSendingRecord smsSendingRecord;

    public SMSSendingRecordProxy(SMSSendingRecord smsSendingRecord) {
        this.smsSendingRecord = smsSendingRecord;
    }

    public Hyperlink getWebsite() {
        return new Hyperlink(smsSendingRecord.getWebsite());
    }

    public Hyperlink getTargetPageUrl() {
        return new Hyperlink(smsSendingRecord.getTargetPageUrl());
    }

    public Hyperlink getSmsRequestUrl() {
        return new Hyperlink(smsSendingRecord.getSmsRequestUrl());
    }

    public boolean isHasCaptcha() {
        return smsSendingRecord.isHasCaptcha();
    }

    public String getException() {
        return smsSendingRecord.getException();
    }

    public RecordStatus getStatus() {
        return smsSendingRecord.getStatus();
    }


    public String getTaskId() {
        return smsSendingRecord.getTaskId();
    }

    public Date getCreateTime() {
        return smsSendingRecord.getCreateTime();
    }
}
