package com.joybean.yogg.statemachine;

import com.gargoylesoftware.htmlunit.WebClient;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author joybean
 */
public class StateMachineEventListener extends StateMachineListenerAdapter<String, String> {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(StateMachineEventListener.class);

    public void stateMachineStopped(StateMachine<String, String> stateMachine) {
        if (stateMachine.isComplete()) {
            String url = stateMachine.getExtendedState().get(STATE_MACHINE_INPUT_WEBSITE, String.class);
            LOGGER.info("State machine#{} for [{}] had been completed", stateMachine.getUuid(), url);
            cleanUp(stateMachine);
        }
    }

    @Override
    public void stateMachineError(StateMachine<String, String> stateMachine, Exception exception) {
        String url = stateMachine.getExtendedState().get(STATE_MACHINE_INPUT_WEBSITE, String.class);
        LOGGER.info("State machine#{} for [{}] error", stateMachine.getUuid(), url, exception);
        SMSSendingRecord smsSendingRecord = stateMachine.getExtendedState().get(STATE_MACHINE_OUTPUT_RECORD, SMSSendingRecord.class);
        smsSendingRecord.setStatus(exception instanceof TimeoutException ? RecordStatus.TIME_OUT : RecordStatus.EXCEPTION);
        smsSendingRecord.setException(exception.toString());
        interruptAndCleanUp(stateMachine);
//        Object tlo = ThreadUtils.cleanThreadLocals();
//        if (tlo != null) {
//            LOGGER.error("Found thread local %s in %s while processing %s", tlo, Thread.currentThread(), url);
//        }
    }

    /**
     * Interrupt state machine and  close some resource referred by it
     *
     * @param stateMachine
     */
    private void interruptAndCleanUp(StateMachine<String, String> stateMachine) {
        /**
         *  TODO Ugly code:sometimes timeout for WebClient doesn't work and there is an infinite loop in javascript engine while executing WebClient.getPage(url),see com.joybean.yogg.SimulatorTests#testTimeout()
         */
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        WebClient webClient = (WebClient) variables.get(STATE_MACHINE_VARIABLE_WEB_CLIENT);
        variables.clear();
        if (webClient != null) {
            //Attempt to break long-time javascript as soon as possible,see HtmlScript.executeScriptIfNeeded
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.close();
        }
    }

    /**
     * Close some resource referred by state machine
     */
    private void cleanUp(StateMachine<String, String> stateMachine) {
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        WebClient webClient = (WebClient) variables.get(STATE_MACHINE_VARIABLE_WEB_CLIENT);
        variables.clear();
        if (webClient != null) {
            webClient.close();
        }
    }
}
