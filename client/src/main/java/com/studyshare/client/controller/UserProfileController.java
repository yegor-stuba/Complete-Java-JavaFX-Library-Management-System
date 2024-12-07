package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
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

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserProfileController extends BaseController {

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
        setupTableColumns();
        loadUserProfile();
        loadBorrowedBooks();
        borrowedBooksTable.setItems(borrowedBooks);
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
        transactionService.getUserTransactions(getCurrentUserId())
                .thenAccept(transactions -> {
                    List<BookDTO> books = transactions.stream()
                            .map(TransactionDTO::getBook)
                            .collect(Collectors.toList());
                    Platform.runLater(() -> borrowedBooks.setAll(books));
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to load borrowed books");
                    return null;
                });
    }

    private void loadUserProfile() {
        userService.getCurrentUser()
                .thenAccept(user -> {
                    usernameLabel.setText("Username: " + user.getUsername());
                    emailLabel.setText("Email: " + user.getEmail());
                    roleLabel.setText("Role: " + user.getRole());
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to load user profile");
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