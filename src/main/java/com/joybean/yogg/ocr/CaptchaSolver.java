package com.joybean.yogg.ocr;

import java.io.File;

/**
 * Captcha solver
 * <p>
 *   When we prepare to send SMS on a website page,a captcha input is often required.<br/>
 *   The captcha solver is used for trying to defeat the captcha.
 * </p>
 *
 * @author joybean
 */
public interface CaptchaSolver {
    /**
     * Solve the specified captcha and get output text
     *
     * @param captcha captcha file to solve
     * @return text that the captcha represents
     */
    String solve(File captcha);

    /**
     * Solve the captcha that the specified captcha path describes and get output text
     *
     * @param captchaPath the location of the captcha to solve
     * @return text that the captcha represents
     */
    String solve(String captchaPath);
}
