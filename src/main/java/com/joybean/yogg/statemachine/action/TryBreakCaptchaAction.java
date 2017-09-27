package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.ocr.CaptchaSolver;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;
import static java.util.UUID.randomUUID;

/**
 * @author jobean
 */
@Component
public class TryBreakCaptchaAction extends AbstractAction {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(TryBreakCaptchaAction.class);
    private final static String IMG_FILE_FORMAT = "temp/%s.jpg";
    @Autowired
    private CaptchaSolver captchaSolver;

    @Override
    public void doExecute(StateContext<String, String> context) throws Exception {
        HtmlPage page = getCurrentPage(context);
        String captchaText = resolveCaptcha(page);
        if (captchaText == null) {
            sendEvent("CAPTCHA_NONEXISTENT", context);
        } else {
            updateRecordProperty(RECORD_PROPERTY_HAS_CAPTCHA, true, context);
            if (captchaText.trim().length() == 0) {
                sendFailureEvent(RecordStatus.SOLVE_CAPTCHA_FAILURE, context);
            } else if (!inputCaptchaText(page, captchaText)) {
                sendFailureEvent(RecordStatus.CAPTCHA_INPUT_NOT_FOUND, context);
            } else {
                sendEvent("CAPTCHA_TEXT_INPUT", context);
            }
        }
    }


    public String resolveCaptcha(HtmlPage page) throws IOException {
        HtmlImage captchaImg = HtmlUnitUtils.findFirstByXPaths(page, config.getPageElementLocators().getCaptchaImageXpaths());
        if (captchaImg == null) {
            return null;
        }
        File imgFile = null;
        try {
            imgFile = new File(String.format(IMG_FILE_FORMAT, randomUUID()));
            if (!imgFile.getParentFile().exists()) {
                imgFile.getParentFile().mkdirs();
            }
            captchaImg.saveAs(imgFile);
            String text = captchaSolver.solve(imgFile);
            return text;
        } catch (Throwable e) {
            LOGGER.error("Failed to resolve captcha on %s", page.getUrl(), e);
            throw e;
        } finally {
            if (imgFile != null) {
                imgFile.delete();
            }
        }
    }

    public boolean inputCaptchaText(HtmlPage page, String captchaText) throws IOException {
        return HtmlUnitUtils.inputFirstByXPaths(page, captchaText, config.getPageElementLocators().getCaptchaInputXpaths());
    }


}
