package com.joybean.yogg.website.dao;

import com.joybean.yogg.datasource.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author jobean
 */
public abstract class AbstractWebsiteStore implements WebsiteStore {
    @Autowired
    private WebsiteStoreFactory websiteStoreFactory;

    @PostConstruct
    protected void register() {
        DataSourceType type = getType();
        websiteStoreFactory.register(type, this);
    }

    protected abstract DataSourceType getType();
}
