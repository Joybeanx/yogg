package com.joybean.yogg.crawler;

import com.joybean.yogg.config.Proxy;
import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.CrawlerDataSource;
import com.joybean.yogg.support.YoggException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;


public abstract class AbstractPageProcessor implements PageProcessor {
    private final static String USER_AGENT = "User-Agent";
    private final static String USER_AGENT_VALUES = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36";
    @Autowired
    private CrawlerDataSource crawlerDataSource;
    @Autowired
    private YoggConfig config;
    private Site site;

    @PostConstruct
    private void initSite() {
        site = Site
                .me()
                .addHeader(USER_AGENT, USER_AGENT_VALUES)
                .setRetryTimes(crawlerDataSource.getRetryTimes()).setCycleRetryTimes(crawlerDataSource.getCycleRetryTimes()).setRetrySleepTime(crawlerDataSource.getRetrySleepTime())
                .setSleepTime(crawlerDataSource.getSleepTime()).setTimeOut(crawlerDataSource.getTimeout());

        Proxy proxy = config.getProxy();
        if (proxy != null && !Proxy.Type.DIRECT.equals(proxy.getType())) {
            String proxyHost = proxy.getHost();
            if (StringUtils.isNotBlank(proxyHost)) {
                site.setHttpProxy(
                        new HttpHost(proxyHost,
                                Integer.valueOf(proxy.getPort())));
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }


}