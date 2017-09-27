package com.joybean.yogg.datasource;

import com.joybean.yogg.config.IDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Crawler data source that supplies website urls
 * @author jobean
 */
@Component
@ConfigurationProperties(prefix = "crawler-datasource")
public class CrawlerDataSource implements IDataSource {
    private List<String> startUrls;
    /**
     * number of crawler threads
     */
    private int threads;
    /**
     * timeout for crawler downloader
     */
    private int timeout;
    /**
     * retry times when download failed
     */
    private int retryTimes;
    /**
     * retry times of adding back to scheduler and downloading again when download failed
     */
    private int cycleRetryTimes;
    /**
     * interval between the processing of two pages,500 ms by default
     */
    private int sleepTime;
    /**
     * retry sleep time when download failed
     */
    private int retrySleepTime;

    public List<String> getStartUrls() {
        return startUrls;
    }

    public void setStartUrls(List<String> startUrls) {
        this.startUrls = startUrls;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getRetrySleepTime() {
        return retrySleepTime;
    }

    public void setRetrySleepTime(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.CRAWLER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrawlerDataSource that = (CrawlerDataSource) o;

        return getStartUrls().equals(that.getStartUrls());

    }

    @Override
    public int hashCode() {
        return getStartUrls().hashCode();
    }
}
