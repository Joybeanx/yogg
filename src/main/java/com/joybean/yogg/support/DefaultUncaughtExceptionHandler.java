/**
 * 
 */
package com.joybean.yogg.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author joybean
 */
public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private final static Logger LOGGER = LoggerFactory
	    .getLogger(DefaultUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
	    LOGGER.error("Failed to execute thread {}", t.getName(), e);
    }

}
