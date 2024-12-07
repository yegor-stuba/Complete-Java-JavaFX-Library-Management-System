package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;


public class RegisterController {
    private final UserService userService;
    private final SceneManager sceneManager;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    public RegisterController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
private void handleRegister() {
    if (!validateInput()) {
        return;
    }

    UserDTO userDTO = new UserDTO();
    userDTO.setUsername(usernameField.getText().trim());
    userDTO.setEmail(emailField.getText().trim());
    userDTO.setPassword(passwordField.getText());
    userDTO.setRole(UserRole.USER);

    userService.register(userDTO)
        .thenAccept(response -> {
            Platform.runLater(() -> {
                AlertUtil.showInfo("Success", "Registration successful!");
                sceneManager.switchToLogin();
            });
        })
        .exceptionally(throwable -> {
            Platform.runLater(() -> {
                String errorMessage = throwable.getCause().getMessage();
                if (errorMessage.contains("username already exists")) {
                    AlertUtil.showError("Registration Failed", "Username already taken");
                } else if (errorMessage.contains("email already exists")) {
                    AlertUtil.showError("Registration Failed", "Email already registered");
                } else {
                    AlertUtil.showError("Registration Failed", "An error occurred: " + errorMessage);
                }
            });
            return null;
        });
}

    private boolean validateInput() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtil.showWarning("Validation Error", "All fields are required");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid email address");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.showWarning("Validation Error", "Passwords do not match");
            return false;
        }

        if (password.length() < 6) {
            AlertUtil.showWarning("Validation Error", "Password must be at least 6 characters long");
            return false;
        }

        return true;
    }

    @FXML
    private void handleBackToLogin() {
        sceneManager.switchToLogin();
    }
}