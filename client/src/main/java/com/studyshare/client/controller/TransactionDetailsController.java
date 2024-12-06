package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class TransactionDetailsController extends BaseController {
    private final TransactionService transactionService;
    private final TransactionDTO transaction;
    private final Stage dialogStage;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label bookTitleLabel;
    @FXML private Label transactionTypeLabel;
    @FXML private Label dateLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label statusLabel;
    @FXML private Button completeButton;

    public TransactionDetailsController(TransactionService transactionService,
                                      TransactionDTO transaction,
                                      Stage dialogStage) {
        this.transactionService = transactionService;
        this.transaction = transaction;
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        populateTransactionDetails();
        setupCompleteButton();
    }

    private void populateTransactionDetails() {
        bookTitleLabel.setText(transaction.getBookTitle());
        transactionTypeLabel.setText(transaction.getType().toString());
        dateLabel.setText(transaction.getDate().format(DATE_FORMATTER));

        if (transaction.getDueDate() != null) {
            dueDateLabel.setText(transaction.getDueDate().format(DATE_FORMATTER));
        }

        updateStatus();
    }

    private void setupCompleteButton() {
        completeButton.setVisible(transaction.getType() == TransactionType.BORROW);
        completeButton.setDisable(transaction.isCompleted());
    }

    @FXML
    private void handleComplete() {
        handleAsync(transactionService.completeTransaction(transaction.getTransactionId()))
            .thenAccept(updatedTransaction -> {
                transaction.setCompleted(true);
                updateStatus();
                completeButton.setDisable(true);
            });
    }

    private void updateStatus() {
        String status = transaction.isCompleted() ? "Completed" : "Active";
        statusLabel.setText(status);
        statusLabel.getStyleClass().add(transaction.isCompleted() ? "completed" : "active");
    }
}
