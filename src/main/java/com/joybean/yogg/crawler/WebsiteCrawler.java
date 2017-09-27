package com.joybean.yogg.crawler;

import us.codecraft.webmagic.Spider;

/**
 * @author jobean
 */
public interface WebsiteCrawler {
    String CRAWLER_FIELD_NAME_URLS = "urls";
    Spider start(String... startUrls);
}
