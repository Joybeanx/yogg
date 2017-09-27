package com.joybean.yogg.statemachine;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.StateMachineState;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author jobean
 */
@Component
public class PooledStateMachineFactory extends BasePooledObjectFactory<StateMachine<String, String>> {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(PooledStateMachineFactory.class);
    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;

    @Override
    public StateMachine create() throws Exception {
        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine();
        return stateMachine;
    }

    @Override
    public PooledObject<StateMachine<String, String>> wrap(StateMachine<String, String> o) {
        return new DefaultPooledObject<>(o);
    }

    @Override
    public void passivateObject(PooledObject<StateMachine<String, String>> p)
            throws Exception {
        StateMachine<String, String> stateMachine = p.getObject();
        reset(stateMachine);
        LOGGER.info("State machine#{} had been returned to pool", stateMachine.getUuid());
    }

    /**
     * Reset state machine status for later reuse
     *
     * @param stateMachine
     */
    private void reset(StateMachine<String, String> stateMachine) {
        if (!stateMachine.isComplete()) {
            stateMachine.stop();
        }
        stateMachine.setStateMachineError(null);
        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(new DefaultStateMachineContext(null, null, null, new DefaultExtendedState(), new HashMap(), stateMachine.getId())));
        stateMachine.getStates().stream().filter(s -> s instanceof StateMachineState).forEach(s -> reset(((StateMachineState) s).getSubmachine()));
    }

}
