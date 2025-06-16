package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML
    private ListView<String> examListView;

    private User user;

    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public void setUser(User user) {
        this.user = user;
        loadExams();
    }

    private void openAnswerReview(int examId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/answer-review.fxml"));
            Parent root = loader.load();

            AnswerReviewController controller = loader.getController();
            controller.setData(examId, user);

            Stage stage = (Stage) examListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadExams() {
        List<Exam> exams = ExamDAO.getExamsByUserId(user.getId());

        for (Exam exam : exams) {
            String line = String.format("[%s] %s–%s   Wynik: %.0f",
                    exam.getStartTime().toLocalDate(),
                    DATE_TIME_FMT.format(exam.getStartTime()),
                    DATE_TIME_FMT.format(exam.getEndTime()),
                    exam.getScore());
            examListView.getItems().add(line);
        }

        examListView.setOnMouseClicked(event -> {
            int index = examListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < exams.size()) {
                int examId = exams.get(index).getId();
                openAnswerReview(examId);
            }
        });

    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/student-view.fxml"));
            Parent root = loader.load();

            // Przekazanie usera z powrotem do StudentController
            StudentController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) examListView.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
