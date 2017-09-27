package com.joybean.yogg.view;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ResourceBundle;

@Configuration
public class ViewManagerConfig {
    @Autowired
     SpringFXMLLoader springFXMLLoader;

    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("Bundle");
    }

    @Bean
    @Lazy //Stage only created after Spring context bootstrap
    public ViewManager viewManager(Stage stage) throws IOException {
        return new ViewManager(springFXMLLoader, stage);
    }

}
