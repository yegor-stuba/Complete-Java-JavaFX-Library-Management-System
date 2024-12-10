package com.studyshare.client;

import com.studyshare.client.service.*;
import com.studyshare.client.service.impl.AuthenticationServiceImpl;
import com.studyshare.client.service.impl.TransactionServiceImpl;
import com.studyshare.client.service.impl.UserServiceImpl;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.client.util.ControllerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApplication extends Application {
    private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);
    private ConnectionMonitor connectionMonitor;

    @Override
public void start(Stage primaryStage) {
    try {
        initializeServices(primaryStage);
        setupErrorHandling();
        setupConnectionMonitoring(primaryStage);
    } catch (Exception e) {
        log.error("Application failed to start: {}", e.getMessage());
        Platform.exit();
    }
}

private void setupErrorHandling() {
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
        log.error("Uncaught exception: {}", throwable.getMessage(), throwable);
        Platform.runLater(() ->
            AlertUtil.showError("System Error",
                "An unexpected error occurred: " + throwable.getMessage()));
    });
}

    private void initializeServices(Stage primaryStage) {
        // Initialize core services
        RestClient restClient = new RestClient();
        connectionMonitor = new ConnectionMonitor();

        // Initialize business services
        UserService userService = new UserServiceImpl(restClient);
        BookService bookService = new BookServiceImpl(restClient);
        TransactionService transactionService = new TransactionServiceImpl(restClient);
        AuthenticationService authService = new AuthenticationServiceImpl(restClient);

        // Setup scene management
        SceneManager sceneManager = new SceneManager(primaryStage);
        ControllerFactory controllerFactory = new ControllerFactory(
            sceneManager,
            userService,
            bookService,
            transactionService,
            authService
        );
        sceneManager.setControllerFactory(controllerFactory);

        // Setup connection monitoring
        setupConnectionMonitoring(primaryStage);

        // Start with login scene
        primaryStage.setTitle("Library Management System");
        sceneManager.switchToLogin();
        primaryStage.show();
    }

    private void setupConnectionMonitoring(Stage primaryStage) {
        connectionMonitor.connectedProperty().addListener((obs, oldVal, newVal) ->
            Platform.runLater(() -> {
                if (primaryStage.getScene() != null) {
                    Label connectionLabel = (Label) primaryStage.getScene().lookup("#connectionStatus");
                    if (connectionLabel != null) {
                        connectionLabel.setText(newVal ? "Connected" : "Offline");
                        connectionLabel.getStyleClass().setAll("connection-status",
                            newVal ? "connected" : "disconnected");
                    }
                }
            }));
    }

    @Override
    public void stop() {
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}