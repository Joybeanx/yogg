package com.joybean.yogg.settings.service;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.support.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jobean
 */
@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    private YoggConfig config;

    @Override
    public void saveSettings(YoggConfig config) {
        JsonUtils.write(config, getFileName());
    }
    private String getFileName() {
        return config.getSettingsFileName();
    }
}
