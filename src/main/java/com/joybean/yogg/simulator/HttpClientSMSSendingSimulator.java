package com.joybean.yogg.simulator;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.support.UrlUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * A SMS sending simulator based on HttpClient.
 * <p>By making use last successful SMS request url template to send SMS,it's more fast but has less success rate than {@link com.joybean.yogg.simulator.HtmlUnitSMSSendingSimulator HtmlUnitSMSSendingSimulator}.</p>
 *
 * @author joybean
 */
@Component
public class HttpClientSMSSendingSimulator extends AbstractSMSSendingSimulator {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(HttpClientSMSSendingSimulator.class);

    @Override
    //TODO POST
    public SMSSendingRecord trySend(List<String> targetPhoneNumbers, String url, String taskId) {
        final String _url = UrlUtils.toHttpFormat(url);
        SMSSendingRecord smsSendingRecord = new SMSSendingRecord(taskId, _url);
        CloseableHttpResponse response;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (String targetPhoneNumber : targetPhoneNumbers) {
                HttpGet httpGet = new HttpGet(String.format(url, targetPhoneNumber));
                response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    LOGGER.warn(
                            "Got unexpected http status code {} after requesting SMS on {}",
                            statusCode, url);
                }
            }
        } catch (Throwable e) {
            smsSendingRecord.setStatus(RecordStatus.EXCEPTION);
            smsSendingRecord.setException(e.toString());
            LOGGER.error("Failed to to send SMS to {} by {}", targetPhoneNumbers,
                    url, e);
        }
        smsSendingRecord.setCreateTime(new Date());
        return smsSendingRecord;
    }
}
