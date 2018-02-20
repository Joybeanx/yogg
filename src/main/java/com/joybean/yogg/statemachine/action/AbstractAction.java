package com.joybean.yogg.statemachine.action;

import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.statemachine.AbstractComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.support.AbstractStateMachine;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.*;

/**
 * @author joybean
 */
public abstract class AbstractAction extends AbstractComponent implements Action<String, String> {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(AbstractAction.class);

    protected static final String STRING_PLACEHOLDER = "%s";

    protected abstract void doExecute(StateContext<String, String> context) throws Exception;

    @Override
    public void execute(StateContext<String, String> context) {
        try {
            doExecute(context);
        } catch (Throwable e) {
            LOGGER.error("Failed to execute {} in {}", this.getClass().getSimpleName(), getVariable(STATE_MACHINE_INPUT_WEBSITE, context), e);
            sendFailureEvent(e, context);
        }
    }


    protected void sendEvent(String event, StateContext<String, String> context) {
        if (hasError(context)) {
            return;
        }
        context.getStateMachine().sendEvent(event);
    }


    protected void sendFailureEvent(RecordStatus status, StateContext<String, String> context) {
        sendFailureEvent(status, null, context);
    }

    protected void sendFailureEvent(Throwable e, StateContext<String, String> context) {
        sendFailureEvent(null, e, context);
    }

    protected void sendFailureEvent(RecordStatus status, Throwable e, StateContext<String, String> context) {
        if (hasError(context)) {
            return;
        }
        try {
            String url = getCurrentPageUrl(context);
            if (status != null) {
                LOGGER.info("{} while processing {}", status, url);
                updateRecordProperty(RECORD_PROPERTY_STATUS, status, context);
            }
            if (e != null) {
                LOGGER.info("Exception occurred while processing {}", url, e);
                updateRecordProperty(RECORD_PROPERTY_EXCEPTION, e.toString(), context);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to update record", ex);
        } finally {
            sendEvent(EVENT_FAILURE, context);
        }

    }

    private boolean hasError(StateContext<String, String> context) {
        AbstractStateMachine stateMachine = (AbstractStateMachine) context.getStateMachine();
        return stateMachine.hasStateMachineError();
    }
}
