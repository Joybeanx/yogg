/**
 *
 */
package com.joybean.yogg.simulator;

import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.statemachine.StateMachinePool;
import com.joybean.yogg.support.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;
import static com.joybean.yogg.support.UrlUtils.toHttpFormat;

/**
 * A SMS sending simulator based on HtmlUnit.
 * <p>In order to send SMS by website SMS gateway,it will simulate a series of user operations on the website page.</p>
 *
 * @author joybean
 */
@Component
public class HtmlUnitSMSSendingSimulator extends AbstractSMSSendingSimulator {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(HtmlUnitSMSSendingSimulator.class);
    @Autowired
    private YoggConfig config;
    @Autowired
    private StateMachinePool stateMachinePool;
    private ScheduledExecutorService schedulerExecutor;

    @PostConstruct
    private void init() {
        schedulerExecutor = ThreadUtils.initScheduledThreadPool(
                "ScheduledTaskExecutorThread-%d", config.getThreads());
    }

    @Override
    public SMSSendingRecord trySend(List<String> targetPhoneNumbers, String url, String taskId) {
        Assert.notNull(targetPhoneNumbers, "Target phone numbers must not be null");
        Assert.hasLength(url, "URL must not be empty");
        LOGGER.info("Start to try to send SMS to {} by {}...", targetPhoneNumbers, url);
        StateMachine<String, String> stateMachine = null;
        SMSSendingRecord record = null;
        String _url;
        ScheduledFuture future = null;
        try {
            _url = toHttpFormat(url);
            stateMachine = stateMachinePool.borrowObject();
            setInput(_url, targetPhoneNumbers, stateMachine);
            record = setOutput(url, taskId, stateMachine);
            future = launch(stateMachine);
        } catch (Throwable e) {
            LOGGER.error("Failed to send SMS to {} by {}", targetPhoneNumbers, url, e);
            if (record == null) {
                record = buildExceptionRecord(taskId, url, e);
            }
        } finally {
            if (future != null) {
                future.cancel(true);
            }
            if (stateMachine != null) {
                stateMachinePool.returnObject(stateMachine);
            }
        }
        LOGGER.info("Complete trying to send SMS:{}", record);
        return record;
    }


    private void setInput(String url, List<String> phoneNumbers, StateMachine<String, String> stateMachine) {
        stateMachine.getExtendedState().getVariables().put(STATE_MACHINE_INPUT_WEBSITE, url);
        stateMachine.getExtendedState().getVariables().put(STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER, new LinkedList<>(phoneNumbers));
    }

    private SMSSendingRecord setOutput(String url, String taskId, StateMachine<String, String> stateMachine) {
        SMSSendingRecord smsSendingRecord = new SMSSendingRecord(taskId, url);
        smsSendingRecord.setCreateTime(new Date());
        stateMachine.getExtendedState().getVariables().put(STATE_MACHINE_OUTPUT_RECORD, smsSendingRecord);
        return smsSendingRecord;
    }

    private SMSSendingRecord buildExceptionRecord(String taskId, String url, Throwable e) {
        SMSSendingRecord record;
        record = new SMSSendingRecord(taskId, url);
        record.setStatus(RecordStatus.EXCEPTION);
        record.setException(e.toString());
        return record;
    }

    /**
     * Launch a state machine to execute current simulator
     *
     * @param stateMachine
     */
    private ScheduledFuture launch(final StateMachine<String, String> stateMachine) {
        final int timeout = config.getTimeout();
        ScheduledFuture scheduledFuture = schedulerExecutor.schedule(() -> {
            //Notify the state machine of timeout error when execution times out
            if (!stateMachine.isComplete()) {
                stateMachine.setStateMachineError(new TimeoutException(String.format("Execution time has exceeded %s ms", timeout)));
            }
        }, config.getTimeout(), TimeUnit.MILLISECONDS);
        stateMachine.start();
        return scheduledFuture;
    }

}
