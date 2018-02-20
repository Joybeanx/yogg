package com.joybean.yogg;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author joybean
 */
public class HtmlUnitTests {

    @Test
    public void testIgnoreFailingStatusCode() throws Exception {
        WebClient webClient = HtmlUnitUtils.buildWebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        Page page = webClient.getPage("http://arebz.com/pagenotexist");
        assertNotNull(page);
    }

    @Test
    public void testOOM() throws Exception {
        WebClient webClient = HtmlUnitUtils.buildWebClient();
        try {
            webClient.getPage("http://sxxfy.com");
        } catch (Throwable e) {
            assertTrue(e instanceof OutOfMemoryError);
        }
    }
}
