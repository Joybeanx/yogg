package com.joybean.yogg.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContextRepository;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.uml.UmlStateMachineModelFactory;

@Configuration
public class StateMachineConfig {

    public static class Constants {
        public static final String STATE_MACHINE_INPUT_TARGET_PHONE_NUMBER = "targetPhoneNumber";
        public static final String STATE_MACHINE_INPUT_WEBSITE = "website";
        public static final String STATE_MACHINE_VARIABLE_WEB_CLIENT = "webClient";
        public static final String STATE_MACHINE_VARIABLE_CURRENT_PAGE = "currentPage";
        public static final String STATE_MACHINE_VARIABLE_IS_SEND_BTN_CHANGED = "isSendBtnChanged";
        public static final String RECORD_PROPERTY_TARGET_PAGE_URL = "targetPageUrl";
        public static final String RECORD_PROPERTY_HAS_CAPTCHA = "hasCaptcha";
        public static final String RECORD_PROPERTY_SMS_REQUEST_URL = "smsRequestUrl";
        public static final String RECORD_PROPERTY_EXCEPTION = "exception";
        public static final String RECORD_PROPERTY_STATUS = "status";
        public static final String STATE_MACHINE_OUTPUT_RECORD = "record";
        public static final String EVENT_FAILURE = "FAILURE";
    }

    @Configuration
    @EnableStateMachineFactory
    public static class Config extends StateMachineConfigurerAdapter<String, String> {
        @Override
        public void configure(StateMachineConfigurationConfigurer<String, String> config)
                throws Exception {
            config
                    .withConfiguration()
                    .autoStartup(false).listener(stateMachineEventListener());
        }

        @Override
        public void configure(StateMachineModelConfigurer<String, String> modelConfig)
                throws Exception {
            modelConfig
                    .withModel()
                    .factory(modelFactory());
        }

        @Bean
        public StateMachineModelFactory<String, String> modelFactory() {
            return new UmlStateMachineModelFactory("classpath:flow/sms-sending-flow.uml");
        }

        @Bean
        public StateMachineListener stateMachineEventListener() {
            return new StateMachineEventListener();
        }
    }

}
