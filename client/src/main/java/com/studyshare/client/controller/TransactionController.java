package com.studyshare.client.controller;

import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.DateTimeUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.client.util.TransactionUtil;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TransactionController extends BaseController {
    private final TransactionService transactionService;
    private final UserService userService;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 20;
    private Timeline updateTimeline;

    @FXML private TableView<TransactionDTO> transactionsTable;
    @FXML private Pagination transactionPagination;
    @FXML private TableColumn<TransactionDTO, String> bookTitleColumn;
    @FXML private TableColumn<TransactionDTO, String> typeColumn;
    @FXML private TableColumn<TransactionDTO, String> dateColumn;
    @FXML private TableColumn<TransactionDTO, String> statusColumn;
    @FXML private TableColumn<TransactionDTO, String> userColumn;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.updateTimeline = new Timeline(
            new KeyFrame(Duration.seconds(30), e -> refreshTransactions())
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupPagination();
        startRealTimeUpdates();
        setupTableClickHandler();
    }

    private void setupPagination() {
        transactionPagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        transactionService.getTransactions(pageIndex, ITEMS_PER_PAGE)
            .thenAccept(pageData -> Platform.runLater(() -> {
                transactions.setAll(pageData);
                transactionsTable.setItems(transactions);
            }))
            .exceptionally(throwable -> {
                log.error("Failed to load transactions page", throwable);
                Platform.runLater(() -> AlertUtil.showError("Error", "Failed to load transactions"));
                return null;
            });
        return transactionsTable;
    }

    private void setupTableColumns() {
        bookTitleColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBook().getTitle()));
        typeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().toString()));
        dateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(DateTimeUtil.formatDateTime(data.getValue().getDate())));
        statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(TransactionUtil.getTransactionStatus(data.getValue())));
        userColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUser().getUsername()));
    }

    private void startRealTimeUpdates() {
        updateTimeline.play();
    }

    private void refreshTransactions() {
        int currentPage = transactionPagination.getCurrentPageIndex();
        createPage(currentPage);
    }

    @FXML
    private void handleRefresh() {
        refreshTransactions();
    }

    private void setupTableClickHandler() {
        transactionsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TransactionDTO selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
                if (selectedTransaction != null) {
                    TransactionUtil.showTransactionDetails(selectedTransaction, this::refreshTransactions);
                }
            }
        });
    }



    public void stop() {
        updateTimeline.stop();
    }
}