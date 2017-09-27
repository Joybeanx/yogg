package com.joybean.yogg.task.controller;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.service.ReportService;
import com.joybean.yogg.support.JFXUtils;
import com.joybean.yogg.task.Task;
import com.joybean.yogg.task.service.TaskService;
import com.joybean.yogg.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Instant task controller<p/>
 * An instant task is the task manually triggered on UI
 */
@Controller
@Lazy
public class InstantTaskController extends SplitPane implements Initializable {
    private static final String START_IMAGE_URL = "view/image/start.png";
    private static final String STOP_IMAGE_URL = "view/image/stop.gif";
    private static Image startImage = new Image(START_IMAGE_URL, 400, 300, false, false);
    private static Image stopImage = new Image(STOP_IMAGE_URL, 400, 300, false, false);
    @Autowired
    private ViewManager viewManager;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ReportService reportService;
    @FXML
    private GridPane inputPane;
    @FXML
    private TextField targetPhoneNumberInput;
    @FXML
    private ImageView controlImageView;
    @FXML
    private ToggleButton toggleButton;
    @FXML
    private TextArea logs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Start a instant task
     */
    public void start() {
        String[] targets = validateTargetPhoneNumber();
        if (targets == null) {
            return;
        }
        Task task = createTask(targets);
        javafx.concurrent.Task jfxTask = JFXUtils.newJFXTask(() -> {
            logs.clear();
            TaskReport taskReport = taskService.startTask(task);
            return taskReport;
        });

        task.getTaskContext().setJfxTask(jfxTask);
        //Show exception dialog if there is any exception after completion
        EventHandler onSucceededHandler = (e) ->
        {
            displayStart();
            Throwable exception = null;
            try {
                TaskReport taskReport = (TaskReport) jfxTask.get();
                exception = taskReport.getException();
            } catch (Exception ex) {
                exception = ex;
            }
            if (exception != null) {
                viewManager.showException(exception, "Failed to execute task due to exception");
            }
        };
        JFXUtils.startJFXTask(jfxTask, onSucceededHandler);
        displayStop();
    }

    private Task createTask(String[] targets) {
        Task task = taskService.createTask(targets);
        if (!reportService.isTaskReportComplete(Task.TASK_ID_MAIN)) {
            task.setTaskId(Task.TASK_ID_MAIN);
        }
        return task;
    }

    /**
     * Stop current instant task
     *
     * @param event
     */
    public void stop(Event event) {
        taskService.stopTask(null);
        displayStart();
    }

    /**
     * Set stop image and stop event handler
     */
    private void displayStop() {
        controlImageView.setImage(stopImage);
        controlImageView.setOnMouseClicked(e -> stop(e));
    }

    /**
     * Set start image and start event handler
     */
    private void displayStart() {
        controlImageView.setImage(startImage);
        controlImageView.setOnMouseClicked(e -> start());
    }

    private String[] validateTargetPhoneNumber() {
        String targetPhoneNumbers = targetPhoneNumberInput.getText();
        if (StringUtils.isBlank(targetPhoneNumbers)) {
            viewManager.showAlert(Alert.AlertType.ERROR, "Please input target phone number");
            return null;
        }
        String[] targets = targetPhoneNumbers.split("[^\\d]]");
        List<String> invalidTargets = new ArrayList();
        for (String target : targets) {
            if (target.length() != 11) {
                invalidTargets.add(target);
            }
        }
        if (CollectionUtils.isNotEmpty(invalidTargets)) {
            viewManager.showAlert(Alert.AlertType.ERROR, String.format("Invalid target phone number %s)", invalidTargets));
            return null;
        }
        return targets;
    }

    public void switchTargetDisplayMode(ActionEvent actionEvent) {
        inputPane.setLayoutY(toggleButton.isSelected() ? -inputPane.sceneToLocal(toggleButton.localToScene(0, 0)).getY() : 0);
    }

    public TextArea getLogs() {
        return logs;
    }

}
