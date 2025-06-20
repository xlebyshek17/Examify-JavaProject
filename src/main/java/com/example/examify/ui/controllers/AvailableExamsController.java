package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class AvailableExamsController {

    @FXML
    private ListView<String> examListView;

    private User user;
    private List<Exam> exams;

    public void setUser(User user) {
        this.user = user;
        loadExams();
    }

    private void loadExams() {
        exams = ExamDAO.getAllExams();

        for (Exam exam : exams) {
            String line = String.format("%s — pytań: %d — czas: %d min",
                    exam.getTitle(),
                    exam.getQuestionCount(),
                    exam.getTime_limit_minutes());
            examListView.getItems().add(line);
        }

        examListView.setOnMouseClicked(event -> {
            int index = examListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < exams.size()) {
                Exam selectedExam = exams.get(index);
                startExam(selectedExam);
            }
        });
    }

    private void startExam(Exam selectedExam) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/examify/fxml/exam-view.fxml"));
            Parent root = loader.load();

            ExamController controller = loader.getController();
            controller.setUserAndExam(user, selectedExam);

            Stage stage = (Stage) examListView.getScene().getWindow();
            stage.setScene(new Scene(root));

            controller.startExam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/examify/fxml/student-view.fxml"));
            Parent root = loader.load();

            StudentController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) examListView.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
