package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.common.dto.TransactionDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class TransactionViewController {
    private static final Logger log = LoggerFactory.getLogger(TransactionViewController.class);
    private final TransactionService transactionService;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();



    public TransactionViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @FXML
    private TableView<TransactionDTO> transactionsTable;
    @FXML
    private TableColumn<TransactionDTO, LocalDateTime> timestampColumn;
    @FXML
    private TableColumn<TransactionDTO, String> actionColumn;
    @FXML
    private TableColumn<TransactionDTO, String> userColumn;
    @FXML
    private TableColumn<TransactionDTO, String> detailsColumn;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTransactions();
        setupAutoRefresh();
        transactionsTable.setItems(transactions);
    }

    private void setupTableColumns() {
        timestampColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTimestamp()));
        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getActionType()));
        userColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));
        detailsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDetails()));
    }

private void loadTransactions() {
    transactionService.getUserTransactions()
        .thenAccept(loadedTransactions -> Platform.runLater(() -> {
            transactions.clear();
            transactions.addAll(loadedTransactions);
        }))
        .exceptionally(throwable -> {
            log.error("Failed to load transactions: {}", throwable.getMessage());
            Platform.runLater(() -> AlertUtil.showError("Error",
                "Failed to load transactions"));
            return null;
        });
}
    private void setupAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(60),
            event -> Platform.runLater(this::loadTransactions)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}