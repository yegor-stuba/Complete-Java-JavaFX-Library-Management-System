package com.studyshare.client.controller;

import com.studyshare.client.service.UserService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.exception.AuthorizationException;
import com.studyshare.client.service.exception.ConflictException;
import com.studyshare.client.service.exception.ResourceNotFoundException;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.client.util.TransactionUtil;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import jakarta.validation.ValidationException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class AdminDashboardController {
    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final SceneManager sceneManager;
    private final ObservableList<UserDTO> users = FXCollections.observableArrayList();
    private static final Logger log = LoggerFactory.getLogger(AdminDashboardController.class);
    private final ObservableList<BookDTO> books = FXCollections.observableArrayList();

   // Required FXML fields
@FXML private TableView<BookDTO> booksTable;
@FXML private TableView<UserDTO> usersTable;
@FXML private TextField userSearchField;
@FXML private TextField bookSearchField;

// Book table columns
@FXML private TableColumn<BookDTO, String> bookIdColumn;
@FXML private TableColumn<BookDTO, String> titleColumn;
@FXML private TableColumn<BookDTO, String> authorColumn;
@FXML private TableColumn<BookDTO, String> isbnColumn;
@FXML private TableColumn<BookDTO, String> copiesColumn;
@FXML private TableColumn<BookDTO, Void> bookActionsColumn;
    @FXML private TableColumn<BookDTO, String> borrowedTitleColumn;
    @FXML private TableColumn<BookDTO, String> borrowedAuthorColumn;
    @FXML private TableColumn<BookDTO, String> borrowerColumn;
    @FXML private TableView<BookDTO> borrowedBooksTable;


// User table columns
@FXML private TableColumn<UserDTO, String> userIdColumn;
@FXML private TableColumn<UserDTO, String> usernameColumn;
@FXML private TableColumn<UserDTO, String> emailColumn;
@FXML private TableColumn<UserDTO, String> roleColumn;
@FXML private TableColumn<UserDTO, Void> actionsColumn;
    private final Timeline statisticsUpdateTimeline;

    public AdminDashboardController(
            UserService userService,
            BookService bookService,
            TransactionService transactionService,
            SceneManager sceneManager) {
        this.userService = userService;
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;
        this.statisticsUpdateTimeline = new Timeline(
        );
        statisticsUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    // transaction columns
    @FXML private TableColumn<TransactionDTO, String> timestampColumn;
    @FXML private TableColumn<TransactionDTO, String> actionColumn;
    @FXML private TableColumn<TransactionDTO, String> userColumn;
    @FXML private TableColumn<TransactionDTO, String> bookColumn;
    @FXML private TableView<TransactionDTO> transactionsTable;


    @FXML
    public void initialize() {
        log.debug("Initializing AdminDashboardController");
        Timer sessionCheckTimer = new Timer(true);
        sessionCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkSessionStatus();
            }
        }, 0, 30000);
        try {
            Thread.sleep(100);
            setupBookColumns();
            loadBooks();

            handleBookSearch();
            setupTables();
            setupUserTable();
            setupBookTable();
            loadInitialData();
            setupSearchListeners();
            setupUserManagement();
            setupBookManagement();
            loadData();
            setupTransactionTable();
            startRealTimeUpdates();
            setupBorrowedBooksTable();
        } catch (Exception e) {
            log.error("Failed to initialize dashboard", e);
            AlertUtil.showError("Error", "Failed to initialize dashboard");
        }
    }

    private void setupBookColumns() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        setupActionColumn();

        booksTable.setItems(books);
    }

    private void setupActionColumn() {
        bookActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setOnAction(event -> handleEditBook(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> handleDeleteBook(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    var hbox = new javafx.scene.layout.HBox(5);
                    hbox.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });
    }

    @FXML
    private void handleBookSearch() {
        String searchQuery = bookSearchField.getText();
        if (!searchQuery.isEmpty()) {
            bookService.searchBooks(searchQuery)
                    .thenAccept(results -> Platform.runLater(() -> {
                        books.clear();
                        books.addAll(results);
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Search Error",
                                "Failed to search books: " + throwable.getMessage()));
                        return null;
                    });
        } else {
            loadBooks();
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

    private CompletableFuture<?> loadBooks() {
        return bookService.getAllBooks()
                .thenAccept(bookList -> {
                    if (bookList != null) {
                        Platform.runLater(() -> {
                            books.clear();
                            books.addAll(bookList);
                            log.debug("Successfully loaded {} books", bookList.size());
                        });
                    } else {
                        throw new ResourceNotFoundException("No books available");
                    }
                })
                .exceptionally(throwable -> {
                    String errorMessage;
                    Throwable cause = throwable.getCause();
                    if (cause instanceof AuthorizationException) {
                        errorMessage = "You don't have permission to view books";
                    } else if (cause instanceof AuthenticationException) {
                        errorMessage = "Session expired - please log in again";
                    } else if (cause instanceof ResourceNotFoundException) {
                        errorMessage = "No books found in the system";
                    } else if (cause instanceof RestClientException) {
                        errorMessage = "Server error: " + cause.getMessage();
                    } else {
                        errorMessage = "Failed to load books: " + throwable.getMessage();
                    }
                    log.error("Book loading error: {}", errorMessage);
                    Platform.runLater(() -> AlertUtil.showError("Loading Error", errorMessage));
                    return null;
                });
    }

    private void setupTransactionTable() {
        log.debug("Setting up transaction table");

        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTransactionDate().toString()));
        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().toString()));
        userColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));
        bookColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBookTitle()));

        // Load initial data
        refreshTransactions();


        Timeline refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(15), event -> refreshTransactions())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

    }

    private void refreshTransactions() {
        transactionService.getAllTransactions()
                .thenAccept(transactions -> Platform.runLater(() -> {
                    transactionsTable.setItems(FXCollections.observableArrayList(transactions));
                    log.debug("Loaded {} transactions", transactions.size());
                }))
                .exceptionally(throwable -> {
                    log.error("Failed to load transactions", throwable);
                    Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load transactions"));
                    return null;
                });
    }

    private void startRealTimeUpdates() {
        statisticsUpdateTimeline.play();
    }


   private void loadInitialData() {    if (!userService.isAdmin()) {
        Platform.runLater(() -> {
            AlertUtil.showError("Access Denied", "Admin privileges required");
            sceneManager.switchToLogin();
        });
        return;
    }

    CompletableFuture.allOf(
        loadUsers(),
        loadBooks()
    ).exceptionally(throwable -> {
        handleLoadError(throwable);
        return null;
    });
}

private void handleLoadError(Throwable throwable) {
    String errorMessage;
    if (throwable.getCause() instanceof AuthenticationException) {
        errorMessage = "Session expired - please log in again";
        Platform.runLater(sceneManager::switchToLogin);
    } else if (throwable.getCause() instanceof AuthorizationException) {
        errorMessage = "You don't have permission to view this data";
    } else {
        errorMessage = "Failed to load data: " + throwable.getMessage();
    }
    Platform.runLater(() -> AlertUtil.showError("Error", errorMessage));
}

private void checkSessionStatus() {
    userService.getCurrentUser()
        .thenAccept(user -> {
            if (!user.getRole().equals(UserRole.ADMIN)) {
                Platform.runLater(() -> {
                    AlertUtil.showWarning("Access Denied", "Admin privileges required");
                    sceneManager.switchToLogin();
                });
            }
        })
        .exceptionally(throwable -> {
            Platform.runLater(() -> {
                AlertUtil.showError("Session Expired", "Please log in again");
                sceneManager.switchToLogin();
            });
            return null;
        });
}





  private CompletableFuture<Void> loadData() {
      return userService.getCurrentUser()
          .thenCompose(user -> CompletableFuture.allOf(
              loadUsers(),
              loadBooks()
          ))
          .exceptionally(throwable -> {
              Platform.runLater(() -> {
                  if (throwable instanceof AuthenticationException) {
                      sceneManager.switchToLogin();
                  } else {
                      AlertUtil.showError("Error", throwable.getMessage());
                  }
              });
              return null;
          });
  }



    private void setupTables() {
        // Setup User Table
        userIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getUserId())));
        usernameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));
        roleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole().toString()));

        // Setup Book Table
        bookIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getBookId())));
        titleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getIsbn()));
        copiesColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAvailableCopies())));

        // Bind tables to observable lists
        usersTable.setItems(users);
        booksTable.setItems(books);
    }

    private void setupBookTable() {
        bookIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBookId())));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));

        loadBooks();
    }

    // Add these methods
    private void setupUserManagement() {
        // User table columns
        userIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getUserId())));
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));

        // Add action buttons
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


    @FXML
    private void refreshData() {
        loadUsers();
        loadBooks();
    }

private CompletableFuture<Void> loadUsers() {
    return userService.getAllUsers()
        .thenAccept(userList -> Platform.runLater(() -> {
            users.clear();
            users.addAll(userList);
            log.debug("Successfully loaded {} users", userList.size());
        }))
        .exceptionally(throwable -> {
            String errorMessage;
            Throwable cause = throwable.getCause();
            if (cause instanceof AuthorizationException) {
                errorMessage = "You don't have permission to view users";
            } else if (cause instanceof AuthenticationException) {
                errorMessage = "Session expired - please log in again";
            } else if (cause instanceof ResourceNotFoundException) {
                errorMessage = "No users found";
            } else {
                errorMessage = "Failed to load users: " + throwable.getMessage();
            }
            log.error("User loading error: {}", errorMessage);
            Platform.runLater(() -> AlertUtil.showError("Loading Error", errorMessage));
            return null;
        });
}



@FXML
private void handleAddUser() {
    UserDTO newUser = showUserDialog(null);
    if (newUser != null) {
        userService.createUser(newUser)
            .thenAccept(user -> Platform.runLater(() -> {
                users.add(user);
                AlertUtil.showInfo("Success", "User created successfully");
                loadUsers(); // Refresh the user list
            }))
            .exceptionally(throwable -> {
                String errorMessage;
                if (throwable.getMessage().contains("UNIQUE constraint failed")) {
                    errorMessage = "Username or email already exists";
                } else {
                    errorMessage = "Failed to create user: " + throwable.getMessage();
                }
                Platform.runLater(() -> AlertUtil.showError("User Creation Error", errorMessage));
                return null;
            });
    }
}


    @FXML
    private void handleLogout() {
        userService.logout()
                .thenRun(() -> Platform.runLater(sceneManager::switchToLogin))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Logout failed"));
                    return null;
                });
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

    private void refreshBooks() {
        CompletableFuture.allOf(
                loadBooks(),
                CompletableFuture.runAsync(this::loadBorrowedBooks)
        ).exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to refresh books"));
            return null;
        });
    }

    @FXML
    private void handleAddBook() {
        BookDTO newBook = showBookDialog(null);
        if (newBook != null) {
            bookService.addBook(newBook)
                    .thenAccept(book -> Platform.runLater(() -> {
                        books.add(book);
                        AlertUtil.showInfo("Success", "Book added successfully");
                        loadBooks();
                    }))
                    .exceptionally(throwable -> {
                        String errorMessage;
                        Throwable cause = throwable.getCause();
                        if (cause instanceof ValidationException) {
                            errorMessage = "Invalid book data: " + cause.getMessage();
                        } else if (cause instanceof ConflictException) {
                            errorMessage = "Book with this ISBN already exists";
                        } else if (cause instanceof AuthorizationException) {
                            errorMessage = "You don't have permission to add books";
                        } else {
                            errorMessage = "Failed to add book: " + throwable.getMessage();
                        }
                        log.error("Book creation error: {}", errorMessage);
                        Platform.runLater(() -> AlertUtil.showError("Book Error", errorMessage));
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


 @FXML
private void handleBorrowSelected() {
    BookDTO selectedBook = booksTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        if (!selectedBook.isAvailable()) {
            AlertUtil.showWarning("Warning", "Book is not available for borrowing");
            return;
        }
        bookService.borrowBook(selectedBook.getBookId())
            .thenAccept(updatedBook -> Platform.runLater(() -> {
                refreshBooks();
                loadBorrowedBooks();
                refreshTransactions();
                AlertUtil.showInfo("Success", "Book borrowed successfully");
            }))
            .exceptionally(throwable -> {
                log.error("Failed to borrow book", throwable);
                Platform.runLater(() -> AlertUtil.showError("Error",
                    "Failed to borrow book: " + throwable.getMessage()));
                return null;
            });
    } else {
        AlertUtil.showWarning("Warning", "Please select a book to borrow");
    }
}

private void loadBorrowedBooks() {
    bookService.getBorrowedBooks()
        .thenAccept(borrowedBooks -> Platform.runLater(() ->
            borrowedBooksTable.setItems(FXCollections.observableArrayList(borrowedBooks))))
        .exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load borrowed books"));
            return null;
        });
}

@FXML
private void handleReturnSelected() {
    BookDTO selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        bookService.returnBook(selectedBook.getBookId())
            .thenAccept(updatedBook -> Platform.runLater(() -> {
                refreshBooks();
                loadBorrowedBooks();
                refreshTransactions();
                AlertUtil.showInfo("Success", "Book returned successfully");
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> AlertUtil.showError("Error", "Failed to return book"));
                return null;
            });
    } else {
        AlertUtil.showWarning("Warning", "Please select a book to return");
    }
}



  private void setupBorrowedBooksTable() {
    borrowedTitleColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTitle()));

    borrowedAuthorColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAuthor()));

    borrowerColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBorrower() != null ?
                    data.getValue().getBorrower().getUsername() : ""));

    loadBorrowedBooks();
}


    private void setupBookManagement() {
        // Book table columns
        bookIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBookId())));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        copiesColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAvailableCopies())));

        bookActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(e -> handleEditBook(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> handleDeleteBook(getTableView().getItems().get(getIndex())));
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

        copiesColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    BookDTO book = getTableView().getItems().get(getIndex());
                    setText(book.getAvailableCopies() + "/" + book.getTotalCopies());
                    if (book.getAvailableCopies() == 0) {
                        setStyle("-fx-background-color: #ffcdd2;"); // Light red for unavailable
                    } else if (book.getAvailableCopies() < book.getTotalCopies()) {
                        setStyle("-fx-background-color: #fff9c4;"); // Light yellow for partially available
                    } else {
                        setStyle("-fx-background-color: #c8e6c9;"); // Light green for fully available
                    }
                }
            }
        });
    }

}
