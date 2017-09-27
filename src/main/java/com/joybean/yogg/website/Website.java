/**
 *
 */
package com.joybean.yogg.website;

/**
 * @author joybean
 */
public class Website {
    /**
     * Url of the website
     */
    private String url;

    public Website(String url) {
        this.url = url;
    }

    public Website() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
