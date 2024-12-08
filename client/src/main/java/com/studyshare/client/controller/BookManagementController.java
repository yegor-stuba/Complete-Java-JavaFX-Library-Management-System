package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.common.dto.BookDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;

public class BookManagementController extends BaseController {
    private final BookService bookService;
    private final ObservableList<BookDTO> books = FXCollections.observableArrayList();

   @FXML private TextField searchField;
@FXML private TableView<BookDTO> booksTable;
@FXML private TableColumn<BookDTO, String> titleColumn;
@FXML private TableColumn<BookDTO, String> authorColumn;
@FXML private TableColumn<BookDTO, String> isbnColumn;
@FXML private TableColumn<BookDTO, Integer> copiesColumn;

@FXML
private void initialize() {
    setupTableColumns();
    loadBooks();
    booksTable.setItems(books);
}

    public BookManagementController(BookService bookService) {
        this.bookService = bookService;
    }



    private void setupTableColumns() {
    titleColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getTitle()));

    authorColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getAuthor()));

    isbnColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getIsbn()));

    copiesColumn.setCellValueFactory(data ->
        new SimpleIntegerProperty(data.getValue().getAvailableCopies()).asObject());
}

    private void loadBooks() {
        bookService.getAllBooks()
                .thenAccept(bookList -> {
                    books.clear();
                    books.addAll(bookList);
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Error", "Failed to load books");
                    return null;
                });
        handleAsync(bookService.getAllBooks())
                .thenAccept(bookList -> {
                    books.clear();
                    books.addAll(bookList);
                });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            loadBooks();
            return;
        }

        handleAsync(bookService.searchBooks(searchTerm))
                .thenAccept(results -> {
                    books.clear();
                    books.addAll(results);
                });
        bookService.searchBooks(searchTerm)
                .thenAccept(results -> {
                    books.clear();
                    books.addAll(results);
                })
                .exceptionally(throwable -> {
                    AlertUtil.showError("Search Error", "Failed to perform search");
                    return null;
                });
    }
    @FXML
    private void handleAddBook() {
        Dialog<BookDTO> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-book-dialog.fxml"));
            dialog.getDialogPane().setContent(loader.load());

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        TextField titleField = (TextField) dialog.getDialogPane().lookup("#titleField");
                        TextField authorField = (TextField) dialog.getDialogPane().lookup("#authorField");
                        TextField isbnField = (TextField) dialog.getDialogPane().lookup("#isbnField");
                        TextField copiesField = (TextField) dialog.getDialogPane().lookup("#copiesField");

                        if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                                isbnField.getText().isEmpty() || copiesField.getText().isEmpty()) {
                            AlertUtil.showWarning("Validation Error", "All fields are required");
                            return null;
                        }

                        BookDTO newBook = new BookDTO();
                        newBook.setTitle(titleField.getText());
                        newBook.setAuthor(authorField.getText());
                        newBook.setIsbn(isbnField.getText());
                        newBook.setAvailableCopies(Integer.parseInt(copiesField.getText()));

                        return newBook;
                    } catch (NumberFormatException e) {
                        AlertUtil.showWarning("Input Error", "Copies must be a valid number");
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(this::handleAddBook);
        } catch (IOException e) {
            AlertUtil.showError("Error", "Failed to load add book dialog");
        }
    }

    private void handleAddBook(BookDTO book) {
        bookService.addBook(book)
            .thenAccept(response -> Platform.runLater(() -> {
                AlertUtil.showInfo("Success", "Book added successfully");
                loadBooks();
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    String error = throwable.getMessage();
                    if (error.contains("ISBN")) {
                        AlertUtil.showError("Error", "ISBN already exists");
                    } else if (error.contains("validation")) {
                        AlertUtil.showError("Error", "Invalid book data");
                    } else {
                        AlertUtil.showError("Error", "Failed to add book: " + error);
                    }
                });
                return null;
            });
    }
}