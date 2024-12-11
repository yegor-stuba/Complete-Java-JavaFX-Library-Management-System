package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.impl.TransactionServiceImpl;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.enums.TransactionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BookLendingController extends BaseController {
    private final BookService bookService;
    private BookDTO book;

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private TextField durationField;
    private TransactionServiceImpl transactionService;


    public BookLendingController(BookService bookService) {
        this.bookService = bookService;
    }


    public void initData(Object data) {
        if (data instanceof BookDTO) {
            this.book = (BookDTO) data;
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
        }
    }
@FXML
private void handleLend() {
    handleAsync(bookService.borrowBook(book.getBookId()))
        .thenCompose(borrowedBook ->
            transactionService.createTransaction(book.getBookId(), TransactionType.BORROW))
        .thenAccept(transaction -> {
            Platform.runLater(() -> {
                AlertUtil.showInfo("Success", "Book borrowed successfully");
                closeDialog();
            });
        })
        .exceptionally(throwable -> {
            Platform.runLater(() ->
                AlertUtil.showError("Error", "Failed to borrow book: " + throwable.getMessage()));
            return null;
        });
}
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) titleLabel.getScene().getWindow()).close();
    }
}