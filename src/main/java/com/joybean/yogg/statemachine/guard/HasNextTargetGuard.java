package com.joybean.yogg.statemachine.guard;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;

import static com.joybean.yogg.statemachine.StateMachineConfig.Constants.STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER;

/**
 * A guard indicates whether there is more target phone number to process
 * @author joybean
 */
@Component
public class HasNextTargetGuard extends AbstractGuard {

    @Override
    public boolean evaluate(StateContext<String,String> context) {
        LinkedList<String> phoneNumbers = getVariable(STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER, context);
        return !CollectionUtils.isEmpty(phoneNumbers);
    }
}
