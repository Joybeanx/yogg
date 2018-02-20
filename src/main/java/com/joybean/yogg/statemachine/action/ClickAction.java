package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.springframework.statemachine.StateContext;

import java.util.List;

/**
 * @author joybean
 */
public abstract class ClickAction extends AbstractAction {

    protected abstract void onAfter(StateContext<String, String> context) throws Exception;

    /**
     * Get a record status when there is no matched element by XPaths
     * @return
     */
    protected abstract RecordStatus statusOnNoElement();

    /**
     * Get a record status after a exception is thrown
     * @return
     */
    protected abstract RecordStatus statusOnException();

    protected abstract void onBefore(StateContext<String, String> context) throws Exception;

    protected abstract List<String> getXPath();

    @Override
    public void doExecute(StateContext<String, String> context) throws Exception {
        onBefore(context);
        HtmlPage page = getCurrentPage(context);
        Page newPage;
        try {
            newPage = HtmlUnitUtils.clickFirstByXPaths(page, getXPath());
        } catch (Exception e) {
            sendFailureEvent(statusOnException(), e, context);
            return;
        }
        if (newPage == null) {
            sendFailureEvent(statusOnNoElement(), context);
            return;
        }
        updateCurrentPage(newPage, context);
        onAfter(context);
    }


}
