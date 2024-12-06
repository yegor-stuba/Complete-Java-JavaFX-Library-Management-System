package com.studyshare.client.util;

import com.studyshare.client.config.ClientConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SceneManager {
    private final Stage primaryStage;
    private ControllerFactory controllerFactory;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setControllerFactory(ControllerFactory factory) {
        this.controllerFactory = factory;
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
            loader.setControllerFactory(controllerFactory::createController);
            Parent root = loader.load();
            Scene scene = new Scene(root, ClientConfig.WINDOW_WIDTH, ClientConfig.WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            primaryStage.setTitle(ClientConfig.APP_TITLE + " - " + title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load scene");
        }
    }
}