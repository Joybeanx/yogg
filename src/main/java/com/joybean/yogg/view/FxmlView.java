package com.joybean.yogg.view;

import java.util.ResourceBundle;

/**
 * Enums of JFX views
 */
public enum FxmlView {

    MAIN {
        @Override
        String getTitle() {
            return getDisplayValue("app.title");
        }

        @Override
        String getFxmlFile() {
            return "/view/fxml/Main.fxml";
        }
    }, NEW_TASK {
        @Override
        String getTitle() {
            return  getDisplayValue("new_task.title");
        }

        @Override
        String getFxmlFile() {
            return "/view/fxml/NewTask.fxml";
        }
    };

    abstract String getTitle();

    abstract String getFxmlFile();


    public String getDisplayValue(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }
}
