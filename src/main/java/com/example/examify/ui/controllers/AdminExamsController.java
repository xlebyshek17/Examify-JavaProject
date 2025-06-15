package com.example.examify.ui.controllers;

import com.example.examify.model.Exam;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;

public class AdminExamsController {
    @FXML private TableColumn<Exam, String> examNameColumn;
    @FXML private TableColumn<Exam, LocalDateTime> dateColumn;
    @FXML private TableColumn<Exam, Void> actionsColumn;
    @FXML private TableView<Exam> examTable;

    @FXML
    public void initialize() {
        examNameColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.25));
        dateColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.35));


    }
}
