package com.joybean.yogg.support;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author jobean
 */
public class UrlUtils {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(UrlUtils.class);
    private final static String HTTP_PREFIX = "http";
    private final static InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

    public static String toHttpFormat(String s) {
        Assert.notNull(s, "Input string must not be null");
        String url = s.trim();
        if (!url.startsWith(HTTP_PREFIX)) {
            return HTTP_PREFIX + "://" + url;
        }
        return url;
    }

    public static boolean isValid(String url) {
        try {
            new URL(url);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getHost(String url) {
        try {
            String host = new URL(url).getHost();
            if (StringUtils.isBlank(host)) {
                LOGGER.warn("Got empty host from {}", url);
            }
            return host;
        } catch (MalformedURLException e) {
            throw new YoggException(e, "Invalid url %s", url);
        }
    }

    /**
     * Get top-level domain of the specified url
     * <p><pre class="code">
     * UrlUtils.getTopLevelDomain("http://127.0.0.1") = "127.0.0.1"
     * UrlUtils.getTopLevelDomain("http://www.google.com") = "google.com"
     * UrlUtils.getTopLevelDomain("http://docs.aws.amazon.com") = "amazon.com"
     * UrlUtils.getTopLevelDomain("https://github.com/bitcoin/") = "github.com"
     * </pre>
     *
     * @param url the url
     * @return the top-level host of the url
     */
    public static String getTopLevelDomain(String url) {
        Assert.hasLength(url, "Url must not be empty");
        try {
            String host = getHost(url);
            if (StringUtils.isEmpty(host)) {
                return host;
            }
            //Host of the url is ip
            if (inetAddressValidator.isValid(host)) {
                return host;
            }
            InternetDomainName internetDomainName = InternetDomainName.from(host);
            if (internetDomainName.isTopPrivateDomain() || internetDomainName.isPublicSuffix()) {
                return internetDomainName.toString();
            }
            return internetDomainName.topPrivateDomain().toString();
        } catch (Exception e) {
            throw new YoggException(e, "Could not get top level domain by %s", url);
        }
    }
}
