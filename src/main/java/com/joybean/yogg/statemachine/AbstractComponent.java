package com.joybean.yogg.statemachine;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.report.record.SMSSendingRecord;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

import java.lang.reflect.InvocationTargetException;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author joybean
 */
public abstract class AbstractComponent {

    @Autowired
    protected YoggConfig config;

    protected HtmlPage getCurrentPage(StateContext<String, String> context) {
        Page page = getVariable(STATE_MACHINE_VARIABLE_CURRENT_PAGE, context);
        if (page == null || !page.isHtmlPage()) {
            return null;
        }
        return (HtmlPage) page;
    }

    protected void updateCurrentPage(Page page, StateContext<String, String> context) {
        if (page != null) {
            putVariable(STATE_MACHINE_VARIABLE_CURRENT_PAGE, page, context);
        }
    }

    protected String getCurrentPageUrl(StateContext<String, String> context) {
        Page page = getCurrentPage(context);
        if (page != null) {
            return page.getUrl().toString();
        }
        return getVariable(STATE_MACHINE_INPUT_WEBSITE, context);
    }

    protected void updateRecordProperty(String key, Object value, StateContext<String, String> context) throws IllegalAccessException, InvocationTargetException {
        SMSSendingRecord record = getVariable(STATE_MACHINE_OUTPUT_RECORD, context);
        BeanUtils.copyProperty(record, key, value);
    }


    protected <T> T getRecordProperty(String key, StateContext<String, String> context) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        SMSSendingRecord record = getVariable(STATE_MACHINE_OUTPUT_RECORD, context);
        return (T) BeanUtils.getProperty(record, key);
    }

    protected void putVariable(String key, Object value, StateContext<String, String> context) {
        StateMachine stateMachine = context.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(key, value);
    }

    protected <T> T getVariable(String key, StateContext<String, String> context) {
        StateMachine stateMachine = context.getStateMachine();
        return (T) stateMachine.getExtendedState().getVariables().get(key);
    }

}
