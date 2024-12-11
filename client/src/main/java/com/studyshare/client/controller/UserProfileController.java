package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.DateTimeUtil;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.TransactionType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
public class UserProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    private BookService bookService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final SceneManager sceneManager;
    private final ObservableList<BookDTO> borrowedBooks = FXCollections.observableArrayList();
    private Timeline refreshTimeline;

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
    @FXML private TableColumn<TransactionDTO, String> transactionIdColumn;
    @FXML private TableColumn<TransactionDTO, String> bookIdColumn;
    @FXML private TableColumn<TransactionDTO, String> dateColumn;
    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private TableColumn<TransactionDTO, String> timestampColumn;
    @FXML private TableColumn<TransactionDTO, String> actionColumn;
    @FXML private TableColumn<TransactionDTO, String> userColumn;
    @FXML private TableColumn<TransactionDTO, String> detailsColumn;




    public UserProfileController(UserService userService,
                                 BookService bookService,
                                 TransactionService transactionService,
                                 SceneManager sceneManager) {
        this.userService = userService;
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        log.debug("Initializing UserProfileController");
        log.debug("Checking FXML injected fields:");
        log.debug("transactionsTable: {}", transactionsTable != null);
        log.debug("searchField: {}", searchField != null);
        log.debug("allBooksTable: {}", allBooksTable != null);

        // Setup core UI components
        setupTableColumns();
        setupTransactionTable();
        setupStatusBasedStyling();

        // Load data and setup updates
        loadUserProfile();
        setupAllBooksTable();
        loadAllData();
        setupAutomaticStatusUpdates();

        borrowedBooksTable.setItems(borrowedBooks);

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                handleSearch();
            } else if (newValue.isEmpty()) {
                loadAllBooks();
            }
        });
    }

    private void setupTransactionTable() {
        if (transactionsTable == null) {
            log.error("Transaction table not initialized");
            return;
        }

        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTimestamp() != null ?
                        formatDateTime(data.getValue().getTimestamp()) : "N/A"));

        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType() != null ?
                        data.getValue().getType().toString() : "Unknown"));

        userColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUser() != null ?
                        data.getValue().getUser().getUsername() : "Unknown"));

        detailsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBook() != null ?
                        data.getValue().getBook().getTitle() : "Unknown"));

        // Enhanced styling with status indication
        actionColumn.setCellFactory(column -> new TableCell<TransactionDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().add("transaction-cell");
                    switch (item) {
                        case "BORROW" -> {
                            setStyle("-fx-text-fill: #006400; -fx-font-weight: bold;"); // Dark green
                            setTooltip(new Tooltip("Borrowed item"));
                        }
                        case "RETURN" -> {
                            setStyle("-fx-text-fill: #000080; -fx-font-weight: bold;"); // Navy blue
                            setTooltip(new Tooltip("Returned item"));
                        }
                        default -> setStyle("");
                    }
                }
            }
        });

        // Add row-level styling
        transactionsTable.setRowFactory(tv -> new TableRow<TransactionDTO>() {
            @Override
            protected void updateItem(TransactionDTO transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (transaction == null || empty) {
                    setStyle("");
                } else if (isOverdue(transaction)) {
                    setStyle("-fx-background-color: #ffebee;"); // Light red for overdue
                }
            }
        });

        loadTransactions();
    }





    private void setupTransactionStatusStyling() {
        transactionsTable.setRowFactory(tv -> new TableRow<TransactionDTO>() {
            @Override
            protected void updateItem(TransactionDTO transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (transaction == null || empty) {
                    setStyle("");
                } else {
                    if ("Overdue".equals(transaction.getStatus())) {
                        setStyle("-fx-background-color: #ffcccc;"); // Light red for overdue
                    } else if (transaction.getType() == TransactionType.BORROW) {
                        setStyle("-fx-background-color: #e6ffe6;"); // Light green for borrowed
                    } else if (transaction.getType() == TransactionType.RETURN) {
                        setStyle("-fx-background-color: #e6e6ff;"); // Light blue for returned
                    }
                }
            }
        });
    }



    private void setupTransactionTables() {
        setupBorrowedBooksTable();
        setupLendedBooksTable();

        transactionsTable.getStyleClass().add("transaction-table");

        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(formatDateTime(data.getValue().getTimestamp())));

        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().toString()));

        userColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUser().getUsername()));

        detailsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBook().getTitle()));

        loadTransactions();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String getTransactionDetails(TransactionDTO transaction) {
        return String.format("%s - %s",
                transaction.getBook().getTitle(),
                transaction.getType() == TransactionType.BORROW ? "Borrowed" : "Returned");
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
    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmDialog.setTitle("Confirm Return");
    confirmDialog.setHeaderText("Return Book");
    confirmDialog.setContentText("Are you sure you want to return: " + book.getTitle() + "?");

    confirmDialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            handleAsync(transactionService.returnBook(book.getBookId()))
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        loadAllBooks();
                        loadBorrowedBooks();
                        loadTransactionHistory();
                        AlertUtil.showInfo("Success", "Book returned successfully");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                        "Failed to return book: " + throwable.getMessage()));
                    return null;
                });
        }
    });
}

    private void setupBookStatusColumns() {
        // Add status column with styling
        TableColumn<BookDTO, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusDisplay()));

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Available")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold");
                    }
                }
            }
        });

        allBooksTable.getColumns().add(statusColumn);
    }

    private void updateBookStatus(BookDTO book) {
        handleAsync(bookService.getBookById(book.getBookId()))
                .thenAccept(updatedBook -> Platform.runLater(() -> {
                    int index = allBooksTable.getItems().indexOf(book);
                    if (index >= 0) {
                        allBooksTable.getItems().set(index, updatedBook);
                    }
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                            "Failed to update book status: " + throwable.getMessage()));
                    return null;
                });
    }



    private void handleBorrowBook(BookDTO book) {
        if (!book.isAvailable()) {
            AlertUtil.showWarning("Cannot Borrow", "This book is not available");
            return;
        }

        handleAsync(bookService.borrowBook(book.getBookId()))
                .thenAccept(borrowedBook -> {
                    Platform.runLater(() -> {
                        updateBookStatus(book);
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

    private void setupRefreshMechanism() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> refreshAllData())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void refreshAllData() {
        CompletableFuture.allOf(
                handleAsync(bookService.getAllBooks()),
                handleAsync(transactionService.getUserTransactions())
        ).thenRun(() -> {
            Platform.runLater(() -> {
                loadAllBooks();
                loadBorrowedBooks();
                loadTransactions();
                updateAllBookStatuses();
            });
        });
    }

    private void updateAllBookStatuses() {
        if (allBooksTable != null && allBooksTable.getItems() != null) {
            allBooksTable.getItems().forEach(this::updateBookStatus);
        }
        if (borrowedBooksTable != null && borrowedBooksTable.getItems() != null) {
            borrowedBooksTable.getItems().forEach(this::updateBookStatus);
        }
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

        // All Books table
        allTitleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        allAuthorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAuthor()));
        allAvailableColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAvailableCopies())));

        // Borrowed Books table
        borrowedTitleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        borrowedAuthorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAuthor()));
        borrowedDescriptionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));
        dueDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(formatDueDate(data.getValue())));

        // Lended Books table
        lendedTitleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));
        lendedAuthorColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAuthor()));
        lendedDescriptionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));
        borrowerColumn.setCellValueFactory(data ->
                new SimpleStringProperty(getBorrowerName(data.getValue())));
    }

private void loadUserTransactions() {
    transactionService.getUserTransactions()
        .thenAccept(transactions -> Platform.runLater(() -> {
            transactionsTable.getItems().setAll(transactions);
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

private void setupTransactionHistory() {
    timestampColumn.setCellValueFactory(data ->
        new SimpleStringProperty(DateTimeUtil.formatDateTime(data.getValue().getTimestamp())));

    actionColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getType().toString()));

    detailsColumn.setCellValueFactory(data ->
        new SimpleStringProperty(getTransactionDetails(data.getValue())));

    loadTransactionHistory();
}

private void loadTransactionHistory() {
    handleAsync(transactionService.getUserTransactions())
        .thenAccept(transactions -> Platform.runLater(() -> {
            transactionsTable.getItems().setAll(transactions);
        }))
        .exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to load transaction history: " + throwable.getMessage()));
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


    @FXML
    private void handleRefresh() {
        refreshAllTables();
    }

private void refreshAllTables() {
    CompletableFuture.allOf(
        handleAsync(bookService.getAllBooks()),
        handleAsync(transactionService.getUserTransactions())
    ).thenRun(() -> {
        Platform.runLater(() -> {
            loadAllBooks();
            loadBorrowedBooks();
            loadLendedBooks();
            loadTransactionHistory(); // Changed from loadTransactions to loadTransactionHistory
            AlertUtil.showInfo("Success", "Data refreshed successfully");
        });
    });
}


    private void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            handleAsync(bookService.searchBooks(query))
                    .thenAccept(books -> Platform.runLater(() -> {
                        allBooksTable.getItems().setAll(books);
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Search Error",
                                "Failed to search books: " + throwable.getMessage()));
                        return null;
                    });
        }
    }




    private TableCell<TransactionDTO, String> createStyledCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().add("transaction-cell");
                }
            }
        };
    }

private void loadTransactions() {
    handleAsync(transactionService.getUserTransactions())
        .thenAccept(transactions -> {
            if (transactions != null) {
                Platform.runLater(() -> {
                    transactionsTable.getItems().setAll(transactions);
                    updateTransactionStatus(transactions);
                });
            }
        })
        .exceptionally(throwable -> {
            log.error("Error loading transactions: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Transaction Error",
                "Failed to load transactions: " + throwable.getMessage()));
            return null;
        });
}


    private void updateTransactionStatus(List<TransactionDTO> transactions) {
        transactions.forEach(transaction -> {
            if (transaction.getDueDate() != null &&
                    transaction.getDueDate().isBefore(LocalDateTime.now())) {
                transaction.setStatus("Overdue");
            }
        });
    }



private void setupStatusBasedStyling() {
    transactionsTable.setRowFactory(tv -> new TableRow<TransactionDTO>() {
        @Override
        protected void updateItem(TransactionDTO transaction, boolean empty) {
            super.updateItem(transaction, empty);
            getStyleClass().removeAll("transaction-row-overdue",
                                    "transaction-row-borrowed",
                                    "transaction-row-returned");

            if (transaction != null && !empty) {
                switch (transaction.getType()) {
                    case BORROW -> {
                        if (isOverdue(transaction)) {
                            getStyleClass().add("transaction-row-overdue");
                        } else {
                            getStyleClass().add("transaction-row-borrowed");
                        }
                    }
                    case RETURN -> getStyleClass().add("transaction-row-returned");
                }
            }
        }
    });
}

private boolean isOverdue(TransactionDTO transaction) {
    return transaction.getDueDate() != null &&
           transaction.getDueDate().isBefore(LocalDateTime.now()) &&
           transaction.getType() == TransactionType.BORROW;
}

// In UserProfileController.java
private void setupAutomaticStatusUpdates() {
    Timeline statusUpdater = new Timeline(
        new KeyFrame(Duration.seconds(30), event -> updateTransactionStatuses())
    );
    statusUpdater.setCycleCount(Timeline.INDEFINITE);
    statusUpdater.play();
}



private void updateTransactionStatuses() {
    if (transactionsTable.getItems() != null) {
        transactionsTable.getItems().forEach(transaction -> {
            if (transaction.getType() == TransactionType.BORROW) {
                if (isOverdue(transaction)) {
                    transaction.setStatus("Overdue");
                } else {
                    long daysLeft = ChronoUnit.DAYS.between(
                        LocalDateTime.now(),
                        transaction.getDueDate()
                    );
                    transaction.setStatus("Due in " + daysLeft + " days");
                }
            }
        });
        transactionsTable.refresh();
    }
}

@FXML
private void handleBorrowSelected() {
    BookDTO selectedBook = allBooksTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        handleBorrowBook(selectedBook);
    } else {
        AlertUtil.showWarning("Selection Required", "Please select a book to borrow");
    }
}

@FXML
private void handleReturnSelected() {
    BookDTO selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        handleReturnBook(selectedBook);
    } else {
        AlertUtil.showWarning("Selection Required", "Please select a book to return");
    }
}

@FXML
private void handleReturnLended() {
    BookDTO selectedBook = lendedBooksTable.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
        handleReturnBook(selectedBook);
    } else {
        AlertUtil.showWarning("Selection Required", "Please select a book to return");
    }
}

    public void cleanup() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
}
    public void stop() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }
}