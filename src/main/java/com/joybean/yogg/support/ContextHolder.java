package com.joybean.yogg.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author jobean
 */
@Component
public class ContextHolder implements ApplicationContextAware {

    private static ApplicationContext springContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    public static ApplicationContext getSpringContext() {
        if (springContext == null) {
            throw new YoggException("Spring context not initialized");
        }
        return springContext;
    }
}
