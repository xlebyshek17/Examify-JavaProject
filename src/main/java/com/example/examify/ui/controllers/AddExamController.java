package com.example.examify.ui.controllers;

import com.example.examify.model.Exam;
import com.example.examify.service.ExamService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

public class AddExamController {

    public TextField examName;
    public Spinner<Integer> questionCountSpinner;
    public TextField timeLimit;
    public TableView questionTable;

    private final ExamService examService = new ExamService();
    private int examId;

    void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
        questionCountSpinner.setValueFactory(valueFactory);
    }

    @FXML
    public void addQuestion(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/add_question-view.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stage.centerOnScreen();
    }

    @FXML
    public void saveExam(ActionEvent actionEvent) throws SQLException, IOException {
        String title = examName.getText();
        int questionCount = questionCountSpinner.getValue();
        int timeLimit = Integer.parseInt(this.timeLimit.getText());

        Exam exam = new Exam();
        exam.setTitle(examName.getText());
        exam.setQuestionCount(questionCount);
        exam.setTimeLimitMinutes(timeLimit);
        examId = examService.saveExam(exam);

        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/admin_exams-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
        stage.centerOnScreen();
    }
}
