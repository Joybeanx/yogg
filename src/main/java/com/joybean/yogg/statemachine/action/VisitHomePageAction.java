package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.joybean.yogg.report.record.RecordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author jobean
 */
@Component
public class VisitHomePageAction extends AbstractAction {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(VisitHomePageAction.class);

    @Override
    protected void doExecute(StateContext<String, String> context) throws Exception {
        String url = getVariable(STATE_MACHINE_INPUT_WEBSITE, context);
        Assert.hasText(url, "Url must not be null");
        WebClient webClient = getVariable(STATE_MACHINE_VARIABLE_WEB_CLIENT, context);
        Page page;
        try {
            page = webClient.getPage(url);
        } catch (Exception e) {
            LOGGER.error("Unable to access {}", url, e);
            sendFailureEvent(RecordStatus.HOME_PAGE_NOT_LOADED, e, context);
            return;
        }
        putVariable(STATE_MACHINE_VARIABLE_CURRENT_PAGE, page, context);
        sendEvent("HOME_PAGE_LOADED", context);

    }
}
