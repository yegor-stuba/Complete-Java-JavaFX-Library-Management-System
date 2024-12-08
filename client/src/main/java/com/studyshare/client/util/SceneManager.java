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

    public void switchToAdminDashboard() {
        try {
            loadScene("/fxml/admin-dashboard.fxml", "Admin Dashboard");
        } catch (Exception e) {
            handleSceneLoadError(e, "Failed to load admin dashboard");
        }
    }

    private Scene loadScene(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory::createController);

            Parent root = loader.load();
            if (root == null) {
                throw new IOException("Failed to load FXML root element");
            }

            return new Scene(root);
        } catch (IOException e) {
            log.error("Failed to load FXML file: {} - {}", fxmlPath, e.getMessage(), e);
            Platform.runLater(() -> AlertUtil.showError("Scene Loading Error",
                String.format("Failed to load interface: %s\nDetails: %s",
                fxmlPath, e.getMessage())));
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            Scene scene = loadScene(fxmlPath);
            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle(title);
                primaryStage.show();
            });
        } catch (Exception e) {
            log.error("Scene loading failed: {} - {}", fxmlPath, e.getMessage(), e);
            throw new RuntimeException("Scene loading failed", e);
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

    private void handleSceneLoadError(Exception e, String message) {
        log.error(message + ": {}", e.getMessage());
        Platform.runLater(() -> AlertUtil.showError("Navigation Error", message));
    }
    // Add these methods
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
}