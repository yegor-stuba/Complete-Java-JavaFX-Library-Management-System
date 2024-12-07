package com.studyshare.server;

import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.studyshare.client.ClientApplication;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

        Platform.startup(() -> {
            ClientApplication clientApp = new ClientApplication();
            clientApp.start(new javafx.stage.Stage());
        });
    }
}