package com.joybean.yogg.support;

import com.joybean.yogg.task.controller.InstantTaskController;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handle output for UI console while {@link com.joybean.yogg.task.executor.InstantTaskExecutor} is running
 *
 * @author joybean
 */
public class UIConsole extends OutputStream {
    private final static String LOG_SEPARATOR = "\n";
    private final static int BUFF_SIZE = 300;
    /**
     * jfx controler which represents UI console
     */
    private TextArea output;


    @Override
    public void write(int b) throws IOException {

    }

    public void write(byte b[]) throws IOException {
        initOutput();
        JFXUtils.run(() -> {
            output.appendText(new String(b));
            int currentLineSize = StringUtils.countMatches(output.getText(), LOG_SEPARATOR);
            int i = 0;
            //Delete the first console log automatically when buffer is full
            while (i < currentLineSize - BUFF_SIZE) {
                int firstLineIdx = output.getText().indexOf(LOG_SEPARATOR);
                output.deleteText(0, firstLineIdx + 1);
                i++;
            }
        });
    }

    private void initOutput() {
        if (output == null) {
            InstantTaskController instantTaskController = ContextHolder.getSpringContext().getBean(InstantTaskController.class);
            output = instantTaskController.getLogs();
        }
    }
}
