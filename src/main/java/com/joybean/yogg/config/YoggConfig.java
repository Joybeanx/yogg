package com.joybean.yogg.config;

import com.joybean.yogg.datasource.DataSourceFactory;
import com.joybean.yogg.support.JsonUtils;
import com.joybean.yogg.support.YoggException;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author joybean
 */
@Component
@ConfigurationProperties
@DependsOn("contextHolder")
public class YoggConfig implements Serializable {
    private static final long serialVersionUID = -2167754318343177731L;
    /**
     * Target mobile phone number which we send SMS to,excluding country calling code
     */
    private LinkedList<String> targets;
    /**
     * Number of threads used for sending SMS
     */
    private int threads;
    /**
     * Timeout of sending SMS
     */
    private int timeout;
    /**
     * Retry attempt times if failed,not effective currently
     */
    private int retryAttempts;
    /**
     * Data source from where to fetch website
     */
    private IDataSource dataSource;
    /**
     * Proxy used for accessing website
     */
    private Proxy proxy = new Proxy();
    /**
     * Necessary page element locators for sending SMS,such as phone number input,sending button,etc
     */
    private PageElementLocators pageElementLocators = new PageElementLocators();

    /**
     * Local file for saving current settings
     */
    private String settingsFileName;

    /**
     * Load custom configuration from file and override corresponding default value.
     */
    @PostConstruct
    public void loadCustomConfig() {
        YoggConfig userConfig = JsonUtils.read(getClass(), getSettingsFileName());
        if (userConfig == null) {
            return;
        }
        IDataSource userConfigDataSource = userConfig.getDataSource();
        if (userConfigDataSource != null) {
            //Ensure current data source in YoggConfig is managed by spring bean factory,and then it will copy properties from user defined data source
            IDataSource dataSource = DataSourceFactory.getDataSource(userConfigDataSource.getDataSourceType());
            setDataSource(dataSource);
        }
        try {
            BeanUtils.copyProperties(userConfig, this, "dataSource");
            if (userConfigDataSource != null) {
                BeanUtils.copyProperties(userConfigDataSource, dataSource, "logWriter", "loginTimeout");
            }
        } catch (Exception e) {
            throw new YoggException(e);
        }
    }

    public static class PageElementLocators implements Serializable {
        private static final long serialVersionUID = -5070295430618253722L;
        /**
         * XPaths for finding a link of target page,such as XPath of register link on home page
         */
        private List<String> redirectLinkXpaths;
        /**
         * XPaths for finding phone number input on target page
         */
        private List<String> phoneNumberInputXpaths;
        /**
         * XPaths for finding captcha image on target page
         */
        private List<String> captchaImageXpaths;
        /**
         * XPaths for finding a captcha input on target page
         */
        private List<String> captchaInputXpaths;
        /**
         * XPaths for finding a sending sms button on target page
         */
        private List<String> sendButtonXpaths;
        /**
         * XPaths for finding a confirm button on target page after clicking sending button
         */
        private List<String> confirmButtonXpaths;

        public List<String> getRedirectLinkXpaths() {
            return redirectLinkXpaths;
        }

        public void setRedirectLinkXpaths(List<String> redirectLinkXpaths) {
            this.redirectLinkXpaths = redirectLinkXpaths;
        }

        public List<String> getPhoneNumberInputXpaths() {
            return phoneNumberInputXpaths;
        }

        public void setPhoneNumberInputXpaths(List<String> phoneNumberInputXpaths) {
            this.phoneNumberInputXpaths = phoneNumberInputXpaths;
        }

        public List<String> getCaptchaImageXpaths() {
            return captchaImageXpaths;
        }

        public void setCaptchaImageXpaths(List<String> captchaImageXpaths) {
            this.captchaImageXpaths = captchaImageXpaths;
        }

        public List<String> getCaptchaInputXpaths() {
            return captchaInputXpaths;
        }

        public void setCaptchaInputXpaths(List<String> captchaInputXpaths) {
            this.captchaInputXpaths = captchaInputXpaths;
        }

        public List<String> getSendButtonXpaths() {
            return sendButtonXpaths;
        }

        public void setSendButtonXpaths(List<String> sendButtonXpaths) {
            this.sendButtonXpaths = sendButtonXpaths;
        }

        public List<String> getConfirmButtonXpaths() {
            return confirmButtonXpaths;
        }

        public void setConfirmButtonXpaths(List<String> confirmButtonXpaths) {
            this.confirmButtonXpaths = confirmButtonXpaths;
        }
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(LinkedList<String> targets) {
        this.targets = targets;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    public void setDataSourceType(String dataSourceType) {
        this.dataSource = DataSourceFactory.getDataSource(dataSourceType);
    }

    public IDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(IDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSettingsFileName() {
        return settingsFileName;
    }

    public void setSettingsFileName(String settingsFileName) {
        this.settingsFileName = settingsFileName;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public PageElementLocators getPageElementLocators() {
        return pageElementLocators;
    }

}
