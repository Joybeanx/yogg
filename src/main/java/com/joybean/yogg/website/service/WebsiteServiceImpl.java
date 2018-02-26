/**
 *
 */
package com.joybean.yogg.website.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.website.Website;
import com.joybean.yogg.website.dao.WebsiteStore;
import com.joybean.yogg.website.dao.WebsiteStoreFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author joybean
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(WebsiteServiceImpl.class);
    @Autowired
    private WebsiteStoreFactory websiteStoreFactory;
    @Autowired
    private YoggConfig config;


    @Override
    public void putWebsite(List<String> urls) {
        Assert.notNull(urls, "Urls must not be null");
        List<Website> websites = urls.stream().map(d -> new Website(d)).collect(Collectors.toList());
        getWebsiteStore().putWebsite(websites);
    }


    @Override
    public List<Website> fetchWebsite(Pagination pagination) {
        try {
            return getWebsiteStore().fetchWebsite(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch website urls", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<String> fetchWebsiteUrls(Pagination pagination) {
        try {
            return getWebsiteStore().fetchWebsiteUrl(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch website", e);
            throw new YoggException(e);
        }
    }

    @Override
    public void insertKeyWebsite(Website... websites) {
        insertKeyWebsite(Arrays.asList(websites));
    }

    @Override
    public void insertKeyWebsite(List<Website> websites) {
        Assert.notNull(websites, "Key websites must not be null");
        try {
            if (CollectionUtils.isEmpty(websites)) {
                return;
            }
            getWebsiteStore().insertKeyWebsite(websites);
        } catch (Exception e) {
            LOGGER.error("Failed to insert key websites", e);
            throw new YoggException(e);
        }
    }

    @Override
    public void insertKeyWebsite(String... urls) {
        List<Website> websites = Stream.of(urls).map(Website::new).collect(Collectors.toList());
        insertKeyWebsite(websites);
    }

    @Override
    public void insertKeyWebsite_(List<String> urls) {
        Assert.notNull(urls, "Urls must not be null");
        List<Website> websites = urls.stream().map(d -> new Website(d)).collect(Collectors.toList());
        insertKeyWebsite(websites);
    }

    @Override
    public void replaceKeyWebsite(Website... websites) {
        replaceKeyWebsite(Arrays.asList(websites));
    }

    @Override
    public void replaceKeyWebsite(List<Website> websites) {
        Assert.notNull(websites, "Key websites must not be null");
        try {
            if (CollectionUtils.isEmpty(websites)) {
                return;
            }
            getWebsiteStore().replaceKeyWebsite(websites);
        } catch (Exception e) {
            LOGGER.error("Failed to replace key websites", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<Website> fetchKeyWebsite(Pagination pagination) {
        try {
            return getWebsiteStore().fetchKeyWebsite(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch key website", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<String> fetchKeyWebsiteUrls(Pagination pagination) {
        try {
            return getWebsiteStore().fetchKeyWebsiteUrl(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch key website urls", e);
            throw new YoggException(e);
        }
    }

    public long countKeyWebsite() {
        try {
            return getWebsiteStore().countKeyWebsite();
        } catch (Exception e) {
            LOGGER.error("Failed to count key website.", e);
            throw new YoggException(e);
        }
    }

    public void shutdown() {
        getWebsiteStore().shutdown();
    }

    private WebsiteStore getWebsiteStore() {
        WebsiteStore websiteStore = websiteStoreFactory.getInstance(config.getDataSource().getDataSourceType());
        if (websiteStore == null) {
            websiteStore = websiteStoreFactory.getInstance(DataSourceType.DATABASE);
        }
        return websiteStore;
    }

}
