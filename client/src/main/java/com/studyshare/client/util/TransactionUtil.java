package com.studyshare.client.util;

import com.studyshare.common.dto.TransactionDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class TransactionUtil {

    public static void setupTransactionTableColumns(
            TableColumn<TransactionDTO, String> timestampColumn,
            TableColumn<TransactionDTO, String> actionColumn,
            TableColumn<TransactionDTO, String> userColumn,
            TableColumn<TransactionDTO, String> bookColumn) {

        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(DateTimeUtil.formatDateTime(data.getValue().getTransactionDate())));

        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().toString()));

        userColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        bookColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBookTitle()));
    }
}