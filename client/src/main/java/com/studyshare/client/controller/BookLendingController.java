package com.studyshare.client.controller;

import com.studyshare.client.service.BookService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.common.dto.BookDTO;
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

    public BookLendingController(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void initData(Object data) {
        if (data instanceof BookDTO) {
            this.book = (BookDTO) data;
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
        }
    }

    @FXML
    private void handleLend() {
        try {
            int duration = Integer.parseInt(durationField.getText());
            handleAsync(bookService.borrowBook(book.getBookId()))
                .thenAccept(result -> {
                    AlertUtil.showInfo("Success", "Book borrowed successfully");
                    closeDialog();
                });
        } catch (NumberFormatException e) {
            AlertUtil.showError("Error", "Please enter a valid duration");
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) titleLabel.getScene().getWindow()).close();
    }
}