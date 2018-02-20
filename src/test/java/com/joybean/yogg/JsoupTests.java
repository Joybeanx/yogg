package com.joybean.yogg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author joybean
 */
public class JsoupTests {
    @Test
    public void testConnection() throws Exception {
        Document doc = Jsoup.connect("http://sxxfy.com").get();
        Assert.assertEquals(doc.title(), "山西鑫富源人力资源派遣有限公司");
    }
}
