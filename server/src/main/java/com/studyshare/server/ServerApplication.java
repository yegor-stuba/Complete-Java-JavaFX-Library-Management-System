package com.studyshare.server;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.studyshare.client.ClientApplication;

@SpringBootApplication
public class ServerApplication {
    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
            Platform.startup(() -> {
                try {
                    ClientApplication clientApp = new ClientApplication();
                    clientApp.start(new javafx.stage.Stage());
                } catch (Exception e) {
                    log.error("Failed to start client application: {}", e.getMessage(), e);
                    Platform.exit();
                }
            });
        } catch (Exception e) {
            log.error("Failed to start server: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}