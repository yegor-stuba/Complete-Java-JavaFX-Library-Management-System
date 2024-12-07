package com.studyshare.client.util;

import com.studyshare.client.config.ClientConfig;
import com.studyshare.common.enums.UserRole;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneManager {
    private final Stage primaryStage;
    @Setter
    private ControllerFactory controllerFactory;

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
        loadScene("/fxml/user-profile.fxml", "User Profile");
    }


    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML file not found: " + fxmlPath);
            }
            loader.setControllerFactory(controllerFactory::createController);
            Parent root = loader.load();
            Scene scene = new Scene(root, ClientConfig.WINDOW_WIDTH, ClientConfig.WINDOW_HEIGHT);

            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }

            primaryStage.setTitle(ClientConfig.APP_TITLE + " - " + title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            log.error("Failed to load scene: {}", e.getMessage(), e);
            AlertUtil.showError("Error", "Failed to load scene: " + e.getMessage());
        }
    }

    public void navigateBasedOnRole(UserRole role) {
        if (role == UserRole.ADMIN) {
            loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
        } else {
            loadScene("/fxml/user-profile.fxml", "User Profile");
        }
    }

}