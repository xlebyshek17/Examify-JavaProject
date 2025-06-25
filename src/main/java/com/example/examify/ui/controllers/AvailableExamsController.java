package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.User;
import com.example.examify.service.ExamService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * Kontroler wyświetlający listę dostępnych egzaminów dla użytkownika.
 */
public class AvailableExamsController {
    @FXML
    private ListView<String> examListView;

    private User user;
    private List<Exam> exams;
    private final ExamService examService = new ExamService();

    /**
     * Ustawia użytkownika i ładuje listę egzaminów do wyświetlenia.
     *
     * @param user obiekt użytkownika
     * @throws SQLException gdy wystąpi problem z bazą danych
     */
    public void setUser(User user) throws SQLException {
        this.user = user;
        loadExams();
    }

    /**
     * Ładuje wszystkie dostępne egzaminy i wyświetla je w ListView.
     *
     * @throws SQLException gdy wystąpi problem z bazą danych
     */
    private void loadExams() throws SQLException {
        exams = examService.getAllExams();

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

    /**
     * Uruchamia wybrany egzamin, przełączając scenę na widok egzaminu.
     *
     * @param selectedExam egzamin do rozpoczęcia
     */
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

    /**
     * Obsługuje anulowanie i powrót do widoku głównego studenta.
     */
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
