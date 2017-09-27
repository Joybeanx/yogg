package com.joybean.yogg;

import com.joybean.yogg.datasource.CrawlerDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.net.URL;
import java.util.List;

/**
 * @author jobean
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CrawlerTests {
    @Autowired
    @Qualifier("defaultPageProcessor")
    private PageProcessor pageProcessor;

    @Test
    public void testCrawler() throws Exception {
        String startUlr = "https://www.hao123.com/";
        Spider spider = Spider.create(pageProcessor).addPipeline(new ConsolePipeline()).addUrl(startUlr).thread(3);
        SpiderMonitor.instance().register(spider);
        spider.run();
    }

}
