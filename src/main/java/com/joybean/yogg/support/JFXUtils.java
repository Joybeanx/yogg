package com.joybean.yogg.support;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

/**
 * @author joybean
 */
public class JFXUtils {

    public static <T> Task newJFXTask(Callable<T> caller) {
        return new JFXTask(caller);
    }

    public static Task startJFXTask(Task jfxTask, EventHandler onSucceededHandler) {
        Assert.notNull(jfxTask, "JFX task must not be null");
        return startJFXTask(jfxTask, onSucceededHandler, null, null);
    }

    public static Task startJFXTask(Task jfxTask, EventHandler onSucceededHandler, EventHandler onFailedHandler, EventHandler onCancelledHandler) {
        Assert.notNull(jfxTask, "JFX task must not be null");
        jfxTask.setOnSucceeded(onSucceededHandler);
        jfxTask.setOnFailed(onFailedHandler);
        jfxTask.setOnCancelled(onCancelledHandler);
        Thread th = new Thread(jfxTask);
        th.setDaemon(true);
        th.start();
        return jfxTask;
    }

    public static void run(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    static class JFXTask<T> extends Task<T> {
        private Callable<T> caller;

        JFXTask(Callable<T> caller) {
            this.caller = caller;
        }

        @Override
        protected T call() throws Exception {
            return caller.call();
        }
    }

    /**
     * Bind a numeric field change listener that prevents inputting invalid character
     *
     * @param textField
     */
    public static void bindNumericTextFieldChangeListener(TextField textField) {
        ChangeListener<String> changeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
        textField.textProperty().addListener(changeListener);
    }
}
