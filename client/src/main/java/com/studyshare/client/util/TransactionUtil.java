package com.studyshare.client.util;

import com.studyshare.client.controller.TransactionDetailsController;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import javafx.scene.control.Dialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class TransactionUtil {
    public static void showTransactionDetails(TransactionDTO transaction, TransactionService transactionService) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Transaction Details");

            FXMLLoader loader = new FXMLLoader(TransactionUtil.class.getResource("/fxml/transaction-details-dialog.fxml"));
            loader.setControllerFactory(param -> new TransactionDetailsController(
                transactionService,
                transaction,
                (Stage) dialog.getDialogPane().getScene().getWindow()
            ));

            dialog.getDialogPane().setContent(loader.load());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to show transaction details");
        }
    }
}
