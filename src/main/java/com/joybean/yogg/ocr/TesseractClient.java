package com.joybean.yogg.ocr;


import com.joybean.yogg.support.YoggException;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.tesseract.PSM_SINGLE_LINE;

/**
 * A captcha solver implementation of <a href="https://github.com/tesseract-ocr/tesseract">Tesseract</a>
 * @author joybean
 */
@Service
public class TesseractClient implements CaptchaSolver {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(TesseractClient.class);
    private static final tesseract.StringGenericVector pars = new tesseract.StringGenericVector();
    private static final tesseract.StringGenericVector parsValues = new tesseract.StringGenericVector();

    static {
        pars.addPut(new tesseract.STRING("load_system_dawg"));
        pars.addPut(new tesseract.STRING("load_freq_dawg"));
        pars.addPut(new tesseract.STRING("load_punc_dawg"));
        pars.addPut(new tesseract.STRING("load_number_dawg"));
        pars.addPut(new tesseract.STRING("load_unambig_dawg"));
        pars.addPut(new tesseract.STRING("load_bigram_dawg"));
        pars.addPut(new tesseract.STRING("load_fixed_length_dawgs"));

        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
        parsValues.addPut(new tesseract.STRING("0"));
    }

    public String solve(File captcha) {
        Assert.notNull(captcha, "Captcha must not be null");
        String captchaPath;
        try {
            captchaPath = captcha.getCanonicalPath();
        } catch (IOException e) {
            LOGGER.error("Failed to solve {}", captcha, e);
            throw new YoggException(e);
        }
        return solve(captchaPath);
    }

    public String solve(String captchaPath) {
        Assert.notNull(captchaPath, "Captcha path must not be null");
        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        api.SetPageSegMode(PSM_SINGLE_LINE);
        if (api.Init(null, "eng",
                0, (ByteBuffer) null, 0, pars, parsValues, false) != 0) {
            throw new YoggException("Could not initialize ocr");
        }
        api.SetVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNPQRSTUVWXYabcdefghijklmnpqrstuvwxy");
        lept.PIX image = pixRead(captchaPath);
        api.SetImage(image);
        // Get OCR result
        BytePointer outText = api.GetUTF8Text();
        if (outText == null) {
            LOGGER.warn("Unexpected tesseract solve result:null");
            return StringUtils.EMPTY;
        }
        String result = outText.getString();
        // Destroy used object and release memory
        outText.deallocate();
        pixDestroy(image);
        return result.trim();
    }
}
