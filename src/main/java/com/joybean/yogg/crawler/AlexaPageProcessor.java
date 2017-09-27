package com.joybean.yogg.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Arrays;
import java.util.List;

/**
 * Crawler page processor for collecting domains from <a href="http://alexa.cn">alexa.cn</a>
 *
 * @author jobean
 */
@Component
public class AlexaPageProcessor extends AbstractPageProcessor {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(AlexaPageProcessor.class);

    @Override
    public void process(Page page) {
        Selectable nextPageLink = page.getHtml().regex("<a href=\"([^<>]*?)\">下一页", 1);
        if (nextPageLink != null) {
            page.addTargetRequests(Arrays.asList(nextPageLink.get()));
        }
        String currentUrl = page.getRequest().getUrl();
        List<String> domains = page.getHtml().xpath("//div[@class='domain']/a[1]/text()").all();
        if (CollectionUtils.isEmpty(domains)) {
            return;
        }
        LOGGER.info("Fetched domains {} from {}", domains, currentUrl);
        page.putField(WebsiteCrawler.CRAWLER_FIELD_NAME_URLS, domains);
    }
}
