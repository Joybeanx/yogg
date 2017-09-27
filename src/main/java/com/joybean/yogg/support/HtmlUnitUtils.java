package com.joybean.yogg.support;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.joybean.yogg.config.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * @author jobean
 */
public class HtmlUnitUtils {

    public static WebClient buildWebClient(int timeout, Proxy proxy) {
        final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setJavaScriptTimeout(timeout);
        WebClientOptions options = webClient.getOptions();
        if (proxy != null && !Proxy.Type.DIRECT.equals(proxy.getType()) && StringUtils.isNotBlank(proxy.getHost())) {
            ProxyConfig proxyConfig = new ProxyConfig(proxy.getHost(),
                    Integer.valueOf(proxy.getPort()));
            options.setProxyConfig(proxyConfig);
        }
        //Usually we don't need to enable javascript while visiting home page to find a redirect link
        // Doing this also can avoid OOM problem arise from HtmlUnit when loading some pages,see com.joybean.yogg.HtmlUnitTests.testOOM()
        options.setJavaScriptEnabled(false);
        options.setThrowExceptionOnScriptError(false);
        //Ignore load failures of web resources such as javascript,picture,etc,so we can take further steps on current page
        options.setThrowExceptionOnFailingStatusCode(false);
        options.setCssEnabled(false);
        options.setDoNotTrackEnabled(true);
        options.setRedirectEnabled(true);
        options.setTimeout(timeout);
        options.setUseInsecureSSL(true);
        options.setHistoryPageCacheLimit(0);
        return webClient;
    }

    public static WebClient buildWebClient() {
        return buildWebClient(0, null);
    }

    public static Page clickFirstByXPaths(final DomNode parentElement,
                                          List<String> xPaths) throws IOException {
        Assert.notNull(xPaths, "XPaths must not be null");
        return clickFirstByXPaths(parentElement, xPaths.toArray(new String[xPaths.size()]));
    }

    public static Page clickFirstByXPaths(final DomNode parentElement,
                                          String... xPaths) throws IOException {
        Assert.notNull(parentElement, "ParentElement must not be null");
        DomElement element;
        if (null != (element = findFirstByXPaths(parentElement, xPaths))) {
            return element.click();
        }
        return null;
    }


    public static boolean inputFirstByXPaths(final DomNode parentElement, String value,
                                             List<String> xPaths) throws IOException {
        Assert.notNull(xPaths, "XPaths must not be null");
        return inputFirstByXPaths(parentElement, value, xPaths.toArray(new String[xPaths.size()]));
    }

    public static boolean inputFirstByXPaths(DomNode parentElement, String value,
                                             String... xPaths) throws IOException {
        Assert.notNull(parentElement, "ParentElement must not be null");
        Assert.notNull(xPaths, "XPaths must not be null");
        HtmlInput input;
        if (null != (input = findFirstByXPaths(parentElement, xPaths))) {
            input.setValueAttribute(value);
            return true;
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static <T> T findFirstByXPaths(final DomNode parentElement,
                                          String... xPaths) {
        Assert.notNull(parentElement, "ParentElement must not be null");
        Assert.notNull(xPaths, "XPaths must not be null");
        for (String xPath : xPaths) {
            HtmlElement element;
            if (null != (element = parentElement.getFirstByXPath(xPath))) {
                return (T) element;
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T findFirstByXPaths(final DomNode parentElement,
                                          List<String> xPaths) {
        Assert.notNull(xPaths, "XPaths must not be null");
        return findFirstByXPaths(parentElement, xPaths.toArray(new String[xPaths.size()]));
    }
}
