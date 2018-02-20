package com.joybean.yogg.crawler;

import us.codecraft.webmagic.Spider;

/**
 * @author joybean
 */
public interface WebsiteCrawler {
    String CRAWLER_FIELD_NAME_URLS = "urls";
    Spider start(String... startUrls);
}
