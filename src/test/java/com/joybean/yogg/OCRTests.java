package com.joybean.yogg;

import com.google.common.net.InternetDomainName;
import com.joybean.yogg.ocr.CaptchaSolver;
import com.joybean.yogg.ocr.TesseractClient;
import com.joybean.yogg.website.Website;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * @author jobean
 */
public class OCRTests {
    private CaptchaSolver captchaSolver = new TesseractClient();

    @Test
    public void testSimpleCaptcha() throws Exception {
        InternetDomainName.from("sAppchizi.com").topPrivateDomain();
        URL resource = OCRTests.class.getResource("/captcha.jpg");
        File file = Paths.get(resource.toURI()).toFile();
        String img = file.getAbsolutePath();
        String result = captchaSolver.solve(img);
        assertEquals(result, "6LbWB");
    }

}
