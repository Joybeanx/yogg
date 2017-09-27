package com.joybean.yogg.support;

import ch.qos.logback.core.OutputStreamAppender;

/**
 * A logger appender that output log on UI console
 *
 * @author jobean
 */
public class UIConsoleAppender<E> extends OutputStreamAppender<E> {
    @Override
    public void start() {
        setOutputStream(new UIConsole());
        super.start();
    }
}
