package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.LinkedList;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author jobean
 */
@Component
public class InputPhoneNumberAction extends AbstractAction {
    @Override
    protected void doExecute(StateContext<String, String> context) throws Exception {
        LinkedList<String> targetPhoneNumbers = getVariable(STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER, context);
        Assert.notEmpty(targetPhoneNumbers, "Target phone number must not be empty");
        HtmlPage page = getCurrentPage(context);
        String phoneNumber = targetPhoneNumbers.peek();
        if (HtmlUnitUtils.inputFirstByXPaths(page, phoneNumber, config.getPageElementLocators().getPhoneNumberInputXpaths())) {
            sendEvent("PHONE_NUMBER_INPUT", context);
        } else {
            sendFailureEvent(RecordStatus.PHONE_NUMBER_INPUT_NOT_FOUND, context);
        }
    }
}
