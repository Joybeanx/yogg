package com.joybean.yogg.view;


import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages switching views on the Primary Stage
 */
public class ViewManager {

    private static final Logger LOGGER = getLogger(ViewManager.class);
    private final Stage primaryStage;
    private final SpringFXMLLoader springFXMLLoader;

    public ViewManager(SpringFXMLLoader springFXMLLoader, Stage stage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = stage;
    }

    public void switchScene(final FxmlView view) {
        Parent root = load(view.getFxmlFile());
        show(root, view.getTitle());
    }

    public void showDialog(final FxmlView view, Consumer<ButtonType> okAction) {
        Dialog dialog = load(view.getFxmlFile());
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.CLOSE, ButtonType.OK
        );
        dialog.initOwner(primaryStage);
        dialog.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(okAction);
    }

    public Alert showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(primaryStage);
        alert.getDialogPane().setContentText(content);
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
        return alert;
    }

    public Dialog<ButtonType> showException(Throwable th,String msg) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Oops");

        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContentText("Details of the problem:");
        dialogPane.getButtonTypes().addAll(ButtonType.OK);
        dialogPane.setContentText(msg);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label("Exception stacktrace:");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        pw.close();

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane root = new GridPane();
        root.setVisible(false);
        root.setMaxWidth(Double.MAX_VALUE);
        root.add(label, 0, 0);
        root.add(textArea, 0, 1);
        dialogPane.setExpandableContent(root);
        dialog.showAndWait()
                .filter(response -> response == ButtonType.OK);
        return dialog;
    }

    private void show(final Parent rootNode, String title) {
        Scene scene = prepareScene(rootNode);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("view/image/icon.png"));
        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit("Unable to show scene for" + title, exception);
        }
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    /**
     * Loads the object hierarchy from a FXML document and returns to root node
     * of that hierarchy.
     *
     * @return Parent root node of the FXML document hierarchy
     */
    private <T> T load(String fxmlFilePath) {
        T rootNode = null;
        try {
            rootNode = (T) springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception e) {
            logAndExit("Unable to load " + fxmlFilePath, e);
        }
        return rootNode;
    }


    private void logAndExit(String errorMsg, Exception exception) {
        LOGGER.error(errorMsg, exception);
        Platform.exit();
    }

}
