package com.studyshare.client.controller;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.client.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController extends BaseController {
    private final AuthenticationService authService;
    private final UserService userService;
    private final SceneManager sceneManager;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label connectionStatus;

    public LoginController(UserService userService, SceneManager sceneManager, AuthenticationService authService) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.authService = authService;
    }

@FXML
public void handleLogin() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (validateInput(username, password)) {
        System.out.println("Login attempt: " + username);

        authService.login(username, password)
                .thenAccept(response -> {
                    System.out.println("Login response: " + response);
                    if (response != null) {
                        Platform.runLater(() -> {
                            try {
                                sceneManager.navigateBasedOnRole(response.getRole());
                            } catch (Exception e) {
                                System.err.println("Navigation error:");
                                e.printStackTrace();
                                ErrorHandler.handle(e);
                            }
                        });
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Login error:");
                    throwable.printStackTrace();
                    Platform.runLater(() -> ErrorHandler.handle(throwable));
                    return null;
                });
    }
}
    @FXML
    private void handleRegisterLink() {
        sceneManager.switchToRegister();
    }

    private boolean validateInput(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    private void handleLoginError(Throwable throwable) {
        String message = throwable.getMessage();
        if (message.contains("401")) {
            errorLabel.setText("Invalid username or password");
        } else if (message.contains("429")) {
            errorLabel.setText("Too many attempts. Please try again later");
        } else if (message.contains("Connection")) {
            errorLabel.setText("Cannot connect to server. Please try again");
        } else {
            errorLabel.setText("An unexpected error occurred");
        }
        errorLabel.setVisible(true);
    }

    @Override
    public void updateConnectionStatus(boolean isConnected) {
        connectionStatus.setText(isConnected ? "Connected" : "Offline");
        connectionStatus.getStyleClass().setAll("connection-status",
            isConnected ? "connected" : "disconnected");
    }
}
