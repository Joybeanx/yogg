package com.joybean.yogg.statemachine.action;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author joybean
 */
@Component
public class ResendAction extends ClickSendButtonAction {

    @Override
    protected List<String> getXPath() {
        return config.getPageElementLocators().getConfirmButtonXpaths();
    }
}
