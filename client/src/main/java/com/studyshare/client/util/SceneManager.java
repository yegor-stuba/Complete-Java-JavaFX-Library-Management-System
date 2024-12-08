package com.studyshare.client.util;

import com.studyshare.client.config.ClientConfig;
import com.studyshare.client.controller.BaseController;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public class SceneManager {
    private final Stage primaryStage;
    @Setter
    private ControllerFactory controllerFactory;
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);


    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchToLogin() {
        loadScene("/fxml/login.fxml", "Login");
    }

    public void switchToRegister() {
        loadScene("/fxml/register.fxml", "Register");
    }

    public void switchToBookManagement() {
        loadScene("/fxml/book-management.fxml", "Book Management");
    }

  public void switchToUserProfile() {
    try {
        loadScene("/fxml/user-profile.fxml", "User Profile");
    } catch (Exception e) {
        log.error("Failed to load user profile: {}", e.getMessage());
        AlertUtil.showError("Error", "Failed to load user profile");
    }
}
private void loadScene(String fxmlPath, String title) {
    try {
        URL resource = SceneManager.class.getResource(fxmlPath);
        if (resource == null) {
            throw new RuntimeException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory::createController);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    } catch (Exception e) {
        log.error("Failed to load scene: {}", e.getMessage());
        throw new RuntimeException("Failed to load scene: " + e.getMessage());
    }
}

    public void navigateBasedOnRole(UserRole role) {
        log.debug("Navigating based on role: {}", role);
        try {
            if (role == UserRole.ADMIN) {
                loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
            } else {
                loadScene("/fxml/user-profile.fxml", "User Profile");
            }
        } catch (Exception e) {
            log.error("Navigation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Navigation failed: " + e.getMessage());
        }
    }

    public void updateConnectionStatus(boolean isConnected) {
        Scene currentScene = primaryStage.getScene();
        if (currentScene != null) {
            Object controller = currentScene.getUserData();
            if (controller instanceof BaseController) {
                ((BaseController) controller).updateConnectionStatus(isConnected);
            }
        }
    }

 public void switchToAdminDashboard() {
    try {
        loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
    } catch (Exception e) {
        handleSceneLoadError(e, "Failed to load admin dashboard");
    }
}

private void handleSceneLoadError(Exception e, String message) {
    log.error(message + ": {}", e.getMessage());
    Platform.runLater(() -> AlertUtil.showError("Navigation Error", message));
}
}