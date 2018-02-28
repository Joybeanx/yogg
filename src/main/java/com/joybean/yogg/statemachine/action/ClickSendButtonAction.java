package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.html.HtmlAttributeChangeEvent;
import com.gargoylesoftware.htmlunit.html.HtmlAttributeChangeListener;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;
/**
 * @author joybean
 */
@Component
public class ClickSendButtonAction extends ClickAction {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(ClickSendButtonAction.class);

    @Override
    protected void onBefore(StateContext<String, String> context) throws Exception {
        HtmlPage page = getCurrentPage(context);
        HtmlElement sendButton = HtmlUnitUtils.findFirstByXPaths(page, getXPath());
        if (sendButton != null) {
            //some attributes that control the appearance of send button must be changed after triggering a send successfully
            sendButton.addHtmlAttributeChangeListener(new HtmlAttributeChangeListener() {
                @Override
                public void attributeReplaced(HtmlAttributeChangeEvent event) {
                    String attrName = event.getName();
                    String oldValue = event.getValue();
                    HtmlElement newElement = event.getHtmlElement();
                    String newValue = newElement.getAttribute(attrName);
                    LOGGER.info("attribute[{}] chang detected for {}:[{}] =======> [{}]", attrName, newElement, oldValue, newValue);
                    //putVariable(STATE_MACHINE_VARIABLE_IS_SEND_BTN_CHANGED, true, context);
                }

                @Override
                public void attributeAdded(HtmlAttributeChangeEvent event) {

                }

                @Override
                public void attributeRemoved(HtmlAttributeChangeEvent event) {

                }
            });
        }
    }

    @Override
    protected void onAfter(StateContext<String, String> context) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Boolean sendButtonChanged = getVariable(STATE_MACHINE_VARIABLE_IS_SEND_BTN_CHANGED, context);
        String smsRequestUrl = getRecordProperty(RECORD_PROPERTY_SMS_REQUEST_URL, context);
        //We consider the sending is successful if a valid SMS request url is detected after clicking sending button
        if (smsRequestUrl != null) {
            updateRecordProperty(RECORD_PROPERTY_STATUS, RecordStatus.SUCCESS, context);
            ((LinkedList<String>) getVariable(STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER, context)).pop();
            LOGGER.info("Succeed in sending SMS,request url:{}", smsRequestUrl);
            sendEvent("SEND_SUCCESS", context);
        } else {
            updateRecordProperty(RECORD_PROPERTY_SMS_REQUEST_URL, null, context);
            updateRecordProperty(RECORD_PROPERTY_STATUS, RecordStatus.REQUEST_URL_NOT_DETECTED, context);
            sendEvent("SEND_FAILURE", context);
        }
    }

    @Override
    protected RecordStatus statusOnNoElement() {
        return RecordStatus.SEND_BUTTON_NOT_FOUND;
    }

    @Override
    protected RecordStatus statusOnException() {
        return null;
    }

    @Override
    protected List<String> getXPath() {
        return config.getPageElementLocators().getSendButtonXpaths();
    }
}
