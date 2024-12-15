package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.AlertUtil;

import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;

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

import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
public class UserProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookService bookService;
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

@FXML private TableColumn<BookDTO, String> borrowedTitleColumn;
@FXML private TableColumn<BookDTO, String> borrowedAuthorColumn;
    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private TableColumn<TransactionDTO, String> timestampColumn;
    @FXML private TableColumn<TransactionDTO, String> actionColumn;
    @FXML private TableColumn<TransactionDTO, String> userColumn;
    @FXML private TableColumn<TransactionDTO, String> detailsColumn;
    @FXML private Pagination transactionPagination;

    @FXML private TableView<BookDTO> allBooksTable;
    @FXML private TableColumn<BookDTO, String> allIsbnColumn;
    @FXML private TableColumn<BookDTO, Void> allActionsColumn;
    @FXML private TableColumn<BookDTO, String> borrowedDueDateColumn;

@FXML private TableColumn<TransactionDTO, String> bookColumn;
@FXML private TableColumn<TransactionDTO, String> statusColumn;
@FXML private TableColumn<BookDTO, String> allCopiesColumn;

    @FXML private TextField searchField;



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

        setupBookTables();
        setupTransactionTable();

        loadUserProfile();
        loadAllData();
        Refresh();

        borrowedBooksTable.setItems(borrowedBooks);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                handleSearch();
            } else if (newValue.isEmpty()) {
                loadAllBooks();
            }
        });
    }

// Transaction-related methods that need to be updated:
@FXML
private void setupTransactionTable() {
    timestampColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTransactionDate().toString()));
    actionColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().toString()));
    userColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUsername()));
    bookColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBookTitle()));


    transactionPagination.setPageCount(calculatePageCount());
    transactionPagination.setCurrentPageIndex(0);
    transactionPagination.setPageFactory(this::createPage);

    loadTransactions();
}

private Node createPage(int pageIndex) {
    int pageSize = 10;
    transactionService.getTransactions(pageIndex, pageSize)
        .thenAccept(transactions -> Platform.runLater(() ->
            transactionsTable.setItems(FXCollections.observableArrayList(transactions))))
        .exceptionally(throwable -> {
            log.error("Failed to load transactions page", throwable);
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to load transactions: " + throwable.getMessage()));
            return null;
        });
    return transactionsTable;
}

private int calculatePageCount() {
    return (int) Math.ceil(transactionService.getTransactionCount().join() / 10.0);
}

private void loadTransactions() {
    handleAsync(transactionService.getCurrentUserTransactions())
        .thenAccept(transactions -> {
            if (transactions != null) {
                Platform.runLater(() -> {
                    transactionsTable.setItems(FXCollections.observableArrayList(transactions));
                    log.debug("Loaded {} transactions", transactions.size());
                });
            }
        })
        .exceptionally(throwable -> {
            log.error("Error loading transactions: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to load transactions: " + throwable.getMessage()));
            return null;
        });
}

private void setupTransactionStyling() {
    actionColumn.setCellFactory(column -> new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle("");
            } else {
                setText(item);
                if ("BORROW".equals(item)) {
                    setStyle("-fx-text-fill: #006400; -fx-font-weight: bold;");
                } else if ("RETURN".equals(item)) {
                    setStyle("-fx-text-fill: #000080; -fx-font-weight: bold;");
                }
            }
        }
    });
}




    private void Refresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(120), event -> refreshAllData())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }


    private void loadAllData() {
        CompletableFuture.allOf(
                CompletableFuture.runAsync(this::loadAllBooks),
                CompletableFuture.runAsync(this::loadBorrowedBooks),
                CompletableFuture.runAsync(this::loadTransactions)
        ).exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load data"));
            return null;
        });
    }

    private void handleBorrowBook(BookDTO book) {
        if (!book.isAvailable()) {
            AlertUtil.showWarning("Cannot Borrow", "This book is not available");
            return;
        }

        bookService.borrowBook(book.getBookId())
                .thenCompose(borrowedBook ->
                        transactionService.createTransaction(book.getBookId(), TransactionType.BORROW))
                .thenAccept(transaction -> Platform.runLater(() -> {
                    updateBookStatus(book);
                    loadBorrowedBooks();
                    loadTransactions();
                    AlertUtil.showInfo("Success", "Book borrowed successfully");
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                            "Failed to borrow book: " + throwable.getMessage()));
                    return null;
                });
    }




private void setupBorrowedBooksTable() {
    borrowedTitleColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getTitle()));
    borrowedAuthorColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getAuthor()));
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
            LocalDateTime dueDate = LocalDateTime.parse(transaction.getTransactionDate().toString())
                                                .plusDays(14);
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
                .thenRun(() -> Platform.runLater(() -> {
                    loadAllBooks();
                    loadBorrowedBooks();
                    loadTransactionHistory();
                    AlertUtil.showInfo("Success", "Book returned successfully");
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                        "Failed to return book: " + throwable.getMessage()));
                    return null;
                });
        }
    });
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



    private void refreshAllData() {
        CompletableFuture.allOf(
                handleAsync(bookService.getAllBooks()),
                handleAsync(transactionService.getUserTransactions())
        ).thenRun(() -> Platform.runLater(() -> {
            loadAllBooks();
            loadBorrowedBooks();
            loadTransactions();
            updateAllBookStatuses();
        }));
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

    dialog.showAndWait().ifPresent(updatedBook -> handleAsync(bookService.updateBook(updatedBook.getBookId(), updatedBook))
        .thenAccept(result -> Platform.runLater(() -> {
            loadAllBooks();
            AlertUtil.showInfo("Success", "Book updated successfully");
        }))
        .exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to update book: " + throwable.getMessage()));
            return null;
        }));
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
private void setupBookTables() {
    // All Books Table
    allTitleColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getTitle()));
    allAuthorColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getAuthor()));
    allIsbnColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getIsbn()));
    allCopiesColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getAvailableCopies() + "/" + data.getValue().getTotalCopies()));

    setupActionsColumn();

    // Borrowed Books Table
    borrowedTitleColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getTitle()));
    borrowedAuthorColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getAuthor()));
    borrowedDueDateColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getIsbn()));
}

private void setupActionsColumn() {
    allActionsColumn.setCellFactory(param -> new TableCell<>() {
        private final Button borrowButton = new Button("Borrow");

        {
            borrowButton.setOnAction(event -> {
                BookDTO book = getTableView().getItems().get(getIndex());
                handleBorrowBook(book);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                BookDTO book = getTableView().getItems().get(getIndex());
                if (book.getAvailableCopies() > 0) {
                    setGraphic(borrowButton);
                } else {
                    setGraphic(null);
                }
            }
        }
    });
}

private void setupBookActions() {
    allActionsColumn.setCellFactory(col -> new TableCell<>() {
        private final Button borrowButton = new Button("Borrow");
        {
            borrowButton.setOnAction(e -> handleBorrowBook(getTableView().getItems().get(getIndex())));
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                BookDTO book = getTableView().getItems().get(getIndex());
                borrowButton.setDisable(!book.isAvailable());
                setGraphic(borrowButton);
            }
        }
    });
}

private void loadBooks() {
    bookService.getAllBooks()
        .thenAccept(bookList -> Platform.runLater(() -> allBooksTable.setItems(FXCollections.observableArrayList(bookList))))
        .exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load books"));
            return null;
        });
}


    @FXML
    public void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            bookService.searchBooks(query)
                    .thenAccept(results -> Platform.runLater(() -> allBooksTable.setItems(FXCollections.observableArrayList(results))))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Search Error",
                                "Failed to search books: " + throwable.getMessage()));
                        return null;
                    });
        } else {
            loadBooks();
        }
    }


    @FXML
    private void handleLogout() {
        userService.logout()
                .thenRun(() -> {
                    sceneManager.switchToLogin();
                    AlertUtil.showInfo("Success", "Logged out successfully");
                })
                .exceptionally(throwable -> {
                    return null;
                });
    }


private void loadTransactionHistory() {
    handleAsync(transactionService.getUserTransactions())
        .thenAccept(transactions -> Platform.runLater(() -> transactionsTable.getItems().setAll(transactions)))
        .exceptionally(throwable -> {
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to load transaction history: " + throwable.getMessage()));
            return null;
        });
}




    @FXML
    private void handleRefresh() {
        refreshAllTables();
    }

private void refreshAllTables() {
    CompletableFuture.allOf(
        handleAsync(bookService.getAllBooks()),
        handleAsync(transactionService.getUserTransactions())
    ).thenRun(() -> Platform.runLater(() -> {
        loadAllBooks();
        loadBorrowedBooks();
        loadTransactionHistory(); // Changed from loadTransactions to loadTransactionHistory
        AlertUtil.showInfo("Success", "Data refreshed successfully");
    }));
}



    @FXML
    private void handleBorrowSelected() {
        BookDTO selectedBook = allBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            bookService.borrowBook(selectedBook.getBookId())
                    .thenAccept(updatedBook -> Platform.runLater(() -> {
                        refreshBooks();
                        loadBorrowedBooks();
                        AlertUtil.showInfo("Success", "Book borrowed successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error",
                                "Failed to borrow book: " + throwable.getMessage()));
                        return null;
                    });
        }
    }

    @FXML
    private void handleReturnSelected() {
        BookDTO selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            bookService.returnBook(selectedBook.getBookId())
                    .thenAccept(updatedBook -> Platform.runLater(() -> {
                        refreshBooks();
                        loadBorrowedBooks();
                        AlertUtil.showInfo("Success", "Book returned successfully");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> AlertUtil.showError("Error",
                                "Failed to return book: " + throwable.getMessage()));
                        return null;
                    });
        }
    }

    private void refreshBooks() {
        loadAllBooks();
        loadBorrowedBooks();
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


    private void loadBorrowedBooks() {
        handleAsync(transactionService.getUserTransactions())
                .thenAccept(transactions -> {
                    if (transactions != null) {
                        List<BookDTO> books = transactions.stream()
                                .filter(t -> t != null && t.getBookId() != null)
                                .map(t -> BookDTO.builder()
                                        .bookId(t.getBookId())
                                        .title(t.getBookTitle())
                                        .build())
                                .collect(Collectors.toList());
                        Platform.runLater(() -> borrowedBooks.setAll(books));
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error loading borrowed books: {}", throwable.getMessage());
                    Platform.runLater(() -> AlertUtil.showError("Books Error",
                            "Failed to load borrowed books: " + throwable.getMessage()));
                    return null;
                });
    }


}