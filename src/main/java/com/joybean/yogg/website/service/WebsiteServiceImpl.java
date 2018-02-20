/**
 *
 */
package com.joybean.yogg.website.service;

import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.website.Website;
import com.joybean.yogg.website.dao.WebsiteStore;
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
    private WebsiteStore websiteStore;

    @Override
    public void insertWebsite(Website... websites) {
        insertWebsite(Arrays.asList(websites));
    }

    @Override
    public void insertWebsite(List<Website> websites) {
        Assert.notNull(websites, "Websites must not be null");
        try {
            if (CollectionUtils.isEmpty(websites)) {
                return;
            }
            websiteStore.insertWebsite(websites);
        } catch (Exception e) {
            LOGGER.error("Failed to insert websites", e);
            throw new YoggException(e);
        }
    }

    @Override
    public void insertWebsite(String... urls) {
        List<Website> websites = Stream.of(urls).map(Website::new).collect(Collectors.toList());
        insertWebsite(websites);
    }

    @Override
    public void insertWebsite_(List<String> urls) {
        Assert.notNull(urls, "Urls must not be null");
        List<Website> websites = urls.stream().map(d -> new Website(d)).collect(Collectors.toList());
        insertWebsite(websites);
    }

    @Override
    public void replaceWebsite(Website... websites) {
        replaceWebsite(Arrays.asList(websites));
    }

    @Override
    public void replaceWebsite(List<Website> websites) {
        Assert.notNull(websites, "Websites must not be null");
        try {
            websiteStore.replaceWebsite(websites);
        } catch (Exception e) {
            LOGGER.error("Failed to replace websites", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<Website> fetchWebsite(Pagination pagination) {
        try {
            return websiteStore.fetchWebsite(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch website urls", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<String> fetchWebsiteUrls(Pagination pagination) {
        try {
            return websiteStore.fetchWebsiteUrl(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch website", e);
            throw new YoggException(e);
        }
    }

    public long countWebsite() {
        try {
            return websiteStore.countWebsite();
        } catch (Exception e) {
            LOGGER.error("Failed to count website", e);
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
            websiteStore.insertKeyWebsite(websites);
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
            websiteStore.replaceKeyWebsite(websites);
        } catch (Exception e) {
            LOGGER.error("Failed to replace key websites", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<Website> fetchKeyWebsite(Pagination pagination) {
        try {
            return websiteStore.fetchKeyWebsite(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch key website", e);
            throw new YoggException(e);
        }
    }

    @Override
    public List<String> fetchKeyWebsiteUrls(Pagination pagination) {
        try {
            return websiteStore.fetchKeyWebsiteUrl(pagination);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch key website urls", e);
            throw new YoggException(e);
        }
    }

    public long countKeyWebsite() {
        try {
            return websiteStore.countKeyWebsite();
        } catch (Exception e) {
            LOGGER.error("Failed to count key website.", e);
            throw new YoggException(e);
        }
    }

    public void shutdown() {
        websiteStore.shutdown();
    }



}
