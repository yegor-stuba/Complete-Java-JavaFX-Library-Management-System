package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.studyshare.client.util.TransactionUtil.getTransactionStatus;


@SuppressWarnings("unused")
public class UserProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    private BookService bookService;
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
    @FXML
    private TableColumn<BookDTO, String> allTitleColumn;
    @FXML
    private TableColumn<BookDTO, String> allAuthorColumn;
    @FXML
    private TableColumn<BookDTO, String> allAvailableColumn;
    @FXML
    private TableColumn<BookDTO, String> lendedTitleColumn;
    @FXML
    private TableColumn<BookDTO, String> lendedAuthorColumn;
    @FXML
    private TableColumn<BookDTO, String> borrowerColumn;
@FXML private TableColumn<BookDTO, String> borrowedTitleColumn;
@FXML private TableColumn<BookDTO, String> borrowedAuthorColumn;
@FXML private TableColumn<BookDTO, String> borrowedDescriptionColumn;
@FXML private TableColumn<BookDTO, String> lendedDescriptionColumn;


    public UserProfileController(UserService userService, TransactionService transactionService, SceneManager sceneManager) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;
        this.bookService = bookService;
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        loadUserProfile();
        setupAllBooksTable();
        setupTransactionTables();
        loadAllData();
        borrowedBooksTable.setItems(borrowedBooks);
    }

    private void setupTransactionTables() {
        setupBorrowedBooksTable();
        setupLendedBooksTable();
    }

    private void loadAllData() {
        loadAllBooks();
        loadBorrowedBooks();
        loadLendedBooks();
    }

private void setupAllBooksTable() {
    allTitleColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTitle()));
    allAuthorColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAuthor()));
    allAvailableColumn.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getAvailableCopies())));

    // Add status column with styling
    TableColumn<BookDTO, String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isAvailable() ? "Available" : "Borrowed"));

    TableColumn<BookDTO, String> transactionStatusColumn = new TableColumn<>("Transaction Status");
    transactionStatusColumn.setCellValueFactory(data -> {
        BookDTO book = data.getValue();
        return new SimpleStringProperty(book.getStatusDisplay());
    });

    TableColumn<BookDTO, String> lastTransactionColumn = new TableColumn<>("Last Transaction");
    lastTransactionColumn.setCellValueFactory(data -> {
        BookDTO book = data.getValue();
        return new SimpleStringProperty(getLastTransactionDate(book));
    });
    statusColumn.setCellFactory(column -> new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle("");
            } else {
                setText(item);
                String newStyle = item.equals("Available") ?
                    "-fx-text-fill: green; -fx-font-weight: bold" :
                    "-fx-text-fill: red; -fx-font-weight: bold";
                setStyle(newStyle);
            }
        }
    });

    // Add action buttons column
    TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Actions");
    actionCol.setCellFactory(param -> new TableCell<>() {
        private final Button borrowButton = new Button("Borrow");
        private final Button editButton = new Button("Edit");

        {
            borrowButton.setOnAction(event -> {
                BookDTO book = getTableView().getItems().get(getIndex());
                handleBorrowBook(book);
                handleRefresh(); // Auto refresh after action
            });

            editButton.setOnAction(event -> {
                BookDTO book = getTableView().getItems().get(getIndex());
                handleEditBook(book);
                handleRefresh(); // Auto refresh after action
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(5);
                buttons.setSpacing(5);
                BookDTO book = getTableView().getItems().get(getIndex());

                if (book.getOwnerId().equals(getCurrentUserId())) {
                    editButton.getStyleClass().add("edit-button");
                    buttons.getChildren().add(editButton);
                }

                if (book.isAvailable() && !book.getOwnerId().equals(getCurrentUserId())) {
                    borrowButton.getStyleClass().add("borrow-button");
                    buttons.getChildren().add(borrowButton);
                }

                setGraphic(buttons);
            }
        }
    });

    allBooksTable.getColumns().addAll(statusColumn, actionCol);
}

private String getLastTransactionDate(BookDTO book) {
    return transactionService.getLatestTransaction(book.getBookId())
        .thenApply(transaction -> {
            if (transaction == null) {
                return "No transactions";
            }
            return transaction.getTimestamp()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        })
        .join();
}

private void setupLendedBooksTable() {
    lendedTitleColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getTitle()));
    lendedAuthorColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getAuthor()));
    lendedDescriptionColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getDescription()));
    borrowerColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(getBorrowerName(cellData.getValue())));

    // Add action column
    TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Actions");
    actionCol.setCellFactory(param -> new TableCell<>() {
        private final Button returnButton = new Button("Return");
        {
            returnButton.setOnAction(event -> {
                BookDTO book = getTableView().getItems().get(getIndex());
                handleReturnBook(book);
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : returnButton);
        }
    });
    lendedBooksTable.getColumns().add(actionCol);
}

private void setupBorrowedBooksTable() {
    borrowedTitleColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getTitle()));
    borrowedAuthorColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getAuthor()));
    borrowedDescriptionColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getDescription()));
    dueDateColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(formatDueDate(cellData.getValue())));

    // Add action column
    TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Actions");
    actionCol.setCellFactory(param -> new TableCell<>() {
        private final Button returnButton = new Button("Return");
        {
            returnButton.setOnAction(event -> {
                BookDTO book = getTableView().getItems().get(getIndex());
                handleReturnBook(book);
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : returnButton);
        }
    });
    borrowedBooksTable.getColumns().add(actionCol);
}


private String formatDueDate(BookDTO book) {
    return transactionService.getLatestTransaction(book.getBookId())
        .thenApply(transaction -> {
            if (transaction == null) return "N/A";
            LocalDateTime dueDate = transaction.getTimestamp().plusDays(14);
            return dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        })
        .join();
}


    private void handleReturnBook(BookDTO book) {
        handleAsync(transactionService.returnBook(book.getBookId()))
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        loadAllBooks();
                        loadBorrowedBooks();
                        loadLendedBooks();
                        AlertUtil.showInfo("Success", "Book returned successfully");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                            "Failed to return book: " + throwable.getMessage()));
                    return null;
                });
    }

    private void handleBorrowBook(BookDTO book) {
        handleAsync(transactionService.borrowBook(book.getBookId()))
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        loadAllBooks();
                        loadBorrowedBooks();
                        AlertUtil.showInfo("Success", "Book borrowed successfully");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                            "Failed to borrow book: " + throwable.getMessage()));
                    return null;
                });
    }

private void handleEditBook(BookDTO book) {
    Dialog<BookDTO> dialog = new Dialog<>();
    dialog.setTitle("Edit Book");
    dialog.setHeaderText("Edit book details");

    TextField titleField = new TextField(book.getTitle());
    TextField authorField = new TextField(book.getAuthor());
    TextField isbnField = new TextField(book.getIsbn());
    TextField copiesField = new TextField(String.valueOf(book.getAvailableCopies()));

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    grid.add(new Label("Title:"), 0, 0);
    grid.add(titleField, 1, 0);
    grid.add(new Label("Author:"), 0, 1);
    grid.add(authorField, 1, 1);
    grid.add(new Label("ISBN:"), 0, 2);
    grid.add(isbnField, 1, 2);
    grid.add(new Label("Copies:"), 0, 3);
    grid.add(copiesField, 1, 3);

    dialog.getDialogPane().setContent(grid);

    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.setDisable(false);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            try {
                book.setAvailableCopies(Integer.parseInt(copiesField.getText()));
            } catch (NumberFormatException e) {
                book.setAvailableCopies(1);
            }
            return book;
        }
        return null;
    });

    dialog.showAndWait().ifPresent(updatedBook -> {
        handleAsync(bookService.updateBook(updatedBook.getBookId(), updatedBook))
            .thenAccept(result -> {
                Platform.runLater(() -> {
                    loadAllBooks();
                    AlertUtil.showInfo("Success", "Book updated successfully");
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> AlertUtil.showError("Error",
                    "Failed to update book: " + throwable.getMessage()));
                return null;
            });
    });
}

private void loadLendedBooks() {
    handleAsync(transactionService.getUserTransactions())
        .thenAccept(transactions -> {
            if (transactions != null) {
                List<BookDTO> lendedBooks = transactions.stream()
                    .filter(t -> t.getBook().getOwnerId().equals(getCurrentUserId()))
                    .map(TransactionDTO::getBook)
                    .collect(Collectors.toList());
                Platform.runLater(() -> lendedBooksTable.getItems().setAll(lendedBooks));
            }
        })
        .exceptionally(throwable -> {
            log.error("Error loading lended books: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Books Error",
                "Failed to load lended books: " + throwable.getMessage()));
            return null;
        });
}

private String getBorrowerName(BookDTO book) {
    return handleAsync(transactionService.getUserTransactions())
        .thenApply(transactions -> transactions.stream()
            .filter(t -> t.getBook().getBookId().equals(book.getBookId()))
            .findFirst()
            .map(t -> t.getUser().getUsername())
            .orElse("Unknown"))
        .join();
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

private void loadUserTransactions() {
    transactionService.getUserTransactions()
        .thenAccept(transactions -> Platform.runLater(() -> {
            // Assuming you have a TableView named borrowedBooksTable
            borrowedBooksTable.getItems().setAll((BookDTO) transactions);
        }))
        .exceptionally(throwable -> {
            log.error("Error loading transactions: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Transaction Error",
                "Failed to load transactions: " + throwable.getMessage()));
            return null;
        });
}

private void loadBorrowedBooks() {
    handleAsync(transactionService.getUserTransactions())
        .thenAccept(transactions -> {            if (transactions != null) {
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

    private void loadAllBooks() {
    handleAsync(bookService.getAllBooks())
        .thenAccept(books -> Platform.runLater(() -> {
            if (books != null) {
                allBooksTable.getItems().setAll(books);
            }
        }))
        .exceptionally(throwable -> {
            log.error("Error loading all books: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Books Error",
                "Failed to load books: " + throwable.getMessage()));
            return null;
        });
}

    // Add at the top with other FXML fields
@FXML
private TextField searchField;
@FXML
private TableView<BookDTO> allBooksTable;
@FXML
private TableView<BookDTO> lendedBooksTable;

// Add this method to handle book registration
@FXML
private void handleRegisterBook() {
    Dialog<BookDTO> dialog = new Dialog<>();
    dialog.setTitle("Register New Book");
    dialog.setHeaderText("Enter book details");

    // Create form fields
    TextField titleField = new TextField();
    TextField authorField = new TextField();
    TextField isbnField = new TextField();
    TextField copiesField = new TextField();

    // Create layout
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    grid.add(new Label("Title:"), 0, 0);
    grid.add(titleField, 1, 0);
    grid.add(new Label("Author:"), 0, 1);
    grid.add(authorField, 1, 1);
    grid.add(new Label("ISBN:"), 0, 2);
    grid.add(isbnField, 1, 2);
    grid.add(new Label("Copies:"), 0, 3);
    grid.add(copiesField, 1, 3);

    dialog.getDialogPane().setContent(grid);

    // Add buttons
    ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

    // Enable/Disable register button depending on whether a title was entered
    Node registerButton = dialog.getDialogPane().lookupButton(registerButtonType);
    registerButton.setDisable(true);

    titleField.textProperty().addListener((observable, oldValue, newValue) ->
        registerButton.setDisable(newValue.trim().isEmpty()));

    // Convert the result
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == registerButtonType) {
            BookDTO newBook = new BookDTO();
            newBook.setTitle(titleField.getText());
            newBook.setAuthor(authorField.getText());
            newBook.setIsbn(isbnField.getText());
            try {
                newBook.setAvailableCopies(Integer.parseInt(copiesField.getText()));
            } catch (NumberFormatException e) {
                newBook.setAvailableCopies(1);
            }
            return newBook;
        }
        return null;
    });

    dialog.showAndWait().ifPresent(book -> {
        handleAsync(bookService.registerBook(book))
            .thenAccept(registeredBook -> {
                Platform.runLater(() -> {
                    loadAllBooks();
                    AlertUtil.showInfo("Success", "Book registered successfully");
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> AlertUtil.showError("Error",
                    "Failed to register book: " + throwable.getMessage()));
                return null;
            });
    });
}
    @FXML
    private void handleRefresh() {
        refreshAllTables();
    }

 private void refreshAllTables() {
    CompletableFuture.allOf(
        handleAsync(bookService.getAllBooks()),
        handleAsync(transactionService.getUserTransactions()),
        handleAsync(transactionService.getUserTransactions())
    ).thenRun(() -> {
        loadAllBooks();
        loadBorrowedBooks();
        loadLendedBooks();
        Platform.runLater(() -> AlertUtil.showInfo("Success", "Data refreshed successfully"));
    });
}
}