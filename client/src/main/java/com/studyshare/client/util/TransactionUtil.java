package com.studyshare.client.util;

import com.studyshare.client.controller.TransactionDetailsController;
import com.studyshare.common.dto.TransactionDTO;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class TransactionUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void setupTransactionTableColumns(
            TableColumn<TransactionDTO, String> bookTitleColumn,
            TableColumn<TransactionDTO, String> typeColumn,
            TableColumn<TransactionDTO, String> dateColumn,
            TableColumn<TransactionDTO, String> dueDateColumn,
            TableColumn<TransactionDTO, String> statusColumn) {

        bookTitleColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getBook().getTitle()));

        typeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().toString()));

        dateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTimestamp().format(DATE_FORMATTER)));

        dueDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(calculateDueDate(data.getValue())));

        statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(getTransactionStatus(data.getValue())));
    }

    private static String calculateDueDate(TransactionDTO transaction) {
        // Add 14 days to transaction timestamp for due date
        return transaction.getTimestamp().plusDays(14).format(DATE_FORMATTER);
    }

    public static String getTransactionStatus(TransactionDTO transaction) {
        LocalDateTime dueDate = transaction.getTimestamp().plusDays(14);
        if (LocalDateTime.now().isAfter(dueDate)) {
            return "OVERDUE";
        }
        return "ACTIVE";
    }
    public static void showTransactionDetails(TransactionDTO transaction, Runnable refreshCallback) {
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Transaction Details");
    dialog.setHeaderText("Transaction Information");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    grid.add(new Label("Book:"), 0, 0);
    grid.add(new Label(transaction.getBook().getTitle()), 1, 0);
    grid.add(new Label("Type:"), 0, 1);
    grid.add(new Label(transaction.getType().toString()), 1, 1);
    grid.add(new Label("Date:"), 0, 2);
    grid.add(new Label(transaction.getTimestamp().format(DATE_FORMATTER)), 1, 2);
    grid.add(new Label("Status:"), 0, 3);
    grid.add(new Label(getTransactionStatus(transaction)), 1, 3);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    dialog.showAndWait();
    if (refreshCallback != null) {
        refreshCallback.run();
    }
}
}