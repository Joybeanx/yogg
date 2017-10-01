package com.joybean.yogg.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author jobean
 */
@Component
public class ContextHolder implements ApplicationContextAware {

    private static ApplicationContext springContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setSpringContext(applicationContext);
    }

    private static void setSpringContext(ApplicationContext springContext) {
        ContextHolder.springContext = springContext;
    }

    public static ApplicationContext getSpringContext() {
        if (springContext == null) {
            throw new YoggException("Spring context not initialized");
        }
        return springContext;
    }
}
