package com.studyshare.client.controller;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.UserService;
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
    if (!validateInput()) {
        return;
    }

    String username = usernameField.getText();
    String password = passwordField.getText();
    log.debug("Processing login for user: {}", username);

    authService.login(username, password)
        .thenAccept(response -> Platform.runLater(() -> {
            if (response != null && response.getRole() != null) {
                log.debug("Login successful, role: {}", response.getRole());
                switch (response.getRole()) {
                    case ADMIN -> sceneManager.switchToAdminDashboard();
                    case USER -> sceneManager.switchToUserProfile();
                    default -> handleLoginError("Invalid user role");
                }
            } else {
                handleLoginError("Invalid server response");
            }
        }))
        .exceptionally(throwable -> {
            log.error("Login failed: {}", throwable.getMessage());
            handleLoginError(throwable.getMessage());
            return null;
        });
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
