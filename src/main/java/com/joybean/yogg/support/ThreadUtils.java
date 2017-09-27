/**
 *
 */
package com.joybean.yogg.support;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author joybean
 */
public class ThreadUtils {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(ThreadUtils.class);

    public static ExecutorService initFixedThreadPool(String namePattern,
                                                      boolean daemonFlag, int priority, int nThreads) {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern(namePattern).daemon(daemonFlag)
                .priority(priority)
                .uncaughtExceptionHandler(new DefaultUncaughtExceptionHandler())
                .build();
        return Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    public static ExecutorService initFixedThreadPool(String namePattern,
                                                      int nThreads) {
        return initFixedThreadPool(namePattern, false, Thread.NORM_PRIORITY, nThreads);
    }

    public static ScheduledExecutorService initScheduledThreadPool(String namePattern,
                                                                   boolean daemonFlag, int priority, int nThreads) {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern(namePattern).daemon(daemonFlag)
                .priority(priority)
                .uncaughtExceptionHandler(new DefaultUncaughtExceptionHandler())
                .build();
        return Executors.newScheduledThreadPool(nThreads, threadFactory);
    }

    public static ScheduledExecutorService initScheduledThreadPool(String namePattern,
                                                                   int nThreads) {
        return initScheduledThreadPool(namePattern, false, Thread.NORM_PRIORITY, nThreads);
    }

    public static Object cleanThreadLocals() {
        Thread thread = Thread.currentThread();
        try {
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalTable = threadLocalsField.get(thread);


            Class threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Object table = tableField.get(threadLocalTable);


            Field referentField = Reference.class.getDeclaredField("referent");
            referentField.setAccessible(true);

            for (int i = 0; i < Array.getLength(table); i++) {

                Object entry = Array.get(table, i);
                if (entry != null) {
                    // Get a reference to the thread local object and remove it from the table
                    ThreadLocal threadLocal = (ThreadLocal) referentField.get(entry);
                    if (threadLocal != null) {
                        Object o = threadLocal.get();
                        if (o != null && o instanceof List) {
                            if (((List) o).size() > 0 && ((List) o).get(0) instanceof Node) {
                                threadLocal.remove();
                                return o;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to clean thread locals for {}", thread, e);
        }
        return null;
    }

}
