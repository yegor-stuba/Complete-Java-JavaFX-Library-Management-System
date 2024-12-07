package com.studyshare.client;

import com.studyshare.client.service.*;
import com.studyshare.client.service.impl.AuthenticationServiceImpl;
import com.studyshare.client.service.impl.TransactionServiceImpl;
import com.studyshare.client.service.impl.UserServiceImpl;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.SceneManager;
import com.studyshare.client.util.ControllerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        RestClient restClient = new RestClient();
        ConnectionMonitor connectionMonitor = new ConnectionMonitor();
        UserService userService = new UserServiceImpl(restClient);
        BookService bookService = new BookServiceImpl(restClient);
        TransactionService transactionService = new TransactionServiceImpl(restClient);
        AuthenticationService authService = new AuthenticationServiceImpl(restClient);

        SceneManager sceneManager = new SceneManager(primaryStage);
        ControllerFactory controllerFactory = new ControllerFactory(
            sceneManager,
            userService,
            bookService,
            transactionService,
            authService
        );
        sceneManager.setControllerFactory(controllerFactory);

        connectionMonitor.connectedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                Scene currentScene = primaryStage.getScene();
                if (currentScene != null && currentScene.getRoot() instanceof Parent) {
                    Parent root = (Parent) currentScene.getRoot();
                    Label connectionLabel = (Label) root.lookup("#connectionStatus");
                    if (connectionLabel != null) {
                        connectionLabel.setText(newVal ? "Connected" : "Offline");
                        connectionLabel.getStyleClass().setAll("connection-status",
                            newVal ? "connected" : "disconnected");
                    }
                }
            });
        });

        primaryStage.setTitle("StudyShare Library");
        sceneManager.switchToLogin();
        primaryStage.show();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}