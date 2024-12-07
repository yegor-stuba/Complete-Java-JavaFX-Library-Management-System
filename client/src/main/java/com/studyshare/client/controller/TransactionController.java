package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.TransactionUtil;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.concurrent.CompletableFuture;

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

    @FXML
    private void loadTransactions() {
        transactionService.getUserTransactions(getCurrentUserId())
            .thenAccept(transactionList -> {
                transactions.clear();
                transactions.addAll(transactionList);
                transactionsTable.setItems(transactions);
            })
            .exceptionally(throwable -> {
                AlertUtil.showError("Error", "Failed to load transactions");
                return null;
            });
    }

    private void setupTableClickHandler() {
        transactionsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TransactionDTO selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
                if (selectedTransaction != null) {
                    TransactionUtil.showTransactionDetails(selectedTransaction, this::loadTransactions);
                }
            }
        });
    }
}