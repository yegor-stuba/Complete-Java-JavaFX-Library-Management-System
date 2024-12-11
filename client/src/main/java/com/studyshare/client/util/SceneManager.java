package com.studyshare.client.util;

import com.studyshare.client.config.ClientConfig;
import com.studyshare.client.controller.BaseController;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private final Stage primaryStage;
    private ControllerFactory controllerFactory;
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setControllerFactory(ControllerFactory factory) {
        this.controllerFactory = factory;
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

    public void switchToBookManagement() {
        try {
            loadScene("/fxml/book-management.fxml", "Book Management");
        } catch (Exception e) {
            handleSceneLoadError(e, "Failed to load book management screen");
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

    public void navigateBasedOnRole(UserRole role) {
        log.debug("Navigating based on role: {}", role);
        try {
            if (role == UserRole.ADMIN) {
                loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
            } else {
                loadScene("/fxml/user-profile.fxml", "User Profile");
            }
        } catch (Exception e) {
            log.error("Navigation failed: {}", e.getMessage());
            AlertUtil.showError("Navigation Error", "Failed to navigate: " + e.getMessage());
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


    public void switchToTransactions() {
        loadScene("/fxml/transactions.fxml", "Transactions");
    }

    public void showTransactionDetails(TransactionDTO transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction-details-dialog.fxml"));
            loader.setControllerFactory(controllerFactory::createController);
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Transaction Details");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            handleSceneLoadError(e, "Failed to load transaction details");
        }
    }

    public void showBookLendingDialog(BookDTO book) {
        loadDialog("/fxml/book-lending.fxml", "Lend Book", book);
    }

    public void showBookReturnDialog(BookDTO book) {
        loadDialog("/fxml/book-return.fxml", "Return Book", book);
    }

    private void loadDialog(String fxmlPath, String title, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory::createController);
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.setScene(new Scene(root));

            BaseController controller = loader.getController();
            if (controller != null) {
                controller.initData(data);
            }

            dialogStage.showAndWait();
        } catch (IOException e) {
            handleSceneLoadError(e, "Failed to load dialog: " + title);
        }
    }

private void loadScene(String fxmlPath, String title) {
    try {
        // Try multiple resource loading strategies
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            resource = getClass().getResource("/fxml" + fxmlPath);
        }
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("fxml" + fxmlPath);
        }

        log.debug("Attempting to load FXML from: {}", resource);
        if (resource == null) {
            throw new IOException("Cannot find resource: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory::createController);
        Scene scene = new Scene(loader.load());

        // Add CSS
        URL cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Platform.runLater(() -> {
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        });
    } catch (Exception e) {
        log.error("Failed to load scene: {} - {}", fxmlPath, e.getMessage());
        throw new RuntimeException("Scene loading failed: " + e.getMessage(), e);
    }
}
}