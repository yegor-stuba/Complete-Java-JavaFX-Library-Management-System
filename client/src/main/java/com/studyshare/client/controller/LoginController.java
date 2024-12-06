package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;



public class LoginController extends BaseController {

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

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showWarning("Login Error", "Please enter both username and password");
            return;
        }

        handleAsync(userService.login(username, password))
                .thenAccept(success -> {
                            if (Boolean.TRUE.equals(success)) {
                                sceneManager.switchToBookManagement();
                            } else {
                                AlertUtil.showError("Login Failed", "Invalid username or password");
                            }
                        });

        userService.login(username, password)
                .thenAccept(user -> {
                    if (user != null) {
                        Platform.runLater(() -> sceneManager.navigateBasedOnRole(user.getRole()));
                    } else {
                        AlertUtil.showError("Login Failed", "Invalid username or password");
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Could not connect to server"));
                    return null;
                });
    }

}