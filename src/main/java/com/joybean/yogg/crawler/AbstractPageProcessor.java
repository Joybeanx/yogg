package com.joybean.yogg.crawler;

import com.joybean.yogg.config.Proxy;
import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.datasource.CrawlerDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.PostConstruct;


public abstract class AbstractPageProcessor implements PageProcessor {
    private final static String USER_AGENT = "User-Agent";
    private final static String USER_AGENT_VALUES = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36";
    private YoggConfig config;
    private CrawlerDataSource crawlerDataSource;
    private Site site;

    @PostConstruct
    private void initSite() {
        site = Site
                .me()
                .addHeader(USER_AGENT, USER_AGENT_VALUES)
                .setRetryTimes(crawlerDataSource.getRetryTimes()).setCycleRetryTimes(crawlerDataSource.getCycleRetryTimes()).setRetrySleepTime(crawlerDataSource.getRetrySleepTime())
                .setSleepTime(crawlerDataSource.getSleepTime()).setTimeOut(crawlerDataSource.getTimeout());

        setProxy();
    }

    public void setProxy() {
        Proxy proxy = config.getProxy();
        if (proxy != null && !Proxy.Type.DIRECT.equals(proxy.getType())) {
            String proxyHost = proxy.getHost();
            if (StringUtils.isNotBlank(proxyHost)) {
                site.setHttpProxy(
                        new HttpHost(proxyHost,
                                Integer.parseInt(proxy.getPort())));
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    public void setConfig(YoggConfig config) {
        this.config = config;
    }

    @Autowired
    public void setCrawlerDataSource(CrawlerDataSource crawlerDataSource) {
        this.crawlerDataSource = crawlerDataSource;
    }
}