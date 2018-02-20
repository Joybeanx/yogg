package com.joybean.yogg.statemachine;

import com.joybean.yogg.config.YoggConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author joybean
 */
@Component
public class StateMachinePool extends GenericObjectPool<StateMachine<String, String>> {

    private static GenericObjectPoolConfig config;
    @Autowired
    private YoggConfig properties;

    static {
        config = new GenericObjectPoolConfig();
        config.setJmxEnabled(false);
    }

    @Autowired
    public StateMachinePool(PooledStateMachineFactory factory) {
        super(factory, config);
    }

    @PostConstruct
    void init() {
        setMaxTotal(properties.getThreads());
    }

}
