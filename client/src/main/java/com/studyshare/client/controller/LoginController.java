package com.studyshare.client.controller;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
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

    public LoginController(UserService userService, SceneManager sceneManager, AuthenticationService authService) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.authService = authService;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegisterLink() {
        sceneManager.switchToRegister();
    }


    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showWarning("Login Error", "Please enter both username and password");
            return;
        }

        authService.login(username, password)
                .thenAccept(response -> {
                    if (response != null) {
                        Platform.runLater(() -> sceneManager.navigateBasedOnRole(response.getRole()));
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> handleLoginError(throwable));
                    return null;
                });
    }

    @FXML
private Label errorLabel;

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
@FXML
private Label connectionStatus;

public void updateConnectionStatus(boolean isConnected) {
    connectionStatus.setText(isConnected ? "Connected" : "Offline");
    connectionStatus.getStyleClass().setAll("connection-status",
        isConnected ? "connected" : "disconnected");
}

}
