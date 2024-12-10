package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.client.util.TransactionUtil;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TransactionController extends BaseController {
    private final TransactionService transactionService;
    private final UserService userService;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private TableColumn<TransactionDTO, String> bookTitleColumn;
    @FXML private TableColumn<TransactionDTO, String> typeColumn;
    @FXML private TableColumn<TransactionDTO, String> dateColumn;
    @FXML private TableColumn<TransactionDTO, String> dueDateColumn;
    @FXML private TableColumn<TransactionDTO, String> statusColumn;

    @FXML
    private void initialize() {
        setupTableColumns();
        loadTransactions();
        setupTableClickHandler();
    }

    private void setupTableColumns() {
        TransactionUtil.setupTransactionTableColumns(
            bookTitleColumn,
            typeColumn,
            dateColumn,
            dueDateColumn,
            statusColumn
        );
    }

    private Long getCurrentUserId() {
        CompletableFuture<UserDTO> userFuture = userService.getCurrentUser();
        UserDTO currentUser = userFuture.join();
        return currentUser.getUserId();
    }




    private void loadTransactions() {
        transactionService.getUserTransactions()
                .thenAccept(transactions -> Platform.runLater(() -> {
                    transactionsTable.getItems().setAll(transactions);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error",
                            "Failed to load transactions: " + throwable.getMessage()));
                    return null;
                });
    }

    private void setupTableClickHandler() {        transactionsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TransactionDTO selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
                if (selectedTransaction != null) {
                    TransactionUtil.showTransactionDetails(selectedTransaction, this::loadTransactions);
                }
            }
        });
    }

   private void updateTransactionStatus(List<TransactionDTO> transactions) {
    transactions.forEach(transaction -> {
        String status = transaction.isActive() ? "Active" : "Completed";
        String type = transaction.getType().toString();

        Platform.runLater(() -> {
            statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Completed"));
            typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType().toString()));
            bookTitleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        });
    });
}
}