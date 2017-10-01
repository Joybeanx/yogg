/**
 *
 */
package com.joybean.yogg.crawler;


import com.joybean.yogg.datasource.CrawlerDataSource;
import com.joybean.yogg.support.YoggException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.Arrays;

/**
 * Default website crawler
 *
 * @author joybean
 */
@Component
public class DefaultWebsiteCrawler implements WebsiteCrawler {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(DefaultWebsiteCrawler.class);
    private static final int EXPECTED_WEBSITE_NUM = 100000;

    private PageProcessor pageProcessor;
    private WebsitePersistentPipeline persistencePipeline;
    private CrawlerDataSource crawlerDataSource;

    @Override
    public Spider start(String... startUrls) {
        try {
            if (startUrls.length == 0) {
                throw new IllegalArgumentException("Start urls for crawler must not be empty");
            }
            int threads = crawlerDataSource.getThreads();
            Spider spider = Spider.create(pageProcessor).setScheduler(new QueueScheduler()
                    .setDuplicateRemover(new BloomFilterDuplicateRemover(EXPECTED_WEBSITE_NUM))).addPipeline(persistencePipeline).addUrl(startUrls).thread(threads);

            spider.setSpiderListeners(Arrays.asList(new SpiderListener() {
                @Override
                public void onSuccess(Request request) {

                }

                @Override
                public void onError(Request request) {
                    //TODO notify UI
                    LOGGER.error("Crawler execution error while processing {}", request);
                }
            }));
            spider.start();
            return spider;
        } catch (Throwable e) {
            LOGGER.error("Failed to launch alexa crawler", e);
            throw new YoggException(e);
        }
    }

    @Autowired
    @Qualifier("defaultPageProcessor")
    public void setPageProcessor(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    @Autowired
    public void setPersistencePipeline(WebsitePersistentPipeline persistencePipeline) {
        this.persistencePipeline = persistencePipeline;
    }

    @Autowired
    public void setCrawlerDataSource(CrawlerDataSource crawlerDataSource) {
        this.crawlerDataSource = crawlerDataSource;
    }
}
