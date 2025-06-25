package com.example.examify.ui.controllers;

import com.example.examify.dao.AnswerDAO;
import com.example.examify.model.Answer;
import com.example.examify.model.Question;
import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.List;

/**
 * Kontroler do przeglądania odpowiedzi użytkownika na egzaminie.
 * Wyświetla listę pytań wraz z udzielonymi odpowiedziami oraz informacją, czy odpowiedź była poprawna.
 */
public class AnswerReviewController {
    @FXML
    private ListView<String> answerListView;

    private int examId;
    private User user;

    /**
     * Ustawia dane egzaminu i użytkownika, a następnie ładuje odpowiedzi do widoku.
     *
     * @param examId identyfikator egzaminu
     * @param user obiekt użytkownika
     */
    public void setData(int examId, User user) {
        this.examId = examId;
        this.user = user;
        loadAnswers();
    }

    /**
     * Ładuje odpowiedzi z bazy dla danego egzaminu i wyświetla je w ListView.
     */
    private void loadAnswers() {
        List<Answer> answers = AnswerDAO.getAnswersByExamId(examId);

        for (Answer a : answers) {
            Question q = a.getQuestion();
            String status = a.isCorrect() ? "✅" : "❌";

            String item = String.format("%s\nTwoja odpowiedź: %s  %s\nPoprawna odpowiedź: %s",
                    q.getText(),
                    a.getAnswer(),
                    status,
                    q.getCorrectAnswer());

            answerListView.getItems().add(item);
        }
    }

    /**
     * Obsługuje powrót do widoku historii egzaminów użytkownika.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/history-view.fxml"));
            Parent root = loader.load();

            HistoryController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) answerListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
