package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.util.TransactionUtil;
import com.studyshare.client.util.DateTimeUtil;
import com.studyshare.common.dto.TransactionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;

public class TransactionController extends BaseController {
    private final TransactionService transactionService;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();

    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private TableColumn<TransactionDTO, String> bookTitleColumn;
    @FXML private TableColumn<TransactionDTO, String> typeColumn;
    @FXML private TableColumn<TransactionDTO, String> dateColumn;
    @FXML private TableColumn<TransactionDTO, String> dueDateColumn;
    @FXML private TableColumn<TransactionDTO, String> statusColumn;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupTableRowAction();
        loadTransactions();
    }

    private void setupTableColumns() {
        bookTitleColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBookTitle()));

        typeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().toString()));

        dateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(DateTimeUtil.formatDateTime(data.getValue().getDate())));

        dueDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(DateTimeUtil.formatDateTime(data.getValue().getDueDate())));

        statusColumn.setCellValueFactory(data -> {
            TransactionDTO transaction = data.getValue();
            String status = transaction.isCompleted() ? "Completed" :
                          DateTimeUtil.isOverdue(transaction.getDueDate()) ? "Overdue" : "Active";
            return new SimpleStringProperty(status);
        });

        setupColumnStyles();
    }

    private void setupColumnStyles() {
        statusColumn.setCellFactory(column -> new TableCell<TransactionDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("transaction-status-active",
                                            "transaction-status-overdue",
                                            "transaction-status-completed");
                } else {
                    setText(item);
                    getStyleClass().removeAll("transaction-status-active",
                                            "transaction-status-overdue",
                                            "transaction-status-completed");
                    getStyleClass().add("transaction-status-" + item.toLowerCase());
                }
            }
        });
    }

    private void setupTableRowAction() {
        transactionsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TransactionDTO selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
                if (selectedTransaction != null) {
                    TransactionUtil.showTransactionDetails(selectedTransaction, transactionService);
                }
            }
        });
    }

    @FXML
    private void loadTransactions() {
        handleAsync(transactionService.getCurrentUserTransactions())
            .thenAccept(userTransactions -> {
                transactions.clear();
                transactions.addAll(userTransactions);
                transactionsTable.setItems(transactions);
            });
    }
}