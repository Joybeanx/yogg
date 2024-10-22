package com.joybean.yogg.settings.service;

import com.joybean.yogg.config.YoggConfig;

/**
 * @author joybean
 */
public interface SettingService {
    /**
     * Save settings to file
     *
     * @param config configuration of settings
     */
    void saveSettings(YoggConfig config);
}
