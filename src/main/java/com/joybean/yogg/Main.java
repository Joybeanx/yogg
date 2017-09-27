package com.joybean.yogg;

import com.joybean.yogg.support.ContextHolder;
import com.joybean.yogg.view.FxmlView;
import com.joybean.yogg.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The main entrance of YOGG
 * <p>Some code of integrating Spring Boot with JavaFX reference https://github.com/mvpjava/springboot-javafx-tutorial</p>
 */
@SpringBootApplication
public class Main extends Application {
    private ViewManager viewManager;

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        bootstrapSpringApplicationContext();
    }

    @Override
    public void start(Stage stage) throws Exception {
        viewManager = ContextHolder.getSpringContext().getBean(ViewManager.class, stage);
        displayInitialScene();
    }


    private void displayInitialScene() {
        viewManager.switchScene(FxmlView.MAIN);
    }

    private void bootstrapSpringApplicationContext() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        String[] args = getParameters().getRaw().stream().toArray(String[]::new);
        //needed for TestFX integration testing or else will get a java.awt.HeadlessException during tests
        builder.headless(false);
        builder.run(args);
    }
}
