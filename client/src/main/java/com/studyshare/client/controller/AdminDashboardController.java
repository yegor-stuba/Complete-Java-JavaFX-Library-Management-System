package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class AdminDashboardController {
    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final SceneManager sceneManager;
    private final ObservableList<UserDTO> users = FXCollections.observableArrayList();
    private final ObservableList<BookDTO> books = FXCollections.observableArrayList();

    public AdminDashboardController(
            UserService userService,
            BookService bookService,
            TransactionService transactionService,
            SceneManager sceneManager) {
        this.userService = userService;
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private TableView<UserDTO> usersTable;
    @FXML
    private TableView<BookDTO> booksTable;
    @FXML
    private TextField userSearchField;
    @FXML
    private TextField bookSearchField;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label activeLoansLabel;
    @FXML
    private Label totalTransactionsLabel;
    @FXML
    private TableColumn<UserDTO, String> userIdColumn;
    @FXML
    private TableColumn<UserDTO, String> usernameColumn;
    @FXML
    private TableColumn<UserDTO, String> emailColumn;
    @FXML
    private TableColumn<UserDTO, String> roleColumn;
    @FXML
    private TableColumn<UserDTO, Void> actionsColumn;


    private void loadInitialData() {
        loadUsers();
        loadBooks();
        updateStatistics();
    }

    @FXML
    private void initialize() {
        setupUserTable();
        setupBookTable();
        loadInitialData();
        setupSearchListeners();
    }

    private void setupUserTable() {
        userIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getUserId())));
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(e -> handleEditUser(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> handleDeleteUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupBookTable() {
        // Similar setup for book table columns
    }

    @FXML
    private void refreshData() {
        loadUsers();
        loadBooks();
        updateStatistics();
    }

    private void loadUsers() {
        userService.getAllUsers()
                .thenAccept(userList -> Platform.runLater(() -> {
                    users.clear();
                    users.addAll(userList);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load users"));
                    return null;
                });
    }

    private void loadBooks() {
        bookService.getAllBooks()
                .thenAccept(bookList -> Platform.runLater(() -> {
                    books.clear();
                    books.addAll(bookList);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load books"));
                    return null;
                });
    }

    private void updateStatistics() {
        // Update statistics labels with current system data
        userService.getUserCount().thenAccept(count ->
                Platform.runLater(() -> totalUsersLabel.setText("Total Users: " + count)));

        bookService.getBookCount().thenAccept(count ->
                Platform.runLater(() -> totalBooksLabel.setText("Total Books: " + count)));

        transactionService.getActiveLoansCount().thenAccept(count ->
                Platform.runLater(() -> activeLoansLabel.setText("Active Loans: " + count)));
    }

    @FXML
    private void handleLogout() {
        userService.logout()
                .thenRun(() -> Platform.runLater(() -> sceneManager.switchToLogin()))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Logout failed"));
                    return null;
                });
    }

    // User management methods
    @FXML
    private void handleAddUser() {
        UserDTO newUser = showUserDialog(null);
        if (newUser != null) {
            userService.createUser(newUser)
                    .thenAccept(user -> Platform.runLater(() -> {
                        users.add(user);
                        AlertUtil.showInfo("Success", "User created successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error", "Failed to create user"));
                        return null;
                    });
        }
    }

    private void handleEditUser(UserDTO user) {
        UserDTO updatedUser = showUserDialog(user);
        if (updatedUser != null) {
            userService.updateUser(user.getUserId(), updatedUser)
                    .thenAccept(updated -> Platform.runLater(() -> {
                        int index = users.indexOf(user);
                        users.set(index, updated);
                        AlertUtil.showInfo("Success", "User updated successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error", "Failed to update user"));
                        return null;
                    });
        }
    }

    private void handleDeleteUser(UserDTO user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user.getUserId())
                        .thenRun(() -> Platform.runLater(() -> {
                            users.remove(user);
                            AlertUtil.showInfo("Success", "User deleted successfully");
                        }))
                        .exceptionally(throwable -> {
                            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to delete user"));
                            return null;
                        });
            }
        });
    }

    // Book management methods
    @FXML
    private void handleAddBook() {
        BookDTO newBook = showBookDialog(null);
        if (newBook != null) {
            bookService.addBook(newBook)
                    .thenAccept(book -> Platform.runLater(() -> {
                        books.add(book);
                        AlertUtil.showInfo("Success", "Book added successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error", "Failed to add book"));
                        return null;
                    });
        }
    }

    private void handleEditBook(BookDTO book) {
        BookDTO updatedBook = showBookDialog(book);
        if (updatedBook != null) {
            bookService.updateBook(book.getBookId(), updatedBook)
                    .thenAccept(updated -> Platform.runLater(() -> {
                        int index = books.indexOf(book);
                        books.set(index, updated);
                        AlertUtil.showInfo("Success", "Book updated successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error", "Failed to update book"));
                        return null;
                    });
        }
    }

    private void handleDeleteBook(BookDTO book) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Book");
        confirmation.setContentText("Are you sure you want to delete book: " + book.getTitle() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookService.deleteBook(book.getBookId())
                        .thenRun(() -> Platform.runLater(() -> {
                            books.remove(book);
                            AlertUtil.showInfo("Success", "Book deleted successfully");
                        }))
                        .exceptionally(throwable -> {
                            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to delete book"));
                            return null;
                        });
            }
        });
    }


    // Search functionality
    private void setupSearchListeners() {
        userSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadUsers();
            } else {
                userService.searchUsers(newValue)
                        .thenAccept(results -> Platform.runLater(() -> {
                            users.clear();
                            users.addAll(results);
                        }));
            }
        });

        bookSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadBooks();
            } else {
                bookService.searchBooks(newValue)
                        .thenAccept(results -> Platform.runLater(() -> {
                            books.clear();
                            books.addAll(results);
                        }));
            }
        });
    }

    private UserDTO showUserDialog(UserDTO user) {
        Dialog<UserDTO> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add New User" : "Edit User");
        dialog.setHeaderText(user == null ? "Enter user details" : "Edit user details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField usernameField = new TextField(user != null ? user.getUsername() : "");
        TextField emailField = new TextField(user != null ? user.getEmail() : "");
        PasswordField passwordField = new PasswordField();
        ComboBox<UserRole> roleComboBox = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));
        if (user != null) roleComboBox.setValue(user.getRole());

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (usernameField.getText().isEmpty() || emailField.getText().isEmpty()
                        || (user == null && passwordField.getText().isEmpty())
                        || roleComboBox.getValue() == null) {
                    AlertUtil.showWarning("Validation Error", "All fields are required");
                    return null;
                }
                UserDTO result = new UserDTO();
                if (user != null) result.setUserId(user.getUserId());
                result.setUsername(usernameField.getText());
                result.setEmail(emailField.getText());
                if (!passwordField.getText().isEmpty()) {
                    result.setPassword(passwordField.getText());
                }
                result.setRole(roleComboBox.getValue());
                return result;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }


    private BookDTO showBookDialog(BookDTO book) {
        Dialog<BookDTO> dialog = new Dialog<>();
        dialog.setTitle(book == null ? "Add New Book" : "Edit Book");
        dialog.setHeaderText(book == null ? "Enter book details" : "Edit book details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField titleField = new TextField(book != null ? book.getTitle() : "");
        TextField authorField = new TextField(book != null ? book.getAuthor() : "");
        TextField isbnField = new TextField(book != null ? book.getIsbn() : "");
        Spinner<Integer> copiesSpinner = new Spinner<>(0, 100, book != null ? book.getAvailableCopies() : 1);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Copies:"), 0, 3);
        grid.add(copiesSpinner, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty()
                        || isbnField.getText().isEmpty()) {
                    AlertUtil.showWarning("Validation Error", "All fields are required");
                    return null;
                }
                BookDTO result = new BookDTO();
                if (book != null) result.setBookId(book.getBookId());
                result.setTitle(titleField.getText());
                result.setAuthor(authorField.getText());
                result.setIsbn(isbnField.getText());
                result.setAvailableCopies(copiesSpinner.getValue());
                return result;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private void handleBookSearch() {
        String searchQuery = bookSearchField.getText();
        if (!searchQuery.isEmpty()) {
            bookService.searchBooks(searchQuery)
                    .thenAccept(results -> Platform.runLater(() -> {
                        books.clear();
                        books.addAll(results);
                    }));
        }
    }
    @FXML
    private void handleUserSearch() {
        String searchQuery = userSearchField.getText();
        if (!searchQuery.isEmpty()) {
            userService.searchUsers(searchQuery)
                    .thenAccept(results -> Platform.runLater(() -> {
                        users.clear();
                        users.addAll(results);
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error", "Failed to search users"));
                        return null;
                    });
        } else {
            loadUsers();
        }
    }


}

