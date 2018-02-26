/**
 *
 */
package com.joybean.yogg.crawler;

import com.joybean.yogg.website.service.WebsiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * Pipeline for processing crawler result
 *
 * @author joybean
 */
@Component
public class WebsitePersistentPipeline implements Pipeline {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(WebsitePersistentPipeline.class);
    @Autowired
    private WebsiteService websiteService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            List<String> extractedUrls = resultItems.get(WebsiteCrawler.CRAWLER_FIELD_NAME_URLS);
            if (extractedUrls == null) {
                return;
            }
            websiteService.putWebsite(extractedUrls);
        } catch (Exception e) {
            LOGGER.error("Failed to persist website.", e);
            //TODO should we throw exception here?
        }
    }
}
