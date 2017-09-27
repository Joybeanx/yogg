package com.joybean.yogg.task.controller;

import com.joybean.yogg.view.FxmlView;
import com.joybean.yogg.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.io.IOException;

/**
 * Scheduled task controller<p/>
 * A scheduled task is the task triggered by scheduler
 *
 * @author jobean
 */
@Controller
@Lazy
public class ScheduledTaskController extends SplitPane {
    @Autowired
    private ViewManager viewManager;

    public void newTask(ActionEvent actionEvent) throws IOException {
        //TODO
        viewManager.showDialog(FxmlView.NEW_TASK, (s) -> System.out.println(s));
    }
}
