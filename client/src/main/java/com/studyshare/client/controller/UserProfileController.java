package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final UserService userService;
    private final TransactionService transactionService;
    private final SceneManager sceneManager;
    private final ObservableList<BookDTO> borrowedBooks = FXCollections.observableArrayList();

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private TableView<BookDTO> borrowedBooksTable;

    @FXML
    private TableColumn<BookDTO, String> titleColumn;

    @FXML
    private TableColumn<BookDTO, String> authorColumn;

    @FXML
    private TableColumn<BookDTO, String> dueDateColumn;

    public UserProfileController(UserService userService, TransactionService transactionService, SceneManager sceneManager) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;
    }

    @FXML
private void initialize() {
    try {
        setupTableColumns();
        loadUserProfile();
        loadBorrowedBooks();
        borrowedBooksTable.setItems(borrowedBooks);
    } catch (Exception e) {
        log.error("Failed to initialize user profile: {}", e.getMessage());
        Platform.runLater(() -> AlertUtil.showError("Initialization Error",
            "Failed to setup user profile view"));
    }
}

private void loadUserProfile() {
    handleAsync(userService.getCurrentUser())
        .thenAccept(user -> Platform.runLater(() -> {
            if (user != null) {
                usernameLabel.setText(user.getUsername());
                emailLabel.setText(user.getEmail());
                roleLabel.setText(user.getRole().toString());
            } else {
                log.error("Failed to load user profile: user data is null");
                AlertUtil.showError("Profile Error", "Could not load user profile data");
            }
        }))
        .exceptionally(throwable -> {
            log.error("Error loading user profile: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Profile Error",
                "Failed to load profile: " + throwable.getMessage()));
            return null;
        });
}
    private void setupTableColumns() {
        titleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAuthor()));
        dueDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty("Due Date")); // Placeholder until we implement due dates
    }



private void loadBorrowedBooks() {
    handleAsync(transactionService.getUserTransactions(getCurrentUserId()))
        .thenAccept(transactions -> {
            if (transactions != null) {
                List<BookDTO> books = transactions.stream()
                    .filter(t -> t != null && t.getBook() != null)
                    .map(TransactionDTO::getBook)
                    .collect(Collectors.toList());
                Platform.runLater(() -> borrowedBooks.setAll(books));
            } else {
                log.error("Failed to load borrowed books: transaction data is null");
                Platform.runLater(() -> AlertUtil.showWarning("Books",
                    "No borrowed books found"));
            }
        })
        .exceptionally(throwable -> {
            log.error("Error loading borrowed books: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Books Error",
                "Failed to load borrowed books: " + throwable.getMessage()));
            return null;
        });
}

    @FXML
    private void handleLogout() {
        userService.logout()
                .thenRun(() -> {
                    sceneManager.switchToLogin();
                    AlertUtil.showInfo("Success", "Logged out successfully");
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to logout");
                    return null;
                });
    }

    private Long getCurrentUserId() {
        return userService.getCurrentUser()
                .thenApply(UserDTO::getUserId)
                .join();
    }
}