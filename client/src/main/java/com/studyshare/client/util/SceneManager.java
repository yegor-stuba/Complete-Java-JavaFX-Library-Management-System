package com.studyshare.client.util;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
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
        try {
            loadScene("/fxml/login.fxml", "Login");
        } catch (Exception e) {
            handleSceneLoadError(e, "Failed to load login screen");
        }
    }

    public void switchToRegister() {
        try {
            loadScene("/fxml/register.fxml", "Register");
        } catch (Exception e) {
            handleSceneLoadError(e, "Failed to load registration screen");
        }
    }


public void switchToUserProfile() {
    try {
        loadScene("/fxml/user-profile.fxml", "User Profile");
    } catch (Exception e) {
        handleSceneLoadError(e, "Failed to load user profile");
    }
}
private void handleSceneLoadError(Exception e, String message) {
    log.error("Scene error details: ", e);
    Platform.runLater(() -> AlertUtil.showError("Navigation Error",
        message + "\nType: " + e.getClass().getSimpleName() +
        "\nCause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown")));
}


    public void switchToAdminDashboard() {
        try {
            loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
        } catch (Exception e) {
            handleSceneLoadError(e, "Failed to load admin dashboard");
        }
    }




    public void switchToTransactions() {
        loadScene("/fxml/transactions.fxml", "Transactions");
    }



private void loadScene(String fxmlPath, String title) {
    try {
        log.debug("Attempting to load FXML: {}", fxmlPath);

        // Normalize the path
        String normalizedPath = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
        log.debug("Normalized path: {}", normalizedPath);

        // Try multiple resource loading strategies
        URL resource = getClass().getClassLoader().getResource(normalizedPath);

        if (resource == null) {
            resource = getClass().getClassLoader().getResource("fxml/" + normalizedPath);
        }

        if (resource == null) {
            resource = Thread.currentThread().getContextClassLoader().getResource(normalizedPath);
        }

        log.debug("Final resource URL: {}", resource);
        if (resource == null) {
            throw new IOException("Resource not found: " + normalizedPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory::createController);
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Load CSS with similar fallback strategy
        URL cssUrl = getClass().getClassLoader().getResource("css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Platform.runLater(() -> {
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        });
    } catch (Exception e) {
        log.error("Failed to load scene: {} - {}", fxmlPath, e.getMessage(), e);
        throw new RuntimeException("Scene loading failed: " + e.getMessage(), e);
    }
}
}