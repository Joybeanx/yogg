package com.joybean.yogg.report.record.dao;

import com.joybean.yogg.datasource.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author jobean
 */
public abstract class AbstractSMSSendingRecordStore implements SMSSendingRecordStore {
    @Autowired
    private SMSSendingRecordStoreFactory smsSendingRecordStoreFactory;

    @PostConstruct
    protected void register() {
        DataSourceType type = getType();
        smsSendingRecordStoreFactory.register(type, this);
    }

    protected abstract DataSourceType getType();
}
