package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {

    private final UserService userService;
    private final SceneManager sceneManager;

    public LoginController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
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

    System.out.println("Attempting login with username: " + username);

    userService.login(username, password)
        .thenAccept(success -> {
            System.out.println("Login result: " + success);
            if (Boolean.TRUE.equals(success)) {
                Platform.runLater(() -> {
                    System.out.println("Switching to book management view");
                    sceneManager.switchToBookManagement();
                });
            } else {
                Platform.runLater(() -> AlertUtil.showError("Login Failed", "Invalid username or password"));
            }
        })
        .exceptionally(throwable -> {
            System.out.println("Login error: " + throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Error", "Could not connect to server"));
            return null;
        });
}
}