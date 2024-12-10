package com.studyshare.client.controller;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.exception.AuthenticationException;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController extends BaseController {
    private final AuthenticationService authService;
    private final UserService userService;
    private final SceneManager sceneManager;
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginController.class);

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
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();

    log.debug("Attempting login with username: '{}', password length: {}",
            username, password.length());


    if (username.isEmpty() || password.isEmpty()) {
        AlertUtil.showError("Login Error", "Username and password are required");
        return;
    }


    authService.login(username, password)
            .thenAccept(response -> Platform.runLater(() -> {
                log.debug("Login response received: {}", response);

                if (response == null) {
                    log.error("Received null response from authentication service");
                    AlertUtil.showError("Login Error", "Authentication failed - no response");
                    return;
                }

                UserRole role = response.getRole();
                log.debug("User authenticated with role: {}", role);

                switch (role) {
                    case ADMIN -> {
                        log.info("Admin user logged in: {}", response.getUsername());
                        sceneManager.switchToAdminDashboard();
                    }
                    case USER -> {
                        log.info("Regular user logged in: {}", response.getUsername());
                        sceneManager.switchToUserProfile();
                    }
                    default -> {
                        log.error("Unknown user role: {}", role);
                        AlertUtil.showError("Login Error", "Invalid user role");
                    }
                }
            }))
            .exceptionally(throwable -> {
                log.error("Login failed: {}", throwable.getMessage());
                Platform.runLater(() -> AlertUtil.showError("Login Failed", throwable.getMessage()));
                return null;
            });
}

private String extractErrorMessage(Throwable throwable) {
    Throwable cause = throwable.getCause();
    if (cause instanceof AuthenticationException) {
        return "Invalid username or password";
    } else if (cause instanceof RestClientException) {
        RestClientException rce = (RestClientException) cause;
        return rce.getErrorBody();
    }
    return "Connection error. Please try again.";
}

private boolean validateInput() {
    if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
        AlertUtil.showWarning("Validation Error", "Username and password are required");
        return false;
    }
    return true;
}

private void handleLoginError(String message) {
    Platform.runLater(() -> {
        AlertUtil.showError("Login Failed", message);
        passwordField.clear();
        usernameField.requestFocus();
    });
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
