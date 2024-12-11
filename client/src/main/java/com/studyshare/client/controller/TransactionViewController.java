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
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class TransactionViewController {
    private static final Logger log = LoggerFactory.getLogger(TransactionViewController.class);
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();
    private Pagination pagination;
    private TransactionService transactionService;
    private static final int ITEMS_PER_PAGE = 40;


    public TransactionViewController() {
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
    public void initialize(TableView<TransactionDTO> table, Pagination pagination, TransactionService service) {
        this.transactionsTable = table;
        this.pagination = pagination;
        this.transactionService = service;
        this.transactionService = transactionService;
        setupPagination();
        setupTableColumns();
        loadTransactions();
        setupAutoRefresh();
        transactionsTable.setItems(transactions);
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createPage);
        updateTotalPages();
    }

    private Node createPage(int pageIndex) {
        transactionService.getTransactions(pageIndex, ITEMS_PER_PAGE)
                .thenAccept(transactions -> Platform.runLater(() -> {
                    transactionsTable.setItems(FXCollections.observableArrayList(transactions));
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load transactions"));
                    return null;
                });
        return transactionsTable;
    }

    private void updateTotalPages() {
        transactionService.getTransactionCount()
                .thenAccept(count -> {
                    int pages = (int) Math.ceil(count / (double) ITEMS_PER_PAGE);
                    Platform.runLater(() -> pagination.setPageCount(pages));
                });
    }

    public void refresh() {
        updateTotalPages();
        pagination.setCurrentPageIndex(pagination.getCurrentPageIndex());
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