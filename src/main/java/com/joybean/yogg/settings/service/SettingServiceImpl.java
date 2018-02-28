package com.joybean.yogg.settings.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.crawler.AbstractPageProcessor;
import com.joybean.yogg.support.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author joybean
 */
@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    private YoggConfig config;

    private Collection<AbstractPageProcessor> crawlerPageProcessors;

    @Override
    public void saveSettings(YoggConfig config) {
        crawlerPageProcessors.forEach(AbstractPageProcessor::setProxy);
        JsonUtils.write(config, getFileName());
    }

    private String getFileName() {
        return config.getSettingsFileName();
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.crawlerPageProcessors = context.getBeansOfType(AbstractPageProcessor.class).values();
    }
}
