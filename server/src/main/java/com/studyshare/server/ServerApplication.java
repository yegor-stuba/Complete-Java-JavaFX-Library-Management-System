package com.studyshare.server;

import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

        // Launch JavaFX client after server is up
        Platform.startup(() -> {
            com.studyshare.client.ClientApplication clientApp = new com.studyshare.client.ClientApplication();
            clientApp.start(new javafx.stage.Stage());
        });
    }
}