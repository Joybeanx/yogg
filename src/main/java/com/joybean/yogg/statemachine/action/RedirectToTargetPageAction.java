package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.report.record.RecordStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * Action of redirecting to target page.
 * <p> A target page is the page on which we attempt to send SMS,such as a register page.</p>
 *
 * @author jobean
 */
@Component
public class RedirectToTargetPageAction extends ClickAction {
    @Override
    protected void onBefore(StateContext<String, String> context) throws Exception {
        WebClient webClient = getVariable(STATE_MACHINE_VARIABLE_WEB_CLIENT, context);
        //Enable javascript for subsequent actions after completing loading home page
        webClient.getOptions().setJavaScriptEnabled(true);
    }

    @Override
    protected void onAfter(StateContext<String, String> context) throws InvocationTargetException, IllegalAccessException {
        HtmlPage currentPage = getCurrentPage(context);
        updateRecordProperty(RECORD_PROPERTY_TARGET_PAGE_URL, currentPage.getUrl().toString(), context);
        sendEvent("TARGET_PAGE_LOADED", context);
    }

    @Override
    protected RecordStatus statusOnNoElement() {
        return RecordStatus.REDIRECT_LINK_NOT_FOUND;
    }

    @Override
    protected RecordStatus statusOnException() {
        return RecordStatus.TARGET_PAGE_NOT_LOADED;
    }

    @Override
    protected List<String> getXPath() {
        return config.getPageElementLocators().getRedirectLinkXpaths();
    }

}
