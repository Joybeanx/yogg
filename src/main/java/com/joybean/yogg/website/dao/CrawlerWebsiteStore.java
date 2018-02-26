package com.joybean.yogg.website.dao;

import com.joybean.yogg.crawler.WebsiteCrawler;
import com.joybean.yogg.datasource.CrawlerDataSource;
import com.joybean.yogg.datasource.DataSourceType;
import com.joybean.yogg.support.Pagination;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.website.Website;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author jobean
 */
@Component
public class CrawlerWebsiteStore extends AbstractWebsiteStore {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(CrawlerWebsiteStore.class);
    private final static int DEFAULT_QUEUE_SIZE = 2000;
    private ArrayBlockingQueue<Website> queue = new ArrayBlockingQueue(DEFAULT_QUEUE_SIZE);
    private Spider spider;
    @Autowired
    private CrawlerDataSource crawlerDataSource;
    @Autowired
    private WebsiteCrawler crawler;

    public CrawlerWebsiteStore(SqlSession sqlSession) {
        super(sqlSession);
    }

    public void putWebsite(List<Website> websites) {
        websites.stream().forEach(w -> {
            try {
                queue.put(w);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to insert {}", w, e);
                throw new YoggException(e);
            }
        });
    }

    @Override
    public synchronized List<Website> fetchWebsite(Pagination pagination) {
        if (spider == null) {
            List<String> startUrls = crawlerDataSource.getStartUrls();
            spider = crawler.start(startUrls.toArray(new String[startUrls.size()]));
        }
        int maxNum = pagination.getLimit();
        List<Website> websites = fetchAvailableWebsite(maxNum);
        if (Spider.Status.Stopped == spider.getStatus()) {
            spider = null;
        }
        return websites;
    }

    @Override
    public List<String> fetchWebsiteUrl(Pagination pagination) {
        List<Website> websites = fetchWebsite(pagination);
        return websites.stream().map(w -> w.getUrl()).collect(Collectors.toList());
    }


    @Override
    public synchronized void shutdown() {
        if (spider != null) {
            if (Spider.Status.Stopped != spider.getStatus()) {
                spider.stop();
                LOGGER.info("Crawler has been shutdown");
            }
        }
    }

    /**
     * Wait until any website is available or current spider is stopped
     *
     * @param maxNum
     */
    private List<Website> fetchAvailableWebsite(int maxNum) {
        List<Website> websites = new ArrayList<>();
        if (Spider.Status.Stopped == spider.getStatus()) {
            queue.drainTo(websites, maxNum);
            return websites;
        }
        while (websites.size() < maxNum && Spider.Status.Stopped != spider.getStatus()) {
            try {
                websites.add(queue.take());
                queue.drainTo(websites, maxNum - websites.size());
            } catch (InterruptedException e) {
                LOGGER.error("Failed to fetch website", e);
                throw new YoggException(e);
            }
        }
        return websites;
    }

    @Override
    protected DataSourceType getType() {
        return DataSourceType.CRAWLER;
    }
}
