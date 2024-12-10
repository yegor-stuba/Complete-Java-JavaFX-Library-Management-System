package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.client.util.SceneManager;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
public class BookManagementController extends BaseController {
    private final BookService bookService;
    private final TransactionService transactionService;
    private final ObservableList<BookDTO> books = FXCollections.observableArrayList();
    private final ObservableList<TransactionDTO> borrowedBooks = FXCollections.observableArrayList();
    private final ObservableList<TransactionDTO> lentBooks = FXCollections.observableArrayList();
    private final ObservableList<BookDTO> allBooks = FXCollections.observableArrayList();

    @FXML private TextField searchField;
    @FXML private TableView<BookDTO> allBooksTable;
    @FXML private TableView<TransactionDTO> borrowedBooksTable;
    @FXML private TableView<TransactionDTO> lentBooksTable;

    @FXML private TableColumn<BookDTO, String> titleColumn;
    @FXML private TableColumn<BookDTO, String> authorColumn;
    @FXML private TableColumn<BookDTO, String> isbnColumn;
    @FXML private TableColumn<BookDTO, Integer> copiesColumn;
    @FXML private TableColumn<BookDTO, String> statusColumn;
    @FXML private Label borrowedBooksLabel;
    private SceneManager sceneManager;

    public BookManagementController(BookService bookService, TransactionService transactionService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.sceneManager = sceneManager;

    }

    @FXML
    private void initialize() {
        initializeCollections();
        setupTables();
        setupSearch();
        setupAllBooksTable();
        setupBorrowedBooksTable();
        setupLentBooksTable();
        setupTableActions();
        loadAllData();
    }
    private void initializeCollections() {
        allBooksTable.setItems(allBooks);
        borrowedBooksTable.setItems(borrowedBooks);
        lentBooksTable.setItems(lentBooks);
    }

    private void setupTables() {
        setupAllBooksTable();
        setupBorrowedBooksTable();
        setupLentBooksTable();
    }

    private void loadData() {
        loadAllBooks();
        loadBorrowedBooks();
        loadLentBooks();
        refreshAllBooks();
        refreshBorrowedBooks();
        refreshLentBooks();
    }

    private void refreshAllBooks() {
        bookService.getAllBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        allBooks.setAll(books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }


    private void refreshLentBooks() {
        bookService.getLentBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        lentBooks.setAll((TransactionDTO) books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }

    private void refreshBorrowedBooks() {
        bookService.getBorrowedBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        borrowedBooks.setAll((TransactionDTO) books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }

    private void loadAllBooks() {
        bookService.getAllBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        allBooksTable.getItems().setAll(books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }

    private void loadBorrowedBooks() {
        bookService.getBorrowedBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        borrowedBooksTable.getItems().setAll((TransactionDTO) books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }

    private void loadLentBooks() {
        bookService.getLentBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        lentBooksTable.getItems().setAll((TransactionDTO) books)))
                .exceptionally(throwable -> {
                    ErrorHandler.handle(throwable);
                    return null;
                });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                bookService.searchBooks(newValue)
                        .thenAccept(books -> Platform.runLater(() ->
                                allBooksTable.getItems().setAll(books)))
                        .exceptionally(throwable -> {
                            ErrorHandler.handle(throwable);
                            return null;
                        });
            } else {
                loadAllBooks();
            }
        });
    }

    private void setupActionColumn(TableView<BookDTO> tableView) {
        TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button();
            {
                actionButton.setOnAction(event -> {
                    BookDTO book = getTableView().getItems().get(getIndex());
                    if (book.isAvailable()) {
                        handleBorrow(book);
                    } else {
                        handleReturn(book);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BookDTO book = getTableView().getItems().get(getIndex());
                    actionButton.setText(book.isAvailable() ? "Borrow" : "Return");
                    setGraphic(actionButton);
                }
            }
        });
        tableView.getColumns().add(actionCol);
    }

    private void loadAllData() {
        bookService.getAllBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        allBooksTable.getItems().setAll(books)));

        transactionService.getActiveLoansCount()
                .thenAccept(count -> Platform.runLater(() ->
                        borrowedBooksLabel.setText("Borrowed Books: " + count)));
    }

    private void setupColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(getBookStatus(cellData.getValue())));
    }

    private void setupTableActions() {
        TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(event -> {
                    BookDTO book = getTableView().getItems().get(getIndex());
                    if (book.isAvailable()) {
                        handleBorrow(book);
                    } else {
                        handleReturn(book);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BookDTO book = getTableView().getItems().get(getIndex());
                    actionButton.setText(book.isAvailable() ? "Borrow" : "Return");
                    setGraphic(actionButton);
                }
            }
        });
        allBooksTable.getColumns().add(actionCol);
    }


    private void setupAllBooksTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

        TableColumn<BookDTO, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button borrowButton = new Button("Borrow");
            {
                borrowButton.setOnAction(event -> handleBorrow(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : borrowButton);
            }
        });
        allBooksTable.getColumns().add(actionCol);
        allBooksTable.setItems(books);
    }

    private void setupBorrowedBooksTable() {
        TableColumn<TransactionDTO, String> titleCol = new TableColumn<>("Title");
        TableColumn<TransactionDTO, String> authorCol = new TableColumn<>("Author");
        TableColumn<TransactionDTO, LocalDate> dateCol = new TableColumn<>("Borrowed Date");

        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getAuthor()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        borrowedBooksTable.getColumns().addAll(titleCol, authorCol, dateCol);
        borrowedBooksTable.setItems(borrowedBooks);
    }

    private void setupLentBooksTable() {
        TableColumn<TransactionDTO, String> titleCol = new TableColumn<>("Title");
        TableColumn<TransactionDTO, String> authorCol = new TableColumn<>("Author");
        TableColumn<TransactionDTO, String> borrowerCol = new TableColumn<>("Borrowed By");

        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getAuthor()));
        borrowerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser().getUsername()));

        lentBooksTable.getColumns().addAll(titleCol, authorCol, borrowerCol);
        lentBooksTable.setItems(lentBooks);
    }




    private void loadBooks() {
        bookService.getAllBooks()
                .thenAccept(books -> Platform.runLater(() -> {
                    allBooksTable.getItems().setAll(books);
                    updateBorrowedAndLentTables();
                }))
                .exceptionally(throwable -> {
                    log.error("Failed to load books: {}", throwable.getMessage());
                    Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load books"));
                    return null;
                });
    }


    private void updateBorrowedAndLentTables() {
        bookService.getBorrowedBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        borrowedBooksTable.getItems().setAll((TransactionDTO) books)));

        bookService.getLentBooks()
                .thenAccept(books -> Platform.runLater(() ->
                        lentBooksTable.getItems().setAll((TransactionDTO) books)));
    }

    private String getBookStatus(BookDTO book) {
        return book.getAvailableCopies() > 0 ? "Available" : "Borrowed";
    }

    @FXML
private void showTransactionHistory() {
    sceneManager.switchToTransactions();
}

    private void handleReturn(BookDTO book) {
        transactionService.returnBook(book.getBookId())
                .thenRun(() -> {
                    loadBooks();
                    AlertUtil.showInfo("Success", "Book returned successfully");
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to return book");
                    return null;
                });
    }

    private void handleBorrow(BookDTO book) {
        transactionService.borrowBook(book.getBookId())
                .thenRun(() -> {
                    loadBooks();
                    AlertUtil.showInfo("Success", "Book borrowed successfully");
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to borrow book");
                    return null;
                });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadBooks();
            return;
        }

        bookService.searchBooks(searchTerm)
            .thenAccept(results -> Platform.runLater(() -> {
                books.clear();
                books.addAll(results);
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> AlertUtil.showError("Search Error", "Failed to perform search"));
                return null;
            });
    }
}

