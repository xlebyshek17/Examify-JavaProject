package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamResultDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.ExamResult;
import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.examify.dao.ExamResultDAO.getExamTitle;

/**
 * Kontroler widoku historii egzaminów użytkownika.
 * Odpowiada za wyświetlanie listy wyników i obsługę przejścia do szczegółowego podglądu odpowiedzi.
 */
public class HistoryController {

    @FXML
    private ListView<String> examListView;

    private User user;

    /** Formatowanie czasu do wyświetlenia w liście wyników */
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Ustawia aktualnego użytkownika i ładuje jego historię egzaminów.
     *
     * @param user aktualny użytkownik
     */
    public void setUser(User user) {
        this.user = user;
        loadExams();
    }

    /**
     * Otwiera widok szczegółowej recenzji odpowiedzi dla wybranego egzaminu.
     *
     * @param examId identyfikator egzaminu
     */
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

    /**
     * Ładuje wyniki egzaminów użytkownika do listy widoku.
     * Ustawia również obsługę kliknięcia na wynik, aby wyświetlić szczegóły.
     */
    private void loadExams() {
        List<ExamResult> exams = ExamResultDAO.getExamsByUserId(user.getId());

        for (ExamResult exam : exams) {
            String line = String.format("%s - [%s] %s–%s   Wynik: %.0f",
                    getExamTitle(exam.getExamId()),
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

    /**
     * Obsługuje powrót do widoku użytkownika (StudentController).
     */
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
