package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;
import com.example.examify.service.ExamService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AdminExamsController {
    @FXML private TableColumn<Exam, Integer> timeLimitColumn;
    @FXML private TableColumn<Exam, String> examNameColumn;
    @FXML private TableColumn<Exam, LocalDateTime> dateColumn;
    @FXML private TableColumn<Exam, Void> actionsColumn;
    @FXML private TableView<Exam> examTable;

    private final ExamService examService = new ExamService();

    @FXML
    public void initialize() throws SQLException {
        examNameColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.30));
        dateColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.25));
        timeLimitColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.25));
        actionsColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.20));

        examNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        timeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("time_limit_minutes"));
        loadExams();

        //dodać button update and delete
    }

    private void loadExams() throws SQLException {
        List<Exam> exams = examService.getAllExams();
        examTable.getItems().setAll(exams);
    }

    @FXML
    public void addExam(ActionEvent actionEvent) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/add_exam-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
        stage.centerOnScreen();
    }
}
