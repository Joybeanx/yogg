package com.joybean.yogg.crawler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.joybean.yogg.support.UrlUtils.*;

/**
 * Default crawler page processor which would collect every link the crawler find
 *
 * @author jobean
 */
@Component
public class DefaultPageProcessor extends AbstractPageProcessor {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(DefaultPageProcessor.class);

    @Override
    public void process(Page page) {
        String currentUrl = page.getUrl().get();
        String topLevelDomain = getTopLevelDomain(currentUrl);
        List<String> allLinks = page.getHtml().links().all();
        //Page urls except for url that contains current top level domain
        List<String> targetUrls = allLinks.stream().filter(l -> StringUtils.isNotBlank(l) && !l.contains(topLevelDomain) && isValid(l)).map(l -> getTopLevelDomain(l))
                .filter(l -> StringUtils.isNotEmpty(l)).map(l -> toHttpFormat(l)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(targetUrls)) {
            page.addTargetRequests(targetUrls);
            LOGGER.info("Fetched urls {} from {}", targetUrls, currentUrl);
            page.putField(WebsiteCrawler.CRAWLER_FIELD_NAME_URLS, targetUrls);
        }
        LOGGER.info("No matched urls found on {}", currentUrl);
    }
}
