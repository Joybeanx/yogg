/**
 *
 */
package com.joybean.yogg.report.record;

import com.joybean.yogg.support.EnumValue;

/**
 * @author joybean
 */
public enum RecordStatus implements EnumValue {
    UNKNOWN(0),
    SUCCESS(1),
    EXCEPTION(-1),
    TIME_OUT(-2),
    HOME_PAGE_NOT_LOADED(-3),
    REDIRECT_LINK_NOT_FOUND(-4),
    TARGET_PAGE_NOT_LOADED(-5),
    PHONE_NUMBER_INPUT_NOT_FOUND(-6),
    CAPTCHA_INPUT_NOT_FOUND(-7),
    SOLVE_CAPTCHA_FAILURE(-8),
    SEND_BUTTON_NOT_FOUND(-9),
    REQUEST_URL_NOT_DETECTED(-10);
    private int value;

     RecordStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
