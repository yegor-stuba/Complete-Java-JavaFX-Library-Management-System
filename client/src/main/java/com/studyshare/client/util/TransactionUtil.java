package com.studyshare.client.util;

import com.studyshare.client.controller.TransactionDetailsController;
import com.studyshare.common.dto.TransactionDTO;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
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
            new SimpleStringProperty(data.getValue().getBookTitle()));

        typeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType().toString()));

        dateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMATTER)));

        dueDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDueDate().format(DATE_FORMATTER)));

        statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(getTransactionStatus(data.getValue())));
    }

    public static void showTransactionDetails(TransactionDTO transaction, Runnable onComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(
                TransactionUtil.class.getResource("/fxml/transaction-details-dialog.fxml"));
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Transaction Details");
            dialog.getDialogPane().setContent(loader.load());

            TransactionDetailsController controller = loader.getController();
            controller.setTransaction(transaction);
            controller.setOnComplete(onComplete);

            dialog.showAndWait();
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not load transaction details");
        }
    }

    private static String getTransactionStatus(TransactionDTO transaction) {
        if (isOverdue(transaction.getDueDate())) {
            return "OVERDUE";
        }
        return "ACTIVE";
    }

    public static boolean isOverdue(LocalDateTime dueDate) {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }
}