package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
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
        userDTO.setUsername(usernameField.getText());
        userDTO.setEmail(emailField.getText());
        userDTO.setPassword(passwordField.getText());
        userDTO.setRole(UserRole.USER);

        userService.register(userDTO)
            .thenAccept(user -> {
                AlertUtil.showInfo("Success", "Registration successful!");
                sceneManager.switchToLogin();
            })
            .exceptionally(throwable -> {
                AlertUtil.showError("Error", "Registration failed: " + throwable.getMessage());
                return null;
            });
    }

    @FXML
    private void handleBackToLogin() {
        sceneManager.switchToLogin();
    }

    private boolean validateInput() {
        if (usernameField.getText().isEmpty() || emailField.getText().isEmpty()
            || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "All fields are required");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.showWarning("Validation Error", "Passwords do not match");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.showWarning("Validation Error", "Invalid email format");
            return false;
        }

        return true;
    }
}