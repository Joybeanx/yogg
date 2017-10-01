package com.joybean.yogg.statemachine.action;

import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.HtmlUnitUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author jobean
 */
@Component
public class InitWebClientAction extends AbstractAction {

    static {
        //If the Domain Name System (DNS) server is not configured to handle IPv6 queries, the application may have to wait for the IPv6 query to timeout for IPv6 queries.
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    @Override
    protected void doExecute(StateContext<String, String> context) throws Exception {
        final WebClient webClient = HtmlUnitUtils.buildWebClient(config.getTimeout(),config.getProxy());
        putVariable(STATE_MACHINE_VARIABLE_WEB_CLIENT, webClient, context);
        final SMSSendingRecord record = getVariable(STATE_MACHINE_OUTPUT_RECORD, context);
        LinkedList<String> targetPhoneNumberList = getVariable(STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER, context);
        webClient.setWebConnection(new HttpWebConnection(webClient) {
            public WebResponse getResponse(final WebRequest request)
                    throws IOException {
                String requestUrl;
                String currentPhoneNumber = targetPhoneNumberList.peek();
                //if current request matches sms request format,set it to record temporally,but may be unset it later because of other reasons
                if ((requestUrl = matches(request, currentPhoneNumber)) != null) {
                    record.setSmsRequestUrl(requestUrl.replace(currentPhoneNumber, STRING_PLACEHOLDER));
                }
                return super.getResponse(request);
            }
        });
        sendEvent("WEB_CLIENT_INITIATED", context);
    }

    private String matches(WebRequest request, String currentPhoneNumber) {
        String requestUrl = request.getUrl().toString();
        String requestBody = request.getRequestBody();
        if (requestUrl.contains(currentPhoneNumber)) {
            return requestUrl;
        }
        if (null != requestBody && requestBody.contains(currentPhoneNumber)) {
            return requestUrl + "?" + requestBody;
        }
        return null;
    }
}
